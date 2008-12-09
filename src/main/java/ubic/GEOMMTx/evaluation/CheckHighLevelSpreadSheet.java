package ubic.GEOMMTx.evaluation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import ubic.GEOMMTx.HashMapStringSet;
import ubic.GEOMMTx.SetupParameters;

/*
 * Call this from CompareToManual.java
 */
public class CheckHighLevelSpreadSheet extends CreateSpreadSheet {

    public CheckHighLevelSpreadSheet( String filename ) throws Exception {
        super( filename, new CheckHighLevelSchema() );
    }

    public void populate( Map<String, Set<String>> newPredictions, Map<String, String> labels, int size )
            throws Exception {

        List<String> datasets = new LinkedList<String>( newPredictions.keySet() );

        // sort then shuffle so we can re-create the file
        Collections.sort( datasets );

        Collections.shuffle( datasets, new Random( 1 ) );

        System.out.println( datasets );
        // pick random ones
        // through the set to list operations, assume it is random
        // write out

        int row = 0;
        for ( int i = 0; i < size; i++ ) {
            row++;
            String dataset = datasets.get( i );
            // ExcelUtil.setValue( spreadsheet, row, schema.getPosition( "Dataset" ), dataset );
            // for each URL

            // if its empty don't do it
            if ( newPredictions.get( dataset ).isEmpty() ) {
                size++;
                row--;
            }
            for ( String URL : newPredictions.get( dataset ) ) {
                row++;
                ExcelUtil.setFormula( spreadsheet, row, schema.getPosition( "Dataset" ),
                        "HYPERLINK(\"http://bioinformatics.ubc.ca/Gemma/expressionExperiment/showExpressionExperiment.html?id="
                                + dataset + "\",\"" + dataset + "\")" );
                ExcelUtil.setValue( spreadsheet, row, schema.getPosition( "URL" ), URL );
                ExcelUtil.setValue( spreadsheet, row, schema.getPosition( "Name" ), labels.get( URL ) );

            }

        }
    }


}
