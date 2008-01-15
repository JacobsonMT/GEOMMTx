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
public abstract class SpreadSheetSchema {
    protected  Map<String, Integer> positions;
    protected  Log log = LogFactory.getLog( SpreadSheetSchema.class );

    // ?geoLabel ?description ?CUI ?SUI ?mapping ?phrase ?mentionLabel
    public SpreadSheetSchema() {
        positions = new HashMap<String, Integer>();
    }

    public  String[] getHeaderRow() {
        String[] result = new String[positions.size()];
        for ( String key : positions.keySet() ) {
            result[positions.get( key )] = key;
        }
        return result;
    }

    public  Integer getPosition( String varName ) {
        return positions.get( varName );
    }

}
