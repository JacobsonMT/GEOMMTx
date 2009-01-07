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

import gov.nih.nlm.nls.mmtx.MMTxAPI;
import gov.nih.nlm.nls.nlp.textfeatures.Candidate;
import gov.nih.nlm.nls.nlp.textfeatures.Document;
import gov.nih.nlm.nls.nlp.textfeatures.FinalMapping;
import gov.nih.nlm.nls.nlp.textfeatures.Phrase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MMTxRunner {
    private static final long serialVersionUID = 1L;

    protected static Log log = LogFactory.getLog( MMTxRunner.class );

    private MMTxAPI MMTx;

    private int scoreThreshold;

    private Cache memoryOnlyCache;

    // public MMTxRunner(String[] options) {
    // this(, options);
    // }

    public MMTxRunner() {
        this( 0, new String[] {} );
    }

    public MMTxRunner( int scoreThreshold, String[] options ) {
        this.scoreThreshold = scoreThreshold;
        CacheManager singletonManager = CacheManager.create();

        memoryOnlyCache = singletonManager.getCache( "realCache" );
        if ( memoryOnlyCache == null ) {
            memoryOnlyCache = new Cache( "realCache", 25, false, false, 5000, 1500 );
            singletonManager.addCache( memoryOnlyCache );
        }

        try {
            // try MMTxAPILite?
            MMTx = new MMTxAPI( options );
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        }
    }

    public void clearCache() {
        memoryOnlyCache.removeAll();
    }

    public Collection<Candidate> getConcepts( Phrase phrase ) {
        Collection<Candidate> results = new ArrayList<Candidate>();

        List finalMappings = phrase.getFinalMappings();

        // somtimes finalMappings is null, guess this happens when it can't find
        // anything
        if ( finalMappings == null ) return results;

        // go through the mappings
        for ( Object mappingIterator : finalMappings ) {
            FinalMapping aMapping = ( FinalMapping ) mappingIterator;

            // go through the concepts which are Candidates
            for ( Object cObj : aMapping.getConcepts() ) {
                Candidate concept = ( Candidate ) cObj;
                if ( concept.getFinalScore() > scoreThreshold ) {
                    results.add( concept );
                }
            } // end for
        } // end for
        return results;
    }

    public Collection<Candidate> getConcepts( String text ) {
        Document doc = null;
        Collection<Candidate> results = new ArrayList<Candidate>();

        for ( Phrase p : getPhrases( text ) ) {
            results.addAll( getConcepts( p ) );
        } // end for
        return results;
    }

    public List<Phrase> getPhrases( String text ) {
        // check to see if we done it before
        Element element = memoryOnlyCache.get( text );
        if ( element != null ) {
            // log.info("using phrase cache");
            return ( List<Phrase> ) ( memoryOnlyCache.get( text ).getObjectValue() );
        }

        Document doc = null;
        List<Phrase> results = new ArrayList<Phrase>();

        // MMTX processing
        try {
            doc = MMTx.processDocument( text );
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        }
        // doc.getSentences()

        if ( doc.getPhrases() == null ) return results;

        for ( Object phraseObj : doc.getPhrases() ) {
            results.add( ( Phrase ) phraseObj );
        } // end for

        memoryOnlyCache.put( new Element( text, results ) );
        return results;
    }

    public int getScoreThreshold() {
        return scoreThreshold;
    }

    public void setScoreThreshold( int scoreThreshold ) {
        this.scoreThreshold = scoreThreshold;
    }
}
