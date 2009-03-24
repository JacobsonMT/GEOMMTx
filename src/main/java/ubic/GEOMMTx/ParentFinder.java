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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.gemma.ontology.AbstractOntologyService;
import ubic.gemma.ontology.BirnLexOntologyService;
import ubic.gemma.ontology.OntologyTerm;

public class ParentFinder {
    private static Log log = LogFactory.getLog( ParentFinder.class.getName() );

    // load ontologies
    // have a method that returns children?
    public static void main( String args[] ) throws Exception {
        ParentFinder x = new ParentFinder();
        x.init();
        x.test();
    }

    Set<AbstractOntologyService> ontologies = new HashSet<AbstractOntologyService>();

    /**
     * Given a set of URI's return them and their parents.
     * 
     * @param inputURIs
     * @return
     */
    public int nullTerms = 0;

    public Set<String> allParents( Set<String> inputURIs ) {
        Set<String> result = new HashSet<String>( inputURIs );
        for ( String URI : inputURIs ) {
            OntologyTerm t = getTerm( URI );
            if ( t == null ) {
                nullTerms++;
                log.warn( "got null term " + URI );
                continue;
            }
            // convert the parents to string and add it
            for ( OntologyTerm p : t.getParents( false ) ) {
                result.add( p.getUri() );
            }
        }
        return result;
    }

    public Map<String, Set<String>> expandToParents( Map<String, Set<String>> experiments ) {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        for ( String key : experiments.keySet() ) {
            result.put( key, allParents( experiments.get( key ) ) );
        }
        return result;
    }

    public void init() throws Exception {
         BirnLexOntologyService hd = new BirnLexOntologyService();

        //HumanDiseaseOntologyService hd = new HumanDiseaseOntologyService();
        // FMAOntologyService hd = new FMAOntologyService();
        
        hd.init( true );
        while ( !hd.isOntologyLoaded() ) {
            Thread.sleep( 5000 );
        }
        log.info( "Loaded ontology" );
        ontologies.add( hd );
    }

    /**
     * Given a set of URI's return only the leaves, that is remove all of the parent terms.
     * 
     * @param inputURIs
     * @return
     */
    public Set<String> onlyLeaves( Set<String> inputURIs ) {
        Set<String> result = new HashSet<String>( inputURIs );
        for ( String URI : inputURIs ) {
            Set<String> intersection = new HashSet<String>( inputURIs );
            OntologyTerm t = getTerm( URI );
            if ( t == null ) {
                nullTerms++;
                // log.warn( "got null term " + URI );
                continue;
            }

            // convert the parents to string
            Set<String> parents = new HashSet<String>();
            for ( OntologyTerm p : t.getParents( false ) ) {
                parents.add( p.getUri() );
            }

            intersection.retainAll( parents );

            // remove those that are in both lists from the final list
            result.removeAll( intersection );
        }
        return result;
    }

    public Map<String, Set<String>> reduceToLeaves( Map<String, Set<String>> experiments ) {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        for ( String key : experiments.keySet() ) {
            result.put( key, onlyLeaves( experiments.get( key ) ) );
        }
        return result;
    }

    public void test() {
        //OntologyTerm t = getTerm( "http://purl.org/nbirn/birnlex/ontology/BIRNLex-Anatomy.owl#birnlex_721" );
        //birnlex_4
        OntologyTerm t = getTerm( "http://purl.org/nbirn/birnlex/ontology/BIRNLex-OBO-UBO.owl#birnlex_4" );
        
        System.out.println( "Using:" + t.getLabel() );
        for ( OntologyTerm tt : t.getParents( false ) ) {
            System.out.println( tt.getLabel() );
            System.out.println( tt.getUri() );
        }
        System.out.println( "----------" );
        for ( OntologyTerm tt : t.getParents( true ) ) {
            System.out.println( tt.getLabel() );
            System.out.println( tt.getUri() );
        }
        System.out.println( "----------Children" );
        for ( OntologyTerm tt : t.getParents( true ) ) {
            System.out.println( tt.getLabel() );
            System.out.println( tt.getUri() );
        }
        System.out.println( "----------" );
        Set<String> inputURIs = new HashSet<String>();
        inputURIs.add( "http://purl.org/nbirn/birnlex/ontology/BIRNLex-Anatomy.owl#birnlex_721" );
        inputURIs.add( "http://purl.org/nbirn/birnlex/ontology/BIRNLex-Anatomy.owl#birnlex_731" );

        inputURIs.add( "http://purl.org/nbirn/birnlex/ontology/BIRNLex-Anatomy.owl#birnlex_922" );

        inputURIs.add( "http://purl.org/nbirn/birnlex/ontology/BIRNLex-Anatomy.owl#birnlex_757" );
        System.out.println( onlyLeaves( inputURIs ) );
        System.out.println( "----------" );

        System.out.println( allParents( inputURIs ) );

    }

    private OntologyTerm getTerm( String URI ) {
        OntologyTerm t = null;
        for ( AbstractOntologyService ontology : ontologies ) {
            t = ontology.getTerm( URI );
            // its gota be in one of the three ontologies
            if ( t != null ) break;
        }
        return t;
    }

}
