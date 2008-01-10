package ubic.GEOMMTx.evaluation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ubic.gemma.ontology.OntologyTools;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class CreateSpreadSheet {
    protected static Log log = LogFactory.getLog( CreateSpreadSheet.class );

    /**
     * @param args
     */
    final static short NNDEFAULTNAME = 2;

    HSSFSheet spreadsheet;
    HSSFWorkbook workbook;
    String filename;

    public CreateSpreadSheet( String filename ) throws Exception {
        if ( new File( filename ).exists() ) {
            throw new Exception( "please delete previous file to prevent overwrite" );
        }
        this.filename = filename;
        try {
            workbook = new HSSFWorkbook();
            spreadsheet = workbook.createSheet();
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        }
        // make the header
        String[] header = SpreadSheetSchema.getHeaderRow();
        for ( int i = 0; i < header.length; i++ ) {
            ExcelUtil.setValue( spreadsheet, 0, i, header[i] );
        }
    }

    public void polulate() throws Exception {
        Model model = ModelFactory.createDefaultModel();
        FileInputStream fi = new FileInputStream( "mergedRDF.rdf" );
        model.read( fi, null );
        fi.close();

        String queryString = "PREFIX leon: <http://www.purl.org/leon/umls#>                                 \r\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>                                \r\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>                                     \r\n"
                + "PREFIX gss: <http://www.w3.org/2001/11/IsaViz/graphstylesheets#>                         \r\n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>                                          \r\n"
                + "PREFIX rs: <http://www.w3.org/2001/sw/DataAccess/tests/result-set#>                      \r\n"
                + "SELECT DISTINCT ?geoLabel ?description ?CUI ?SUI ?mapping ?phraseLabel ?mentionLabel                    \r\n"
                + "WHERE {                                                                                  \r\n"
                + "    ?dataset leon:describedBy ?description .                                             \r\n"
                + "    ?dataset rdfs:label ?geoLabel .                                                      \r\n"
                + "    ?description leon:hasPhrase ?phrase .                                                \r\n"
                + "    ?phrase leon:hasMention ?mention .                                                   \r\n"
                + "    ?phrase rdfs:label ?phraseLabel .                                                   \r\n"
                + "    ?mention leon:mappedTerm ?mapping .                                                 \r\n"
                + "    ?mention leon:hasSUI ?SUI .                                                         \r\n"
                + "    ?mention leon:hasCUI ?CUI .                                                         \r\n"
                + "    ?mention rdfs:label ?mentionLabel .                                                 \r\n"
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
            String lastSUI = "";
            while ( results.hasNext() ) {
                // get the next solution and load it into a map (varName -> value)
                Map<String, String> solutionMap = mapQuerySolution( results.nextSolution() );

                //block together like SUI's, requires that sparql sorts SUI
                if (!solutionMap.get( "SUI" ).equals(lastSUI)){
                    row++;
                }
                lastSUI = solutionMap.get( "SUI" );

                // here we take the variable names, find the position and value and put it in the excel file
                for ( String varName : solutionMap.keySet() ) {
                    Integer position = SpreadSheetSchema.getPosition( varName );
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
     * Given an Jena query solution
     * 
     * @param soln
     * @return
     */
    public Map<String, String> mapQuerySolution( QuerySolution soln ) {
        Map<String, String> map = new HashMap<String, String>();
        String varName = "";
        for ( Iterator i = soln.varNames(); i.hasNext(); ) {
            try {
                varName = ( String ) i.next();
                map.put( varName, OntologyTools.varToString( varName, soln ) );
            } catch ( Exception e ) {
                System.err.println( "error on:" + varName );
                e.printStackTrace();
                System.exit( 1 );
            }
        }
        return map;
    }

    public void save() throws Exception {
        FileOutputStream fileOut = new FileOutputStream( filename );
        workbook.write( fileOut );
        fileOut.close();
    }

    public static void main( String[] args ) throws Exception {
        CreateSpreadSheet test = new CreateSpreadSheet( "test.xls" );
        log.info( "populating" );
        test.polulate();
        log.info( "saving.." );
        test.save();
        log.info( "Done!" );

    }

}
