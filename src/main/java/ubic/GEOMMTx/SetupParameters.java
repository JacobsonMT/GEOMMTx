package ubic.GEOMMTx;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.ibm.icu.text.Normalizer;

import ubic.GEOMMTx.evaluation.CUIIRIPair;

public class SetupParameters {

    static String filename = "Annotator.properties";

    // just grab the config and call get to get the parameter value
    public static Configuration config;

    // The few rejected CUI to IRI/URL pairings as determined by human curation
    public static HashSet<CUIIRIPair> rejectedCUIIRIPairs;

    static {
        try {
            config = new PropertiesConfiguration( filename );
        } catch ( Exception e ) {
            System.out.println( "Could not load " + filename );
            System.exit( 1 );
        }

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

    public static void main( String argsp[] ) {
        Iterator i = config.getKeys( "gemma.annotator" );

        while ( i.hasNext() ) {
            String key = ( String ) i.next();
            System.out.println( key + ":" + config.getString( key ) );
        }

        System.out.println( "options[0] :" + config.getStringArray( "gemma.annotator.mmtxOptions" )[0] );
        System.out.println( "options[1] :" + config.getStringArray( "gemma.annotator.mmtxOptions" )[1] );
    }

}
