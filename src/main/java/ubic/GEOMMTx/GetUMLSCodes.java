package ubic.GEOMMTx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GetUMLSCodes {
    private String location;
    private Map<String, Set<UMLSSourceCode>> SABMap;

    protected static Log log = LogFactory.getLog( GetUMLSCodes.class );
    
    public GetUMLSCodes() {
        this( SetupParameters.config.getString( "gemma.annotator.cui_code_loc" ) );
    }
    
    //location of MRCONSO.RRF
    public GetUMLSCodes( String location ) {
        this.location = location;
        loadUMLSCodeMap();
    }
    
    public static void main( String[] args ) {
        GetUMLSCodes umlscodez = new GetUMLSCodes();
        System.out.println("Size:"+umlscodez.getUMLSCodeMap().size());
    }

    public void loadUMLSCodeMap() {
        int noCodeCount = 0;
        SABMap = new HashMap<String, Set<UMLSSourceCode>>();
        try {
            PrintWriter fOut = new PrintWriter( new FileWriter( "temp.txt" ) );
            BufferedReader f = new BufferedReader( new FileReader( location ) );
            String line;
            String[] tokens;
            while ( ( line = f.readLine() ) != null ) {
                tokens = line.split( "[|]" );
                String CUI = tokens[0];
                // SAB is source vocab
                String SAB = tokens[11];
                String CODE = tokens[13];
                if ( !SABMap.containsKey( CUI ) ) {
                    SABMap.put( CUI, new HashSet<UMLSSourceCode>() );
                }
                // add it to the set associated with this CUI
                SABMap.get( CUI ).add( new UMLSSourceCode( SAB, CODE ) );

                if ( CODE.equals( "NOCODE" ) ) noCodeCount++;
                if ( SAB.startsWith( "MSH" ) || SAB.startsWith( "SNO" ) || CODE.equals( "NOCODE" ) ) continue;
                // fOut.println( CUI + "->" + SAB + ":" + CODE );
                // System.out.println(SAB + "=" + nickName);
            }
            f.close();
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        }
        log.info( "Loaded UMLS Codes, nocode entries=" + noCodeCount );
    }

    public Map<String, Set<UMLSSourceCode>> getUMLSCodeMap() {
        return SABMap;
    }
}
