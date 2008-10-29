package ubic.GEOMMTx.filters;

import ubic.GEOMMTx.Vocabulary;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class LowScoreFilter extends AbstractFilter {

    int minScore;

    public String getName() {
        return "Low score remover";
    }

    /**
     * Sets the minimum allowed mmtx score
     * 
     * @param minScore - minimum score allowed (0-1000)
     */
    public LowScoreFilter( int minScore ) {
        this.minScore = minScore;
    }

    @Override
    public int filter( Model model ) {
        // query for low score mentions, these ones will be removed
        String queryString = "PREFIX gemmaAnn: <http://bioinformatics.ubc.ca/Gemma/ws/xml/gemmaAnnotations.owl#>\n"
                + "SELECT  ?mention ?score \n                                                                                       "
                + "WHERE {\n                                                                                                 "
                + "   ?mention gemmaAnn:" + Vocabulary.hasScore.getLocalName()
                + " ?score .\n                                 " + "   FILTER (?score <" + minScore
                + ")                                                            " + "}";

        Query q = QueryFactory.create( queryString );
        QueryExecution qexec = QueryExecutionFactory.create( q, model );

        ResultSet results = qexec.execSelect();
        return removeMentions( model, results );
    }
}
