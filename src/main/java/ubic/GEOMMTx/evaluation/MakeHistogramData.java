package ubic.GEOMMTx.evaluation;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ubic.GEOMMTx.SetupParameters;
import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.io.writer.MatrixWriter;
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
        if ( value != null ) current.add( value );
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
    public static void makeHistoGrams( String file ) throws Exception {
        Map<String, Set<String>> expToConcept = new HashMap<String, Set<String>>();
        Map<String, Set<String>> conceptToExp = new HashMap<String, Set<String>>();
        Set<String> titles = new HashSet<String>();
        Set<String> URIs = new HashSet<String>();
        Map<String, String> labelMap = new HashMap<String, String>();

        Model model = ModelFactory.createDefaultModel();
        model.read( new FileInputStream( file ), null );
        model.read( new FileInputStream( SetupParameters.gemmaTitles ), null );

        String queryString = "PREFIX leon: <http://www.purl.org/leon/umls#>                                            \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>                                     \n"
                + "SELECT DISTINCT ?mapping ?label \n"
                + "WHERE {                                                                                  \n"
                + "    ?mention leon:mappedTerm ?mapping .                                                            \n"
                + "    ?mention rdfs:label ?label .                                                         \n"
                + " }                                                                                        \n";
        Query q = QueryFactory.create( queryString );
        QueryExecution qexec = QueryExecutionFactory.create( q, model );
        ResultSet results = qexec.execSelect();
        while ( results.hasNext() ) {
            QuerySolution soln = results.nextSolution();
            String mapping = OntologyTools.varToString( "mapping", soln );
            String label = "\"" + OntologyTools.varToString( "label", soln ) + "\"";
            labelMap.put( mapping, label );
        }
        qexec.close();

        queryString = "PREFIX leon: <http://www.purl.org/leon/umls#>                                            \r\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>                                     \r\n"
                + "PREFIX dc: <http://purl.org/dc/elements/1.1/>                                           \r\n"
                + "SELECT DISTINCT ?dataset ?geoLabel ?mapping ?title                                        \r\n"
                + "WHERE {                                                                                  \r\n"
                + "    ?dataset leon:describedBy ?description .                                             \r\n"
                + "    ?dataset rdfs:label ?geoLabel .                                                      \r\n"
                + "    OPTIONAL {?dataset dc:title ?title}  .                                                           \r\n"
                + "    OPTIONAL{                                                                             \n"
                + "      ?description leon:hasPhrase ?phrase .                                                \r\n"
                + "      ?phrase leon:hasMention ?mention .                                                   \r\n"
                + "      ?mention leon:mappedTerm ?mapping .                                                  \r\n"
                + "      ?mention rdfs:label ?label .                                                         \r\n"
                + "} }                                                                                        \r\n";

        q = QueryFactory.create( queryString );
        qexec = QueryExecutionFactory.create( q, model );
        try {
            results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                String dataset = OntologyTools.varToString( "dataset", soln );
                String geoLabel = OntologyTools.varToString( "geoLabel", soln );
                String mapping = OntologyTools.varToString( "mapping", soln );
                String label = labelMap.get( mapping );
                String title = "\"" + OntologyTools.varToString( "title", soln ) + " [" + geoLabel + "]\"";
                titles.add( title );
                if ( mapping != null ) {
                    URIs.add( mapping );
                    addToSetMap( conceptToExp, label + "|" + mapping, geoLabel );
                    addToSetMap( expToConcept, geoLabel, label + "|" + mapping );
                } else {
                    addToSetMap( expToConcept, geoLabel, null );
                }
            }
        } finally {
            qexec.close();
        }
        System.out.println( "URIs:" + URIs.size() );
        Set<String> labels = new HashSet<String>();

        for ( String URI : URIs ) {
            labels.add( labelMap.get( URI ) );
        }
        System.out.println( "labels:" + labels.size() );

        queryString = "PREFIX leon: <http://www.purl.org/leon/umls#>                                            \r\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>                                     \r\n"
                + "PREFIX dc: <http://purl.org/dc/elements/1.1/>                                           \r\n"
                + "SELECT DISTINCT ?dataset ?geoLabel ?mapping  ?title                                                  \r\n"
                + "WHERE {                                                                                  \r\n"
                + "    ?dataset leon:describedBy ?description .                                             \r\n"
                + "    ?dataset rdfs:label ?geoLabel .                                                      \r\n"
                + "    ?description leon:hasPhrase ?phrase .                                                \r\n"
                + "    ?phrase leon:hasMention ?mention .                                                   \r\n"
                + "    ?mention leon:mappedTerm ?mapping .                                                  \r\n"
                + "    ?mention rdfs:label ?label .                                                         \r\n"
                + "    OPTIONAL {?dataset dc:title ?title} .                                                           \r\n"
                + " }                                                                                        \r\n";

        DoubleMatrix<String, String> resultMatrix = new DenseDoubleMatrix<String, String>( expToConcept.size(),
                conceptToExp.size() );
        resultMatrix.setRowNames( new ArrayList<String>( titles ) );
        resultMatrix.setColumnNames( new ArrayList<String>( labels ) );

        q = QueryFactory.create( queryString );
        qexec = QueryExecutionFactory.create( q, model );
        try {
            results = qexec.execSelect();
            int c = 0;
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();

                String geoLabel = OntologyTools.varToString( "geoLabel", soln );
                String title = "\"" + OntologyTools.varToString( "title", soln ) + " [" + geoLabel + "]\"";

                String mapping = OntologyTools.varToString( "mapping", soln );
                String label = labelMap.get( mapping );

                Double count = resultMatrix.getByKeys( title, label );
                resultMatrix.setByKeys( title, label, 1.0 );
                c++;
            }
            System.out.println( "c=" + c );
        } finally {
            qexec.close();
        }

        // Histogram1D hist = new Histogram1D( "Distribution of terms" );//numBins, minSize, maxSize );
        // run query

        // remove dupes within experiments - hash->set

        // System.out.println( "Concept to experiment" );
        // System.out.println( arrayToRString( getCounts( conceptToExp ) ) );
        // printMapSizes( conceptToExp );
        // System.out.println( "Experiment to Concept" );
        // System.out.println( arrayToRString( getCounts( expToConcept ) ) );
        // printMapSizes( expToConcept );

        System.out.println( "Concept to experiment" );
        System.out.println( arrayToRString( getCounts( conceptToExp ) ) );
        System.out.println( "Experiment to Concept" );
        System.out.println( arrayToRString( getCounts( expToConcept ) ) );
        System.out.println( "Number of concepts:" + conceptToExp.size() );
        System.out.println( "Number of experiments:" + expToConcept.size() );

        int total = 0;
        for ( Set<String> x : expToConcept.values() ) {
            total += x.size();
        }
        System.out.println( "Total from exp:" + total );

        total = 0;
        for ( Set<String> x : conceptToExp.values() ) {
            total += x.size();
        }
        System.out.println( "Total from concepts:" + total );

        writeRTable( "ExperimentToConceptMatrix.txt", resultMatrix );

        System.out.println( "Matrix wrote" );
    }

    public static void writeRTable( String filename, DoubleMatrix<String, String> matrix ) throws Exception {
        // write it out for R
        FileWriter fOut = new FileWriter( filename );
        MatrixWriter matWriter = new MatrixWriter( fOut );

        matWriter.setTopLeft( "" );
        matWriter.writeMatrix( matrix, true );
        fOut.close();
    }

    public static void printMapSizes( Map<String, Set<String>> map ) {
        for ( String key : map.keySet() ) {
            // System.out.println("\""+key +"\","+ map.get( key ).size() );
            System.out.println( key + "|" + map.get( key ).size() );
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
        makeHistoGrams( "mergedRDFBirnLexUpdate.afterUseless.rdf" );
        // makeHistoGrams( "mergedRDF.rejected.removed.rdf" );
    }
}
