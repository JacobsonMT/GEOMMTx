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
package ubic.GEOMMTx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.evaluation.CreateSpreadSheet;
import ubic.GEOMMTx.util.SetupParameters;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * Gets the labels of the ontology classes and saves them to disk
 * 
 * @author leon
 * @version $Id$
 */
public class LabelLoader {

    private static final String LABEL_FILE_LOCATION_KEY = "geommtx.annotator.cachedLabels";

    protected static Log log = LogFactory.getLog( LabelLoader.class );

    /**
     * @return label map
     */
    public static Map<String, String> readLabels() {

        try {
            String labelFilePath = getLabelFilePath();
            log.info( "Reading labels from " + labelFilePath );
            File labelFile = new File( labelFilePath );
            if ( !labelFile.canRead() ) {
                return writeLabels();
            }

            ObjectInputStream o2 = new ObjectInputStream( new FileInputStream( labelFilePath ) );
            Map<String, String> labels = ( Map<String, String> ) o2.readObject();
            o2.close();
            return labels;
        } catch ( Exception e ) {
            log.error( "Problem loading cached labels, reloading [" + e.getMessage() + "]" );
            return writeLabels();
        }
    }

    /**
     * @return label map
     */
    private static Map<String, String> writeLabels() {

        String labelFilePath = getLabelFilePath();

        File labelFile = new File( labelFilePath );
        File dir = new File( labelFile.getParent() );
        if ( !dir.exists() ) dir.mkdirs();

        log.info( "Initializing the label cache..." );

        OntologyLabelLoader labelLoader = new OntologyLabelLoader();
        Model model = labelLoader.loadOntologies();

        Map<String, String> labels = new HashMap<String, String>();

        String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>                                     \r\n"
                + "SELECT DISTINCT ?URI ?label                                    \r\n"
                + "WHERE {                                                                                  \r\n"
                + "    ?URI rdfs:label ?label .                                                         \r\n" + "}";
        Query q = QueryFactory.create( queryString );
        QueryExecution qexec = QueryExecutionFactory.create( q, model );
        ResultSet results = qexec.execSelect();
        while ( results.hasNext() ) {
            Map<String, String> solutionMap = CreateSpreadSheet.mapQuerySolution( results.nextSolution() );
            // a bad way to get rid of anonymous nodes
            if ( solutionMap.get( "URI" ).contains( "http" ) ) {
                labels.put( solutionMap.get( "URI" ), solutionMap.get( "label" ) );
            }
        }

        try {
            ObjectOutputStream o2 = new ObjectOutputStream( new FileOutputStream( labelFilePath ) );
            o2.writeObject( labels );
            o2.close();
        } catch ( FileNotFoundException e ) {
            throw new RuntimeException( e );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        log.info( "Labels written" );
        return labels;
    }

    private static String getLabelFilePath() {
        String path = SetupParameters.getString( LABEL_FILE_LOCATION_KEY );
        return path;
    }

    private LabelLoader() {
    }

}
