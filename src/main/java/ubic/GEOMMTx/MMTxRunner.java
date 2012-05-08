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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO Document Me
 * 
 * @author lfrench
 * @version $Id$
 */
public class MMTxRunner {
    protected static Log log = LogFactory.getLog( MMTxRunner.class );

    private CacheManager cacheManager;

    private Cache memoryOnlyCache;

    /**
     * the business end
     */
    private MMTxAPI mmtxAPI;

    private int scoreThreshold;

    /**
     * @param scoreThreshold
     * @param options
     */
    public MMTxRunner( CacheManager cacheManager, int scoreThreshold, String[] options ) {
        this.scoreThreshold = scoreThreshold;

        if ( cacheManager != null ) {
            this.cacheManager = cacheManager;
        } else {
            this.cacheManager = CacheManager.create();
        }

        memoryOnlyCache = this.cacheManager.getCache( "mmtxCache" );
        if ( memoryOnlyCache == null ) {
            memoryOnlyCache = new Cache( "mmtxCache", 25, false, false, 5000, 1500 );
            this.cacheManager.addCache( memoryOnlyCache );
        }

        // try MMTxAPILite?

        log.info( StringUtils.join( options, " " ) );

        try {
            mmtxAPI = new MMTxAPI( options );
        } catch ( Exception e ) {
            log.error( "**** MMTx Initialization failed : " + e.getMessage() + " ****" );
            throw new RuntimeException( e );
        }

    }

    /**
     * 
     */
    public void clearCache() {
        memoryOnlyCache.removeAll();
    }

    /**
     * @param phrase
     * @return
     */
    public Collection<Candidate> getConcepts( Phrase phrase ) {
        Collection<Candidate> results = new ArrayList<Candidate>();

        List<?> finalMappings = phrase.getFinalMappings();

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

    /**
     * @param text
     * @return
     */
    public Collection<Candidate> getConcepts( String text ) {
        Collection<Candidate> results = new ArrayList<Candidate>();

        for ( Phrase p : getPhrases( text ) ) {
            results.addAll( getConcepts( p ) );
        } // end for
        return results;
    }

    /**
     * @param text
     * @return
     */
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
            doc = mmtxAPI.processDocument( text );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
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
