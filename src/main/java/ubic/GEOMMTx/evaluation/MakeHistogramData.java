package ubic.GEOMMTx.evaluation;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ubic.GEOMMTx.Text2OwlModelTools;
import ubic.gemma.ontology.OntologyTools;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class MakeHistogramData {
    // needs refactor -maybe its own class
    public static void addToSetMap( Map<String, Set<String>> hash, String key, String value ) {
        Set<String> current = hash.get( key );
        if ( current == null ) {
            current = new CopyOnWriteArraySet<String>();
            hash.put( key, current );
        }
        current.add( value );
    }

    // convert a hash of sets to an vector of the set sizes
    public static Integer[] getCounts( Map<String, Set<String>> hash ) {
        List<Integer> l = new LinkedList<Integer>();
        for ( String key : hash.keySet() ) {
            l.add( hash.get( key ).size() );
        }
        return l.toArray( new Integer[0] );
    }

    // so we get all the experiment to concept pairings that have mapped terms and then make a vector
    public static void makeHistoGrams(String file) throws Exception {
        Map<String, Set<String>> expToConcept = new HashMap<String, Set<String>>();
        Map<String, Set<String>> conceptToExp = new HashMap<String, Set<String>>();

        Model model = ModelFactory.createDefaultModel();
        model.read( new FileInputStream( file ), null );

        String queryString = "PREFIX leon: <http://www.purl.org/leon/umls#>                                            \r\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>                                \r\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>                                     \r\n"
                + "PREFIX gss: <http://www.w3.org/2001/11/IsaViz/graphstylesheets#>                         \r\n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>                                          \r\n"
                + "PREFIX rs: <http://www.w3.org/2001/sw/DataAccess/tests/result-set#>                      \r\n"
                + "SELECT ?dataset ?geoLabel ?label  ?mapping                                             \r\n"
                + "WHERE {                                                                                  \r\n"
                + "    ?dataset leon:describedBy ?description .                                             \r\n"
                + "    ?dataset rdfs:label ?geoLabel .                                                      \r\n"
                + "    ?description leon:hasPhrase ?phrase .                                                \r\n"
                + "    ?phrase leon:hasMention ?mention .                                                   \r\n"
                + "    ?mention leon:mappedTerm ?mapping .                                                  \r\n"
                + "    ?mention rdfs:label ?label .                                                         \r\n"
                + "}                                                                                        \r\n";

        Query q = QueryFactory.create( queryString );
        QueryExecution qexec = QueryExecutionFactory.create( q, model );
        try {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                String geoLabel = OntologyTools.varToString( "geoLabel", soln );
                String mentionURL = OntologyTools.varToString( "mapping", soln );
                String label = OntologyTools.varToString( "label", soln );
                addToSetMap( conceptToExp, label+"|"+mentionURL, geoLabel );
                addToSetMap( expToConcept, geoLabel, label );
            }
        } finally {
            qexec.close();
        }

        // Histogram1D hist = new Histogram1D( "Distribution of terms" );//numBins, minSize, maxSize );
        // run query

        // remove dupes within experiments - hash->set

        System.out.println( "Concept to experiment" );
        System.out.println( arrayToRString( getCounts( conceptToExp ) ) );
        printMapSizes(conceptToExp);
        System.out.println( "Experiment to Concept" );
        System.out.println( arrayToRString( getCounts( expToConcept ) ) );
        printMapSizes(expToConcept);
        
    }

    public static void printMapSizes(Map<String, Set<String>> map) {
        for ( String key : map.keySet() ) {
            //System.out.println("\""+key +"\","+ map.get( key ).size() );
            System.out.println(key + "|"+ map.get( key ).size() );
        }
    }
    
    
    // make a string for R read.table
    public static String arrayToRString( Object[] array ) {
        String result = Arrays.asList( array ).toString();
        result = result.replaceAll( "[,]", "" );
        result = result.replaceAll( "\\[", "" );
        result = result.replaceAll( "\\]", "" );
        return result;
    }
    
    public static void main( String[] args ) throws Exception {
        makeHistoGrams( "mergedRDFBirnLexUpdate.afterUseless.rdf");
    }
}
