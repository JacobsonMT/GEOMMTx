package ubic.GEOMMTx.filters;

import java.util.Set;

import ubic.GEOMMTx.evaluation.CUISUIPair;
import ubic.GEOMMTx.evaluation.EvaluatePhraseToCUISpreadsheet;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class CUISUIFilter extends AbstractFilter {
    Set<CUISUIPair> rejectedCUISUIPairs;

    public String getName() {
        return "Rejected CUI SRI pair remover";
    }

    public CUISUIFilter() throws Exception {
        // CUI -> SUI rejections
        EvaluatePhraseToCUISpreadsheet evalSheet = new EvaluatePhraseToCUISpreadsheet();
        rejectedCUISUIPairs = evalSheet.getRejectedSUIs();
    }

    /*
     * Goes through RDF and removes pairs that have rejected CUI/SUI combinations
     */
    @Override
    public int filter( Model model ) {
        int howMany = 0;

        // query for SUI CUI combinations

        String queryStringTemplate = "PREFIX gemmaAnn: <http://bioinformatics.ubc.ca/Gemma/ws/xml/gemmaAnnotations.owl#>\n"
                + "SELECT  ?mention ?phrase \n                                                                                       "
                + "WHERE {\n                                                                                                 "
                + "   ?phrase gemmaAnn:hasMention ?mention .\n                                                            "
                + "   ?mention gemmaAnn:hasSUI <$SUI> .\n                                                                        "
                + "   ?mention gemmaAnn:hasCUI <$CUI> .\n                                                                        "
                + "}";

        for ( CUISUIPair rejected : rejectedCUISUIPairs ) {
            String queryString = queryStringTemplate;
            queryString = queryString.replace( "$SUI", rejected.SUI );
            queryString = queryString.replace( "$CUI", rejected.CUI );

            Query q = QueryFactory.create( queryString );
            QueryExecution qexec = QueryExecutionFactory.create( q, model );

            ResultSet results = qexec.execSelect();
            howMany += removeMentions( model, results );
        }
        return howMany;
    }

}