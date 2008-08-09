package ubic.GEOMMTx.evaluation;

import java.util.Arrays;

public class CheckHighLevelSchema extends SpreadSheetSchema {
    public CheckHighLevelSchema() {
        super();
        positions.put( "Dataset", 0 );
        positions.put( "Name", 1 );
        positions.put( "Reject", 2 );
        positions.put( "Comment", 3 );
        positions.put( "URL", 4 );
    }
    
    public static void main( String[] args ) {
        // TODO Auto-generated method stub
        CheckHighLevelSchema test = new CheckHighLevelSchema();
        System.out.println( "Header:" + Arrays.asList(test.getHeaderRow()).toString() );
    }
}
