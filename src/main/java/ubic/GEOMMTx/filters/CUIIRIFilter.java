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

import java.util.Set;

import ubic.GEOMMTx.SetupParameters;
import ubic.GEOMMTx.Vocabulary;
import ubic.GEOMMTx.evaluation.CUIIRIPair;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class CUIIRIFilter extends AbstractFilter {
    Set<CUIIRIPair> rejectedCUIIRIPairs;

    public CUIIRIFilter() {
        // CUI -> IRI rejections
        rejectedCUIIRIPairs = SetupParameters.rejectedCUIIRIPairs;
    }

    public String getName() {
        return "Rejected CUI IRI pair remover";
    }

    @Override
    public int filter( Model model ) {
        String queryStringTemplate = "PREFIX gemmaAnn: <http://bioinformatics.ubc.ca/Gemma/ws/xml/gemmaAnnotations.owl#>\n"
                + "SELECT  ?mention ?phrase \n                                                                                       "
                + "WHERE {\n                                                                                                 "
                + "   ?phrase gemmaAnn:hasMention ?mention .\n                                                            "
                + "   ?mention gemmaAnn:"
                + Vocabulary.mappedTerm.getLocalName()
                + " <$IRI> .\n                                                                        "
                + "   ?mention gemmaAnn:hasCUI <$CUI> .\n                                                                        "
                + "}";
        int howMany = 0;
        for ( CUIIRIPair rejected : rejectedCUIIRIPairs ) {
            String queryString = queryStringTemplate;
            queryString = queryString.replace( "$IRI", rejected.IRI );
            queryString = queryString.replace( "$CUI", rejected.CUI );
            // log.info( queryString );

            Query q = QueryFactory.create( queryString );
            QueryExecution qexec = QueryExecutionFactory.create( q, model );

            ResultSet results = qexec.execSelect();
            howMany += removeMentions( model, results );
        }
        return howMany;

    }

}
