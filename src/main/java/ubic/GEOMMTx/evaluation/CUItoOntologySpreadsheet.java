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
package ubic.GEOMMTx.evaluation;

import java.io.FileInputStream;
import java.util.Map;

import ubic.GEOMMTx.OntologyLabelLoader;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

/*
 * old code, needs to be updated with new URL's
 */
public class CUItoOntologySpreadsheet extends CreateSpreadSheet {
    /**
     * @param args
     */
    public static void main( String[] args ) throws Exception {
        CUItoOntologySpreadsheet test = new CUItoOntologySpreadsheet( "tt.xls" );

        // log.info( "populating" );
        // test.populate( "mergedRDF.firstrun.rdf" );
        // log.info( "saving.." );
        // test.save();
        // log.info( "Done!" );

    }

    Model linkedOntologies;

    Model model;

    public CUItoOntologySpreadsheet( String filename ) throws Exception {
        super( filename, new CUItoOntologySchema() );
        OntologyLabelLoader labels = new OntologyLabelLoader();
        model = labels.loadOntologies();
    }

    public void populate( String inputFile ) throws Exception {
        FileInputStream fi = new FileInputStream( inputFile );
        model.read( fi, null );
        fi.close();

        // log.info( "Done reading..." );

        String queryString = "PREFIX gemmaAnn: <http://bioinformatics.ubc.ca/Gemma/ws/xml/gemmaAnnotations.owl#>                                 \r\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>                                \r\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>                                     \r\n"
                + "PREFIX gss: <http://www.w3.org/2001/11/IsaViz/graphstylesheets#>                         \r\n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>                                          \r\n"
                + "PREFIX rs: <http://www.w3.org/2001/sw/DataAccess/tests/result-set#>                      \r\n"
                + "SELECT DISTINCT ?CUI ?CUILabel ?mappedTerm ?mappedTermLabel                              \r\n"
                + "WHERE {                                                                                  \r\n"
                + "    ?mention gemmaAnn:hasCUI ?CUI .                                                         \r\n"
                + "    ?mention rdfs:label ?CUILabel .                                                 \r\n"
                + "    ?mention gemmaAnn:mappedTerm ?mappedTerm .                                                 \r\n"
                + " ?mappedTerm rdfs:label ?mappedTermLabel . \r\n" + "} ORDER BY ASC(?CUI) ASC(?mappedTerm)";

        // sparql query
        // CUI, SUI, phrase, label
        // dataset and description URL?
        // needs mapped term, and sorted?
        Query q = QueryFactory.create( queryString );
        // go through them all and put in excel file
        QueryExecution qexec = QueryExecutionFactory.create( q, model );

        try {
            int skipped = 0;
            int row = 1;
            ResultSet results = qexec.execSelect();
            log.info( "Query executed" );
            while ( results.hasNext() ) {
                // get the next solution and load it into a map (varName -> value)
                Map<String, String> solutionMap = mapQuerySolution( results.nextSolution() );

                // so if the two labels are the same, do we need to evaluate it with a human?
                if ( solutionMap.get( "CUILabel" ).equalsIgnoreCase( solutionMap.get( "mappedTermLabel" ) ) ) {
                    skipped++;
                    continue;
                }

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
}
