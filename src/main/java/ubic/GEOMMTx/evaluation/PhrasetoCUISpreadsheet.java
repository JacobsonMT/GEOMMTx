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

import java.io.FileInputStream;
import java.util.Map;

import ubic.basecode.io.excel.ExcelUtil;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * TODO document me
 * 
 * @author lfrench
 * @version $Id$
 */
public class PhrasetoCUISpreadsheet extends CreateSpreadSheet {

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

    public PhrasetoCUISpreadsheet( String filename ) {
        super( filename, new PhraseToCUISchema() );
    }

    public void populate( String inputFile ) throws Exception {
        Model model = ModelFactory.createDefaultModel();
        try (FileInputStream fi = new FileInputStream( inputFile );) {
            model.read( fi, null );
        }
        log.info( "Done reading..." );

        String queryString = "PREFIX gemmaAnn: <http://bioinformatics.ubc.ca/Gemma/ws/xml/gemmaAnnotations.owl#>  \n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  \n"
                + "PREFIX gss: <http://www.w3.org/2001/11/IsaViz/graphstylesheets#> \n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>   \n"
                + "PREFIX rs: <http://www.w3.org/2001/sw/DataAccess/tests/result-set#>  \n"
                + "SELECT DISTINCT ?CUI ?SUI ?phraseLabel ?mentionLabel \n"
                + "WHERE {  \n"
                + "    ?phrase gemmaAnn:hasMention ?mention .  \n"
                + "    ?phrase rdfs:label ?phraseLabel .  \n"
                + "    ?mention gemmaAnn:hasSUI ?SUI .  \n"
                + "    ?mention gemmaAnn:hasCUI ?CUI . \n"
                + "    ?mention rdfs:label ?mentionLabel .  \n"
                + "    ?mention gemmaAnn:mappedTerm ?mappedTerm .  \n"
                + "} ORDER BY ASC(?CUI) ASC(?SUI) \n";

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
}
