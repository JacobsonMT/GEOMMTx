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
 * TODO document me
 * 
 * @author lfrench
 * @version $Id$
 */
public class DiseaseOntologyMapper extends AbstractToUMLSMapper {

    public static void main( String args[] ) {
        DiseaseOntologyMapper test = new DiseaseOntologyMapper();
        // test.loadFromOntology();
        // test.save();
        // /String cui =

        System.out.println( test.convert( "C0020492", null ) );
        System.out.println( "CUI's that have more that one URI:" + test.countOnetoMany() );
        System.out.println( "All urls size:" + test.getAllURLs().size() );
    }

    private OntModel model;

    public DiseaseOntologyMapper() {
        super();
    }

    @Override
    public String getMainURL() {
        return Configuration.getString( "url.diseaseOntology" );
    }

    @Override
    public void loadFromOntology() {
        CUIMap = new HashMap<String, Set<String>>();
        model = OntologyLoader.loadMemoryModel( getMainURL() );

        String queryString = "PREFIX oboInOwl: <http://www.geneontology.org/formats/oboInOwl#>  "
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  SELECT ?obj ?label ?dbcode WHERE  { "
                + "    ?anon rdfs:label ?dbcode . ?obj oboInOwl:hasDbXref ?anon .  "
                + "   ?obj rdfs:label ?label .  FILTER (REGEX(?dbcode, \"UMLS_CUI:\")) }";

        System.err.println( queryString );

        Query q = QueryFactory.create( queryString );
        QueryExecution qexec = QueryExecutionFactory.create( q, model );
        try {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {

                QuerySolution soln = results.nextSolution();

                log.info( soln );

                // String label = OntologyTools.varToString( "label", soln );
                String URI = OntologyTools.varToString( "obj", soln );
                String cui = OntologyTools.varToString( "dbcode", soln );

                // UMLS_CUI:C00123 is split and we use the second half
                cui = cui.split( ":" )[1];

                Set<String> URIs = CUIMap.get( cui );
                if ( URIs == null ) {
                    URIs = new HashSet<String>();
                    CUIMap.put( cui, URIs );
                }
                URIs.add( URI );

                /*
                 * System.out.print( label + " " ); System.out.println( cui + " " ); System.out.println( URI + " " );
                 */
                //
                // if ( x.isAnon() ) continue; // some reasoners will return these.
            }

            if ( CUIMap.isEmpty() ) {
                log.warn( "No mappings found for DO" );
            } else {
                log.warn( CUIMap.size() + " mappings found for DO" );
            }

        } finally {
            qexec.close();
        }
    }

}
