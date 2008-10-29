package ubic.GEOMMTx;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.filters.AbstractFilter;
import ubic.gemma.ontology.OntologyTools;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

public class ProjectRDFModelTools {
    protected static Log log = LogFactory.getLog( ProjectRDFModelTools.class );

    static public void removeMentions( Model model, Set<Resource> affectedMentions ) {
        Set<Resource> affectedPhrases = new HashSet<Resource>();
        // remove all the links from the mention to its properties
        for ( Resource mention : affectedMentions ) {
            // mention.addLiteral( isRejected, true );
            // remove all of its properties (leaves a blank mention behind)
            mention.removeProperties();

            // get its phrase, assume it only has one
            ResIterator iterator = model.listResourcesWithProperty( Vocabulary.hasMention, mention );
            Resource phrase = ( Resource ) iterator.next();

            affectedPhrases.add( phrase );
            model.remove( model.createStatement( phrase, Vocabulary.hasMention, mention ) );
        }

        // this leaves behind some orphan phrases
        // remove them
        for ( Resource phrase : affectedPhrases ) {
            // make sure it has no mentions
            if ( model.listStatements( phrase, Vocabulary.hasMention, ( RDFNode ) null ).hasNext() == false ) {
                // log.info( phrase.listProperties(RDFS.label).toSet() );
                phrase.removeProperties();

                // remove the triple pointing to that phrase
                model.remove( model.listStatements( null, Vocabulary.hasPhrase, phrase ) );
            }
        }
    }

    public static Model loadModel( String file ) {
        Model model = ModelFactory.createDefaultModel();
        try {
            FileInputStream fi = new FileInputStream( file );
            model.read( fi, null );
            fi.close();
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        }
        return model;
    }

    public static int getMentionCount( Model model ) {
        int count = 0;
        String queryString = "PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX gemmaAnn: <http://bioinformatics.ubc.ca/Gemma/ws/xml/gemmaAnnotations.owl#>\n                              "
                + "SELECT ?phrase ?mention\n                                                            "
                + "WHERE {\n                                                            "
                + "    ?dataset gemmaAnn:describedBy ?description .\n                                                            "
                + "    ?description gemmaAnn:hasPhrase ?phrase .\n                                                            "
                + "    ?phrase gemmaAnn:hasMention ?mention .\n                                                           "
                + " }";
        Query q = QueryFactory.create( queryString );
        QueryExecution qexec = QueryExecutionFactory.create( q, model );
        ResultSet results = qexec.execSelect();
        while ( results.hasNext() ) {
            results.next();
            
            count++;
        }
        return count;
    }

    public static Map<String, Set<String>> getURLsExperiments( String filename ) {
        Model model = ProjectRDFModelTools.loadModel( filename );
        Map<String, Set<String>> result = getURLsExperiments( model );
        model.close();
        return result;
    }

    public static Set<String> getURLsFromSingle( Model model ) {
        Map<String, Set<String>> result = getURLsExperiments( model );
        if ( result.size() != 1 ) {
            log.warn( "More than one experiment, expected only one, returning annotations of one" );
        }
        return result.values().iterator().next();
    }

    /**
     * Given an model with a bunch of experiments, pull out the annotated URLS from the outside ontologies
     * 
     * @param model
     * @return a map with the experiments and URLs of the annotated classes
     */
    public static Map<String, Set<String>> getURLsExperiments( Model model ) {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();

        // from RDF
        String queryString = "PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX gemmaAnn: <http://bioinformatics.ubc.ca/Gemma/ws/xml/gemmaAnnotations.owl#>\n                              "
                + "\n                                                            "
                + "SELECT ?dataset ?label ?mapping\n                                                            "
                + "\n"
                + "WHERE {\n                                                            "
                + "    ?dataset gemmaAnn:describedBy ?description .\n                                                            "
                + "    OPTIONAL {\n                                                                           "
                + "      ?description gemmaAnn:hasPhrase ?phrase .\n                                                            "
                + "      ?phrase gemmaAnn:hasMention ?mention .\n                                                           "
                + "      ?mention gemmaAnn:mappedTerm ?mapping .\n                                                            "
                + "      ?mention rdf:label ?label .\n" + "} }";

        Query q = QueryFactory.create( queryString );
        // go through them all and put in excel file
        QueryExecution qexec = QueryExecutionFactory.create( q, model );

        int row = 1;
        ResultSet results = qexec.execSelect();
        while ( results.hasNext() ) {
            row++;
            QuerySolution soln = results.nextSolution();
            String dataset = OntologyTools.varToString( "dataset", soln );
            dataset = dataset.substring( dataset.lastIndexOf( '/' ) + 1 );
            // log.info( dataset );
            // log.info( OntologyTools.varToString( "mapping", soln ) );

            Set<String> datasetURLs = result.get( dataset );
            if ( datasetURLs == null ) {
                result.put( dataset, new HashSet<String>() );
                datasetURLs = result.get( dataset );
            }

            // this is dealt with in ontology tools - newer version
            String URL = null;
            if ( soln.getResource( "mapping" ) != null ) URL = OntologyTools.varToString( "mapping", soln );

            // it maybe a dataset that has no predictions
            if ( URL != null ) {
                datasetURLs.add( URL );
            }
        }
        return result;
    }

    public int getMentionCount( String filename ) {
        Model model = ProjectRDFModelTools.loadModel( filename );
        int count = getMentionCount( model );
        model.close();
        return count;
    }

}
