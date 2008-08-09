package ubic.GEOMMTx.evaluation;

import java.io.FileInputStream;
import java.util.Map;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class PhrasetoCUISpreadsheet extends CreateSpreadSheet {

    
    public PhrasetoCUISpreadsheet(String filename) throws Exception {
        super(filename, new PhraseToCUISchema());
    }
    
    
    public void populate( String inputFile ) throws Exception {
        Model model = ModelFactory.createDefaultModel();
        FileInputStream fi = new FileInputStream( inputFile );
        model.read( fi, null );
        fi.close();
        log.info( "Done reading..." );

        String queryString = "PREFIX leon: <http://www.purl.org/leon/umls#>                                 \r\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>                                \r\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>                                     \r\n"
                + "PREFIX gss: <http://www.w3.org/2001/11/IsaViz/graphstylesheets#>                         \r\n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>                                          \r\n"
                + "PREFIX rs: <http://www.w3.org/2001/sw/DataAccess/tests/result-set#>                      \r\n"
                + "SELECT DISTINCT ?CUI ?SUI ?phraseLabel ?mentionLabel                    \r\n"
                + "WHERE {                                                                                  \r\n"
                + "    ?phrase leon:hasMention ?mention .                                                   \r\n"
                + "    ?phrase rdfs:label ?phraseLabel .                                                   \r\n"
                + "    ?mention leon:hasSUI ?SUI .                                                         \r\n"
                + "    ?mention leon:hasCUI ?CUI .                                                         \r\n"
                + "    ?mention rdfs:label ?mentionLabel .                                                 \r\n"
                + "    ?mention leon:mappedTerm ?mappedTerm .                                                 \r\n"
                + "} ORDER BY ASC(?CUI) ASC(?SUI)                                                                   \r\n";

        // sparql query
        // CUI, SUI, phrase, label
        // dataset and description URL?
        // needs mapped term, and sorted?
        Query q = QueryFactory.create( queryString );
        // go through them all and put in excel file
        QueryExecution qexec = QueryExecutionFactory.create( q, model );

        try {
            int row = 1;
            ResultSet results = qexec.execSelect();
            log.info( "Query executed" );
            String lastSUI = "";
            while ( results.hasNext() ) {
                // get the next solution and load it into a map (varName -> value)
                Map<String, String> solutionMap = mapQuerySolution( results.nextSolution() );

                // block together like SUI's, requires that sparql sorts SUI
                if ( !solutionMap.get( "SUI" ).equals( lastSUI ) ) {
                    row++;
                }
                lastSUI = solutionMap.get( "SUI" );

                // here we take the variable names, find the position and value and put it in the excel file
                for ( String varName : solutionMap.keySet() ) {
                    Integer position = schema.getPosition( varName );
                    // if it has no mapping to the excel file, move on
                    if ( position == null ) continue;
                    String value = solutionMap.get( varName );
                    ExcelUtil.setValue( spreadsheet, row, position, value );
                }
                row++;
            }
        } finally {
            qexec.close();
        }

    }
    
    /**
     * @param args
     */
    public static void main( String[] args ) throws Exception {
        PhrasetoCUISpreadsheet test = new PhrasetoCUISpreadsheet( "test.xls" );
        log.info( "populating" );
        test.populate( "mergedRDF.rdf" );
        log.info( "saving.." );
        test.save();
        log.info( "Done!" );
    }
}
