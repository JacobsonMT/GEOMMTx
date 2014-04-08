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
package ubic.GEOMMTx.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.evaluation.PhrasetoCUISpreadsheet;

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
        @Override
        public boolean accept( File dir, String name ) {
            return name.toLowerCase().endsWith( ".rdf" );
        }
    };

    /**
     * @throws Exception
     */
    public static void excelAll() throws Exception {
        File workingDir = new File( "." );
        File[] files = workingDir.listFiles( RDFFileFilter );
        PhrasetoCUISpreadsheet test = new PhrasetoCUISpreadsheet( "test.xls" );

        for ( File file : files ) {
            // log.info( "populating" );
            if ( file.getName().contains( "merged" ) ) continue;
            test.populate( file.getName() );
            log.info( "Done " + file.getName() );
        }
    }

    /**
     * @param output
     * @param files
     * @throws Exception
     */
    public static void mergeRDFFiles( File output, File[] files ) throws Exception {
        Model oldModel = ModelFactory.createDefaultModel();
        int i = 0;
        for ( File file : files ) {
            Model current = ModelFactory.createDefaultModel();
            log.info( file.toString() + " " + ( i++ ) + " of " + files.length );
            try (FileInputStream in = new FileInputStream( file );) {
                current.read( in, null );
                Model newModel = oldModel.union( current );
                oldModel = newModel;
            }
        }
        log.info( "Writing out" );
        try (FileOutputStream out = new FileOutputStream( output );) {
            oldModel.write( out );
        }
    }

    public static void mergeWorkingDirRDF( String outputfile ) throws Exception {
        File workingDir = new File( "." );
        mergeRDFFiles( new File( outputfile ), workingDir.listFiles( RDFFileFilter ) );
    }
}
