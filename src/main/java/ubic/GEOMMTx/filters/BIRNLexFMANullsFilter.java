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
package ubic.GEOMMTx.filters;

import java.util.HashSet;
import java.util.Set;

import ubic.GEOMMTx.Vocabulary;
import ubic.gemma.ontology.BirnLexOntologyService;
import ubic.gemma.ontology.FMAOntologyService;
import ubic.gemma.ontology.OntologyService;
import ubic.gemma.ontology.OntologyTerm;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class BIRNLexFMANullsFilter extends AbstractFilter implements URIFilter {
    public static void main( String args[] ) throws Exception {
        BIRNLexFMANullsFilter test = new BIRNLexFMANullsFilter();
    }

    OntologyService ontService;

//    public BIRNLexFMANullsFilter(OntologyService ontService) {
//        this.ontService = ontService;
//    }
    
    public BIRNLexFMANullsFilter() {
        // load FMA and birnlex
        FMAOntologyService FMA = new FMAOntologyService();
        BirnLexOntologyService BIRN = new BirnLexOntologyService();
        FMA.init( true );
        BIRN.init( true );
        while ( !( FMA.isOntologyLoaded() && BIRN.isOntologyLoaded() ) ) {
            try {
                Thread.sleep( 2500 );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        ontService = new OntologyService();
        ontService.setBirnLexOntologyService( BIRN ) ;
        ontService.setFmaOntologyService( FMA );        
        log.info( "FMA and BIRNLex Ontologies loaded" );
    }

    public OntologyService getOntologyService() {
        return ontService;
    }

    public boolean accept( String URI ) {
        OntologyTerm term = ontService.getTerm( URI );
        // go into FMA and birnlex and check if it's missing
        if ( URI.contains( "/owl/FMA#" ) && term == null ) {
            return false;
        }
        if ( URI.contains( "birnlex" ) && term == null ) {
            return false;
        }
        return true;
    }

    @Override
    public int filter( Model model ) {
        // need a list of all the appearing URL's
        Set<String> removeURIs = new HashSet<String>();

        String queryString = "PREFIX gemmaAnn: <http://bioinformatics.ubc.ca/Gemma/ws/xml/gemmaAnnotations.owl#>\n"
                + "SELECT DISTINCT ?url \n                                                                                       "
                + "WHERE {\n                                                                                                 "
                + "   ?mention gemmaAnn:" + Vocabulary.mappedTerm.getLocalName()
                + " ?url .\n                                                                        " + "}";

        Query q = QueryFactory.create( queryString );
        QueryExecution qexec = QueryExecutionFactory.create( q, model );

        int count = 0;

        ResultSet results = qexec.execSelect();
        while ( results.hasNext() ) {
            QuerySolution soln = results.nextSolution();
            Resource urlR = soln.getResource( "url" );
            String URI = urlR.getURI();
            // go into FMA and birnlex and check if it's missing
            // if its then add it to the set
            if ( accept( URI ) == false ) {
                removeURIs.add( URI );
                // log.info( URI );
                count++;
            }
            // else its not FMA or birnlex
        }
        // log.info( "number of null URL's:" + count );
        return removeMentionsURLs( model, removeURIs );
    }

    @Override
    public String getName() {
        return "BIRNLex and FMA null mapping remover";
    }

}
