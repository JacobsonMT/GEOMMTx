/*
 * The GEOMMTx project
 * 
 * Copyright (c) 2010 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.GEOMMTx.mappers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ubic.GEOMMTx.OntologyTools;
import ubic.basecode.ontology.OntologyLoader;
import ubic.basecode.util.Configuration;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * @author paul
 * @version $Id$
 */
public class NIFSTDMapper extends AbstractToUMLSMapper {
    private OntModel model;

    @Override
    String getMainURL() {
        return "http://ontology.neuinfo.org/NIF/nif-gemma.owl";//Configuration.getString( "url.nifstdOntology" );
    }

    public static void main( String args[] ) {
        NIFSTDMapper test = new NIFSTDMapper();

        System.out.println( test.convert( "C0175286", null ) );
        System.out.println( "CUI's that have more than one URI:" + test.countOnetoMany() );
        System.out.println( "All urls size:" + test.getAllURLs().size() );
    }

    @Override
    void loadFromOntology() {
        CUIMap = new HashMap<String, Set<String>>();

        this.model = OntologyLoader.loadMemoryModel( getMainURL() );

        String queryString = "PREFIX obo_annot: <http://ontology.neuinfo.org/NIF/Backend/OBO_annotation_properties.owl#> "
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "SELECT ?class ?label ?cui "
                + "WHERE  {  ?class obo_annot:UmlsCui ?cui . ?class rdfs:label ?label . } ";

        Query q = QueryFactory.create( queryString );
        QueryExecution qexec = QueryExecutionFactory.create( q, model );
        try {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                // String label = OntologyTools.varToString( "label", soln );
                String cui = OntologyTools.varToString( "cui", soln );
                String URI = OntologyTools.varToString( "class", soln );
                // some have blank UMLS codes
                if ( !cui.equals( "" ) ) {
                    // if we already have a mapping for the CUI then?
                    Set<String> URIs = CUIMap.get( cui );
                    if ( URIs == null ) {
                        URIs = new HashSet<String>();
                        CUIMap.put( cui, URIs );
                    }
                    URIs.add( URI );
                }

                // System.out.print( label + "|" );
                // System.out.print( cui + "|" );
                // System.out.println( URI + "|" );
                //
                // if ( x.isAnon() ) continue; // some reasoners will return these.
            }
        } finally {
            qexec.close();
        }

    }

}
