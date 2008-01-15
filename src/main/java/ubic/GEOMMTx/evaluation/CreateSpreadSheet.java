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

public abstract class CreateSpreadSheet {
    protected static Log log = LogFactory.getLog( CreateSpreadSheet.class );

    /**
     * @param args
     */
    HSSFSheet spreadsheet;
    HSSFWorkbook workbook;
    String filename;
    SpreadSheetSchema schema;

    public CreateSpreadSheet( String filename, SpreadSheetSchema schema ) throws Exception {
        if ( new File( filename ).exists() ) {
            // throw new Exception( "please delete previous file to prevent overwrite" );
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
        this.schema = schema;
        createHeader();
    }

    private void createHeader() {
        String[] header = schema.getHeaderRow();
        for ( int i = 0; i < header.length; i++ ) {
            ExcelUtil.setValue( spreadsheet, 0, i, header[i] );
        }
    }

    public abstract void populate(String inputFile) throws Exception;

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


}
