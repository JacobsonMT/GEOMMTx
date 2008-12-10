/*
 * The Gemma project
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
package ubic.GEOMMTx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.evaluation.CreateSpreadSheet;
import ubic.GEOMMTx.evaluation.PhrasetoCUISpreadsheet;
import ubic.gemma.ontology.OntologyTools;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Merges many RDF files
 * 
 * @author lfrench
 */
public class RDFMerge {
    protected static Log log = LogFactory.getLog( RDFMerge.class );

    // class for RDF filenames
    public static FilenameFilter RDFFileFilter = new FilenameFilter() {
        public boolean accept( File dir, String name ) {
            return name.toLowerCase().endsWith( ".rdf" );
        }
    };

    public static void mergeRDFFiles( File output, File[] files ) throws Exception {
        Model oldModel = ModelFactory.createDefaultModel();
        int i = 0;
        for ( File file : files ) {
            Model current = ModelFactory.createDefaultModel();
            log.info( file.toString() +" "+ (i++)+" of "+ files.length);
            current.read( new FileInputStream( file ), null );
            Model newModel = oldModel.union(current);
            oldModel = newModel;
        }
        log.info( "Writing out" );
        oldModel.write( new FileOutputStream( output ) );
    }

    public static void excelAll( ) throws Exception {
        File workingDir = new File( "." );
        File[] files = workingDir.listFiles( RDFFileFilter );
        PhrasetoCUISpreadsheet test = new PhrasetoCUISpreadsheet( "test.xls" );
        
        for ( File file : files ) {
            //log.info( "populating" );
            if (file.getName().contains("merged")) continue;
            test.populate(file.getName());
            log.info( "Done "+file.getName() );
        }
    }

    
    public static void mergeWorkingDirRDF( String outputfile ) throws Exception {
        File workingDir = new File( "." );
        mergeRDFFiles( new File( outputfile ), workingDir.listFiles( RDFFileFilter ) );
    }


    public static void main( String args[] ) throws Exception {
        mergeWorkingDirRDF( "mergedRDF.rdf" );
        //CreateSpreadSheet.main( null );
        //excelAll();
        //makeHistoGrams();
    }
}
