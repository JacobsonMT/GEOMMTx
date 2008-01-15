package ubic.GEOMMTx.evaluation;

import java.util.Arrays;

public class CUItoOntologySchema extends SpreadSheetSchema {

    /**
     * @param args
     */
    public CUItoOntologySchema() {
        super();
        positions.put( "CUI", 0 );
        positions.put( "mappedTerm", 1 );
        positions.put( "CUILabel", 2 );
        positions.put( "Reject", 3 );
        positions.put( "mappedTermLabel", 4 );
        positions.put( "Comment", 5 );
    }
    
    public static void main( String[] args ) {
        // TODO Auto-generated method stub
        CUItoOntologySchema test = new CUItoOntologySchema();
        System.out.println( "Header:" + Arrays.asList(test.getHeaderRow()).toString() );
        


    }

}
