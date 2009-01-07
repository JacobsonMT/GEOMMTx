/*
 * The Gemma project
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
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.mappers.BirnLexMapper;
import ubic.GEOMMTx.mappers.DiseaseOntologyMapper;
import ubic.GEOMMTx.mappers.FMALiteMapper;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

public class Text2Owl {
    // short main test
    public static void main( String args[] ) throws Exception {
        long time = System.currentTimeMillis();
        Text2Owl text2Owl = new Text2Owl( 850, new String[] { "--an_derivational_variants", "--no_acros_abbrs" } );
        text2Owl.addMapper( new FMALiteMapper() );
        text2Owl.addMapper( new DiseaseOntologyMapper() );
        text2Owl.addMapper( new BirnLexMapper() );

        Model model = ModelFactory.createDefaultModel();
        Resource root = model.createResource( "http://www.bioinformatics.ubca.ca/testing/umls#Sample" );

        // model = text2Owl.processText( "Expression data from adult laboratory mouse brain hemispheres", root );
        // model = text2Owl.processText( "mouse brain hemispheres", root );
        model = text2Owl.processText( "brain", root );
        log.info( "here" );
        // Hippocampus CA3 acute
        // model = text2Owl.processText( "Sample # Group OD 260/280 RNA, ug/ul Actb Ct Chip 1 PregPBS 2.0 0.63 13.8 a 2
        // ", root );
        // log.info( "here" );
        // model = text2Owl.processText( "Sample # Group OD 260/280 RNA, ug/ul Actb Ct Chip 1 PregPBS 2.0 0.63 13.8 a 2
        // PregPBS 2.0 0.647 13.3 j 3 PregPBS 2.1 0.612 13.4 l 4 ", root );
        // log.info( "here" );
        // model = text2Owl.processText( "Sample # Group OD 260/280 RNA, ug/ul Actb Ct Chip 1 PregPBS 2.0 0.63 13.8 a 2
        // PregPBS 2.0 0.647 13.3 j 3 PregPBS 2.1 0.612 13.4 l 4 PregPBS 2.0 0.575 13.8 m 6 PregTiO 2.1 0.585 14.3 n 8
        // PregTiO 2.1 0.458 13.8 o 9 NormPBS 2.0 0.627 13.3 p 11", root );
        // log.info( "here" );
        // model = text2Owl.processText( "0.575 13.8 m 6 PregTiO 2.1 0.585 14.3 n 8 PregTiO 2.1 0.458 13.8 o 9 NormPBS
        // 2.0 0.627 13.3 p 11 NormPBS 2.0 0.714 13.4 b 12 NormPBS 2.1 0.462 13.8 c 13 NormTiO 2.0 0.572 15.6 d 14
        // NormTiO 2.1 0.598 13.1 e 15 NormTiO 2.1 0.682 13.6 f 16 NormTiO 2.1 0.654 13.6 g 17 PregTiO 2.1 0.586 13.7 h
        // 18 PregTiO 2.0 0.833 12.8 i 20 NormPBS 2.0 0.804 12.5 k Source GEO sample is GSM180989 Last updated
        // (according to GEO): Apr 12 2007", root );
        // log.info( "here" );
        // model = text2Owl.processText( "Sample # Group OD 260/280 RNA, ug/ul Actb Ct Chip 1 PregPBS 2.0 0.63 13.8 a 2
        // PregPBS 2.0 0.647 13.3 j 3 PregPBS 2.1 0.612 13.4 l 4 PregPBS 2.0 0.575 13.8 m 6 PregTiO 2.1 0.585 14.3 n 8
        // PregTiO 2.1 0.458 13.8 o 9 NormPBS 2.0 0.627 13.3 p 11 NormPBS 2.0 0.714 13.4 b 12 NormPBS 2.1 0.462 13.8 c
        // 13 NormTiO 2.0 0.572 15.6 d 14 NormTiO 2.1 0.598 13.1 e 15 NormTiO 2.1 0.682 13.6 f 16 NormTiO 2.1 0.654 13.6
        // g 17 PregTiO 2.1 0.586 13.7 h 18 PregTiO 2.0 0.833 12.8 i 20 NormPBS 2.0 0.804 12.5 k Source GEO sample is
        // GSM180989 Last updated (according to GEO): Apr 12 2007", root );
        // model = text2Owl.processText( "", root );

        /*
         * model = text2Owl .processText( "Serum here. Estrogen receptor status in breast cancer is associated with
         * remarkably distinct gene expression patterns. Serum at end.", root ); model = text2Owl .processText( "Serum
         * here. Estrogen receptor status in breast cancer is associated with remarkably distinct gene expression
         * patterns. Serum at end.", root ); /* model = text2Owl .processText( "Serum here. Estrogen receptor status in
         * breast cancer is associated with remarkably distinct gene expression patterns. Serum at end.", root ); model
         * = text2Owl .processText( "Serum here. Estrogen receptor status in breast cancer is associated with remarkably
         * distinct gene expression patterns. Serum at end.", root ); model = text2Owl.processText( "Breast cancer",
         * root ); model = text2Owl .processText( "Serum here. Estrogen receptor status in breast cancer is associated
         * with remarkably distinct gene expression patterns. Serum at end.", root );
         */

        model.write( System.out, "N-TRIPLE" );
        // System.out.println( "----------------------" );
        // model.write( new FileWriter( "RDFfromText2Owl.main.rdf" ) );
        System.out.println( "time:" + ( System.currentTimeMillis() - time ) );
    }

    private MMTxRunner mmtx;

    // things that turn concepts into URI's
    private Collection<CUIMapper> CUIMappers;

    // gets CUI's to codes
    private GetUMLSCodes umlscodes;

    protected static Log log = LogFactory.getLog( Text2Owl.class );

    // stores CUI's to codes
    private Map<String, Set<UMLSSourceCode>> codeMap;

    public Text2Owl() {
        this( SetupParameters.config.getInt( "gemma.annotator.scoreThreshold" ), SetupParameters.config
                .getStringArray( "gemma.annotator.mmtxOptions" ) );
    }

    public Text2Owl( int threshold, String[] options ) {
        mmtx = new MMTxRunner( threshold, options );
        CUIMappers = new ArrayList<CUIMapper>();
        umlscodes = new GetUMLSCodes();
        codeMap = umlscodes.getUMLSCodeMap();

        // call the static constructor of data
        // ConceptToSource.main( null );
        // log.info( "Done init for UMLSCodes" );

    }

    public void addMapper( CUIMapper mapper ) {
        if ( CUIMappers.contains( mapper ) ) return;
        CUIMappers.add( mapper );
    }

    public void clearCache() {
        mmtx.clearCache();
    }

    public Model processText( String text, Resource root ) {
        Model model;
        model = root.getModel();

        Collection<Candidate> candidates;

        // phrases, or chunks of the text
        for ( Phrase p : mmtx.getPhrases( text ) ) {

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
