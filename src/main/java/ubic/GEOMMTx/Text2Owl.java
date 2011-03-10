/*
 * The GEOMMTx project
 * 
 * Copyright (c) 2007 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.GEOMMTx;

import static ubic.GEOMMTx.Vocabulary.hasCUI;
import static ubic.GEOMMTx.Vocabulary.hasMention;
import static ubic.GEOMMTx.Vocabulary.hasPhrase;
import static ubic.GEOMMTx.Vocabulary.hasSUI;
import static ubic.GEOMMTx.Vocabulary.hasScore;
import static ubic.GEOMMTx.Vocabulary.mappedTerm;
import static ubic.GEOMMTx.Vocabulary.spanEnd;
import static ubic.GEOMMTx.Vocabulary.spanStart;
import gov.nih.nlm.nls.nlp.textfeatures.Candidate;
import gov.nih.nlm.nls.nlp.textfeatures.Phrase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.CacheManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.mappers.DiseaseOntologyMapper;
import ubic.GEOMMTx.mappers.FMALiteMapper;
import ubic.GEOMMTx.mappers.NIFSTDMapper;
import ubic.GEOMMTx.util.SetupParameters;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Performs the mapping of free text to terms from the ontologies.
 * 
 * @author lfrench
 * @version $Id$
 */
public class Text2Owl {

    private MMTxRunner mmtx;

    // things that turn concepts into URI's
    private Collection<CUIMapper> CUIMappers;

    // gets CUIs to codes
    private GetUMLSCodes umlscodes;

    protected static Log log = LogFactory.getLog( Text2Owl.class );

    // stores CUIs to codes
    private Map<String, Set<UMLSSourceCode>> codeMap;

    /**
     * 
     */
    public Text2Owl() {
        this( null );
    }

    /**
     * Initialization includes setting up configured mappers, score threshold, and mmtx options.
     */
    public Text2Owl( CacheManager cacheManager ) {

        this( cacheManager, SetupParameters.getInt( "geommtx.annotator.scoreThreshold" ), SetupParameters
                .getStringArray( "geommtx.annotator.mmtxOptions" ) );

        if ( SetupParameters.getBoolean( "geommtx.map.nifstd", true ) ) {
            addMapper( new NIFSTDMapper() );
        }

        if ( SetupParameters.getBoolean( "geommtx.map.fma", true ) ) {
            addMapper( new FMALiteMapper() );
        }
        if ( SetupParameters.getBoolean( "geommtx.map.disease", true ) ) {
            addMapper( new DiseaseOntologyMapper() );
        }

    }

    /**
     * @param threshold
     * @param options
     */
    public Text2Owl( CacheManager cacheManager, int threshold, String[] options ) {
        log.info( "Initializing MMTx..." );
        mmtx = new MMTxRunner( cacheManager, threshold, options );
        CUIMappers = new ArrayList<CUIMapper>();
        umlscodes = new GetUMLSCodes( true );
        codeMap = umlscodes.getUMLSCodeMap();
        log.info( "... ready" );
    }

    /**
     * Add a mapper at runtime
     * 
     * @param mapper
     */
    public void addMapper( CUIMapper mapper ) {
        CUIMappers.add( mapper );
    }

    /**
     * 
     */
    public void clearCache() {
        mmtx.clearCache();
    }

    /**
     * The main entry point.
     * 
     * @param text
     * @param root
     * @return
     */
    public Model processText( String text, Resource root ) {
        Model model;
        model = root.getModel();

        Collection<Candidate> candidates;

        // phrases, or chunks of the text
        List<Phrase> phrases = mmtx.getPhrases( text );

        // log.debug( phrases.size() + " phrases" );

        for ( Phrase p : phrases ) {

            candidates = mmtx.getConcepts( p );
            candidates = new HashSet<Candidate>( candidates );

            Resource phraseNode = null;

            // add the span?
            // add the creator/processor?

            // several candidates will be found in each phrase
            for ( Candidate c : candidates ) {
                Resource mentionNode = null;
                String CUI = c.getCUI();

                for ( CUIMapper mapper : CUIMappers ) {
                    Set<String> URIs = mapper.convert( CUI, codeMap.get( CUI ) );
                    if ( URIs != null ) {
                        for ( String URI : URIs ) {
                            if ( mentionNode == null ) {
                                mentionNode = model.createResource();
                            }
                            log.debug( "Found " + URI );
                            mentionNode.addProperty( mappedTerm, model.createResource( URI ) );
                        }
                    }
                }

                // only connect this mention to root if we got at least one URI
                // hit
                if ( mentionNode != null ) {
                    if ( phraseNode == null ) { // we need a phrase node
                        phraseNode = model.createResource();
                    }
                    mentionNode.addProperty( hasCUI, model.createResource( "http://www.purl.org/umls/umls#" + CUI ) );
                    mentionNode.addLiteral( RDFS.label, c.getConcept() );

                    mentionNode.addProperty( hasSUI, model.createResource( "http://www.purl.org/umls/umls#"
                            + c.getSUI() ) );
                    mentionNode.addLiteral( hasScore, c.getFinalScore() );
                    phraseNode.addProperty( hasMention, mentionNode );
                }
            }

            // only connect this phrase to root if we got at least one mention
            if ( phraseNode != null ) {
                // add the text
                phraseNode.addLiteral( RDFS.label, p.getOriginalString() );
                // add span
                phraseNode.addLiteral( spanStart, p.getSpan().getBeginCharacter() );
                phraseNode.addLiteral( spanEnd, p.getSpan().getEndCharacter() );

                // add the link
                root.addProperty( hasPhrase, phraseNode );
            }
        }
        return model;
    }
}
