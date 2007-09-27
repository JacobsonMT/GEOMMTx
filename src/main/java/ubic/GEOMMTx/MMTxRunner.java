package ubic.GEOMMTx;

import gov.nih.nlm.nls.mmtx.MMTxAPI;
import gov.nih.nlm.nls.nlp.textfeatures.Candidate;
import gov.nih.nlm.nls.nlp.textfeatures.Document;
import gov.nih.nlm.nls.nlp.textfeatures.FinalMapping;
import gov.nih.nlm.nls.nlp.textfeatures.Phrase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MMTxRunner {
    private static final long serialVersionUID = 1L;

    private MMTxAPI MMTx;
    private int scoreThreshold;

    public MMTxRunner() {
        this( new String[] {} );
    }

    public MMTxRunner( String[] options ) {
        this( 850, options );
    }

    public MMTxRunner( int scoreThreshold, String[] options ) {
        this.scoreThreshold = scoreThreshold;
        try {
            MMTx = new MMTxAPI( options );
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        }
    }

    public List<Phrase> getPhrases( String text ) {
        Document doc = null;
        List<Phrase> results = new ArrayList<Phrase>();

        // MMTX processing
        try {
            doc = MMTx.processDocument( text );
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        }

        if ( doc.getPhrases() == null ) return results;

        for ( Object phraseObj : doc.getPhrases() ) {
            results.add( ( Phrase ) phraseObj );
        } // end for

        return results;
    }

    public Collection<Candidate> getConcepts( Phrase phrase ) {
        Collection<Candidate> results = new ArrayList<Candidate>();

        List finalMappings = phrase.getFinalMappings();

        // somtimes finalMappings is null, guess this happens when it can't find anything
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

    public int getScoreThreshold() {
        return scoreThreshold;
    }

    public void setScoreThreshold( int scoreThreshold ) {
        this.scoreThreshold = scoreThreshold;
    }

}
