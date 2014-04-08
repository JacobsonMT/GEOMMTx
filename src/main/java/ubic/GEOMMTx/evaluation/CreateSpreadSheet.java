/*
 * The GEOMMTx project
 * 
 * Copyright (c) 2007 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.GEOMMTx.evaluation;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ubic.GEOMMTx.OntologyTools;
import ubic.basecode.io.excel.ExcelUtil;

import com.hp.hpl.jena.query.QuerySolution;

/**
 * TODO document me
 * 
 * @author lfrench
 * @version $Id$
 */
public abstract class CreateSpreadSheet {
    protected static Log log = LogFactory.getLog( CreateSpreadSheet.class );

    /**
     * Given an Jena query solution get a map that has the varnames and there values
     * 
     * @param soln
     * @return
     */
    public static Map<String, String> mapQuerySolution( QuerySolution soln ) {
        Map<String, String> map = new HashMap<String, String>();
        String varName = "";
        for ( Iterator<String> i = soln.varNames(); i.hasNext(); ) {
            varName = i.next();
            map.put( varName, OntologyTools.varToString( varName, soln ) );
        }
        return map;
    }

    /**
     * @param args
     */
    HSSFSheet spreadsheet;
    HSSFWorkbook workbook;
    String filename;

    SpreadSheetSchema schema;

    public CreateSpreadSheet( String filename, SpreadSheetSchema schema ) {
        if ( new File( filename ).exists() ) {
            // throw new Exception( "please delete previous file to prevent overwrite" );
        }
        this.filename = filename;
        workbook = new HSSFWorkbook();
        spreadsheet = workbook.createSheet();
        // make the header
        this.schema = schema;
        createHeader();
    }

    // public abstract void populate(String inputFile) throws Exception;

    public void save() throws Exception {
        try (FileOutputStream fileOut = new FileOutputStream( filename );) {
            workbook.write( fileOut );
        }
    }

    private void createHeader() {
        String[] header = schema.getHeaderRow();
        for ( int i = 0; i < header.length; i++ ) {
            ExcelUtil.setValue( spreadsheet, 0, i, header[i] );
        }
    }

}
