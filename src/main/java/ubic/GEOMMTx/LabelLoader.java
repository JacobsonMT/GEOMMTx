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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.evaluation.CreateSpreadSheet;
import ubic.GEOMMTx.filters.UninformativeFilter;

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
 *
 */
public class LabelLoader {
    protected static Log log = LogFactory.getLog( LabelLoader.class );

    Model model;

    public LabelLoader() {
        OntologyLabelLoader labels = new OntologyLabelLoader();
        model = labels.loadOntologies();
    }

    public static Map<String, String> readLabels() throws Exception {
        ObjectInputStream o2 = new ObjectInputStream( new FileInputStream( SetupParameters.config
                .getString( "gemma.annotator.cachedLabels" ) ) );
        Map<String, String> labels = ( Map<String, String> ) o2.readObject();
        o2.close();
        return labels;
    }

    public Map<String, String> writeLabels() throws Exception {
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
            // System.out.println( solutionMap.get( "URI" ) + "->" + solutionMap.get( "label" ) );
            // a bad way to get rid of anonymous nodes
            if ( solutionMap.get( "URI" ).contains( "http" ) ) {
                labels.put( solutionMap.get( "URI" ), solutionMap.get( "label" ) );
            }
        }

        ObjectOutputStream o2 = new ObjectOutputStream( new FileOutputStream( SetupParameters.config
                .getString( "gemma.annotator.cachedLabels" ) ) );
        o2.writeObject( labels );
        o2.close();
        log.info( "Labels wrote" );
        return labels;
    }

}
