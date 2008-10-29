package ubic.GEOMMTx.filters;

import java.util.HashSet;
import java.util.Set;

import ubic.GEOMMTx.Vocabulary;
import ubic.gemma.ontology.BirnLexOntologyService;
import ubic.gemma.ontology.FMAOntologyService;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class BIRNLexFMANullsFilter extends AbstractFilter {
    FMAOntologyService FMA;
    BirnLexOntologyService BIRN;

    public String getName() {
        return "BIRNLex and FMA null mapping remover";
    }


    public BIRNLexFMANullsFilter() {
        // load FMA and birnlex
        FMA = new FMAOntologyService();
        BIRN = new BirnLexOntologyService();
        FMA.init( true );
        BIRN.init( true );
        while ( !( FMA.isOntologyLoaded() && BIRN.isOntologyLoaded() ) ) {
            try {
                Thread.sleep( 2500 );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        log.info( "FMA and BIRNLex Ontologies loaded" );
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
            if ( URI.contains( "/owl/FMA#" ) && FMA.getTerm( URI ) == null ) {
                removeURIs.add( URI );
                //log.info( URI );
                count++;
            }
            if ( URI.contains( "birnlex" ) && BIRN.getTerm( URI ) == null ) {
                removeURIs.add( URI );
                //log.info( URI );
                count++;
            }
            // else its not FMA or birnlex
        }
        //log.info( "number of null URL's:" + count );
        return removeMentionsURLs( model, removeURIs );
    }

}
