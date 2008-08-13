package ubic.GEOMMTx;

import java.util.HashSet;

import ubic.GEOMMTx.evaluation.CUIIRIPair;

public class SetupParameters {
    // the place where cuisui_sourceinfo.txt is
    public static String CUI_SOURCE_LOC = "/grp/java/apps/mmtx2.4c/nls/mmtx/data/2006/mmtx";

    // location of MRCONSO file that has the source codes for the concepts
    public static String CUI_CODE_LOC = "/home/leon/Desktop/mmtx/MRCONSO.RRF";

    // threshold for mmtx score (max 1000)
    // this value is also stored in the RDF
    public static int scoreThreshold = 0;// 850;

    // the UMLS version that MMTx is using
    public static String UMLS_VERSION = "2006AA";
    
    //RDF file that stores Gemma experiment titles
    public static String gemmaTitles = "GemmaTitles.rdf";

    // Options to pass to MMTx
    public static String[] mmtxOptions = new String[] { "--an_derivational_variants", "--no_acros_abbrs" };

    // The many rejected phrase to CUI pairings as determined by human curation
    public static String CUISUIEvaluationFile = "./FinalEvaluations/Mapping from Phrase to CUI.xls";

    // the IRI's that are deemed too generic like house mice, cell, DNA and RNA
    public static String uselessFrequentURLsFile = "./FinalEvaluations/UselessFrequentURLs.txt";

    // The few rejected CUI to IRI/URL pairings as determined by human curation
    public static HashSet<CUIIRIPair> rejectedCUIIRIPairs;

    static {
        rejectedCUIIRIPairs = new HashSet<CUIIRIPair>();
        rejectedCUIIRIPairs.add( new CUIIRIPair( "http://www.purl.org/umls/umls#C0001162",
                "http://purl.org/obo/owl/FMA#FMA_50869" ) );
        rejectedCUIIRIPairs.add( new CUIIRIPair( "http://www.purl.org/umls/umls#C0001271",
                "http://purl.org/obo/owl/FMA#FMA_67843" ) );
        rejectedCUIIRIPairs.add( new CUIIRIPair( "http://www.purl.org/umls/umls#C0001625",
                "http://purl.org/obo/owl/FMA#FMA_9604" ) );
        rejectedCUIIRIPairs.add( new CUIIRIPair( "http://www.purl.org/umls/umls#C0001655",
                "http://purl.org/obo/owl/FMA#FMA_74639" ) );
    }

}
