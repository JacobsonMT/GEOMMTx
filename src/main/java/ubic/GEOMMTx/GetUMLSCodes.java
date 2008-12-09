package ubic.GEOMMTx;

import gov.nih.nlm.kss.api.KSSRetrieverV5_0;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.Naming;
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

    // location of MRCONSO.RRF
    public GetUMLSCodes( String location ) {
        this.location = location;
        loadUMLSCodeMap();
    }

    public static void main( String[] args ) throws Exception {
        GetUMLSCodes umlscodez = new GetUMLSCodes();
        Map<String, Set<UMLSSourceCode>> map = umlscodez.getUMLSCodeMap();
        Set<String> meshCodes = new HashSet<String>();
        System.out.println( map.get( "C0814999" ) );

    }

    public static void MeSHTest( String[] args ) throws Exception {
        GetUMLSCodes umlscodez = new GetUMLSCodes();
        Map<String, Set<UMLSSourceCode>> map = umlscodez.getUMLSCodeMap();
        Set<String> meshCodes = new HashSet<String>();
        System.out.println( map.get( "C0030567" ) );

        String name = "//umlsks.nlm.nih.gov/KSSRetriever";
        KSSRetrieverV5_0 retriever = ( KSSRetrieverV5_0 ) Naming.lookup( name );

        char[] result = retriever.getSemanticType( "2008AA", "C0001175" );
        String conceptName = new String( result );
        System.out.println( "Concept Name in XML: " + conceptName );

        result = retriever.getSemanticType( "2008AA", "C0001175" );
        conceptName = new String( result );
        System.out.println( "Concept Name in XML: " + conceptName );

        int bothC = 0;
        int meshC = 0;
        int omimC = 0;

        int i = 0;
        for ( String CUI : map.keySet() ) {
            if ( i++ % 10000 == 0 ) System.out.println( ( i - 1.0 ) / 1170000.0 );

            boolean both = false;
            boolean mesh = false;
            boolean omim = false;

            for ( UMLSSourceCode code : map.get( CUI ) ) {
                if ( code.getSource().startsWith( "MSH" ) ) {
                    mesh = true;
                    meshCodes.add( code.getCode() );
                }
                if ( code.getSource().startsWith( "OMIM" ) && !code.getCode().contains( "." ) ) {
                    omim = true;
                }
            }

            if ( omim ) {
                char[] result2 = retriever.getSemanticType( "2008AA", CUI );
                String conceptName2 = new String( result2 );
                omim = ( conceptName2.contains( "T047" ) || conceptName2.contains( "T019" ) );
            }

            both = mesh && omim;
            if ( both ) bothC++;
            if ( mesh ) meshC++;
            if ( omim ) omimC++;
        }
        System.out.println( "OMIM=" + omimC );
        System.out.println( "Mesh=" + meshC );
        System.out.println( "both=" + bothC );
        System.out.println( "Unique Mesh codes=" + meshCodes.size() );
        System.out.println( "Size:" + map.size() );

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
                String SAB = null, CODE = null;
                // SAB is source vocab
                try {
                    SAB = tokens[11];
                    CODE = tokens[13];
                } catch ( Exception e ) {
                    System.out.println( line );
                    e.printStackTrace();
                    System.exit( 1 );
                }
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
