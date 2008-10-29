package ubic.GEOMMTx.filters;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class ExperimentalFactorFilter extends AbstractFilter {

    public String getName() {
        return "Experimental factor remover";
    }

    @Override
    public int filter( Model model ) {
        String queryString = "PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX gemmaAnn: <http://bioinformatics.ubc.ca/Gemma/ws/xml/gemmaAnnotations.owl#>\n                              "
                + "\n                                                            "
                + "SELECT DISTINCT ?dataset ?description ?mention\n                                                            "
                + "WHERE {\n                                                            "
                + "    ?dataset gemmaAnn:describedBy ?description .\n                                                            "
                + "    ?description gemmaAnn:hasPhrase ?phrase .\n                                                            "
                + "    ?phrase gemmaAnn:hasMention ?mention .\n                                                            "
                + "    FILTER regex(str(?description), \"experimentalDesign|experimentalFactor\") }";
        Query q = QueryFactory.create( queryString );
        QueryExecution qexec = QueryExecutionFactory.create( q, model );

        ResultSet results = qexec.execSelect();
        return removeMentions( model, results );
    }

}
