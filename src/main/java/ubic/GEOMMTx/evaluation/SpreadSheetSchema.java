package ubic.GEOMMTx.evaluation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is to determine what information is put in the spreadsheet and its column position
 * 
 * @author lfrench
 */
public class SpreadSheetSchema {
    private static Map<String, Integer> positions;
    protected static Log log = LogFactory.getLog( SpreadSheetSchema.class );

    // ?geoLabel ?description ?CUI ?SUI ?mapping ?phrase ?mentionLabel
    static {
        positions = new HashMap<String, Integer>();
//        positions.put( "geoLabel", 0 );
        //positions.put( "description", 1 );
        positions.put( "CUI", 0 );
        positions.put( "SUI", 1 );
        positions.put( "phraseLabel", 2 );
        positions.put( "mentionLabel", 3 );
        positions.put( "Decision", 4 );
        positions.put( "Comment", 5 );
    }

    public static String[] getHeaderRow() {
        String[] result = new String[positions.size()];
        for ( String key : positions.keySet() ) {
            result[positions.get( key )] = key;
        }
        return result;
    }

    public static Integer getPosition( String varName ) {
        return positions.get( varName );
    }

    public static void main( String args[] ) {
        System.out.println( "Header:" + Arrays.asList(getHeaderRow()).toString() );
    }
}
