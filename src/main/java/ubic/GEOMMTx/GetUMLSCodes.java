package ubic.GEOMMTx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GetUMLSCodes {
    private String location;

    public GetUMLSCodes() {
        this( "C:\\Documents and Settings\\lfrench\\Desktop\\UMLS2007AA\\SecondSubset\\2007AA\\META\\MRCONSO.RRF" );
    }

    public GetUMLSCodes( String location ) {
        this.location = location;
    }

    public static void main( String[] args ) {
        GetUMLSCodes umlscodez = new GetUMLSCodes();
        umlscodez.getUMLSCodeMap();
    }

    public Map<String, Set<UMLSSourceCode>> getUMLSCodeMap() {
        int noCodeCount = 0;
        Map<String, Set<UMLSSourceCode>> SABMap = new HashMap<String, Set<UMLSSourceCode>>();
        try {
            PrintWriter fOut = new PrintWriter( new FileWriter( "temp.txt" ) );
            BufferedReader f = new BufferedReader( new FileReader( location ) );
            String line;
            String[] tokens;
            while ( ( line = f.readLine() ) != null ) {
                tokens = line.split( "[|]" );
                String CUI = tokens[0];
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
        System.out.println( "Loaded UMLS Codes, nocodes entries=" + noCodeCount );
        return SABMap;
    }
}
