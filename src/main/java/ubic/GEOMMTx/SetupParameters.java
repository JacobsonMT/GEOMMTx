package ubic.GEOMMTx;

public class SetupParameters {
    // the place where cuisui_sourceinfo.txt is
    public static String CUI_SOURCE_LOC = "/grp/java/apps/mmtx2.4c/nls/mmtx/data/2006/mmtx";

    //location of MRCONSO file that has the source codes for the concepts
    public static String CUI_CODE_LOC = "/home/leon/Desktop/mmtx/MRCONSO.RRF";

    //threshold for mmtx score (max 1000)
    public static int scoreThreshold = 850;
    
    // the UMLS version that MMTx is using
    public static String UMLS_VERSION = "2006AA";
    
    //Options to pass to MMTx
    public static String[] mmtxOptions = new String[] { "--an_derivational_variants", "--no_acros_abbrs" };

}
