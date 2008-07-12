package ubic.GEOMMTx.mappers;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import ubic.GEOMMTx.CUIMapper;
import ubic.GEOMMTx.UMLSSourceCode;
import ubic.gemma.ontology.OntologyLoader;
import ubic.gemma.ontology.OntologyTools;

public class DiseaseOntologyMapper extends AbstractToUMLSMapper implements CUIMapper {

    private OntModel model;

    public String getMainURL() {
        return "http://www.berkeleybop.org/ontologies/owl/DOID";
    }

    
    public DiseaseOntologyMapper() {
        super();
    }

    public void loadFromOntology() {
        CUIMap = new HashMap<String, String>();

        // load the ontology model
        try {
            model = OntologyLoader.loadPersistentModel( getMainURL(), false );
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        }

        String queryString = "PREFIX oboInOwl: <http://www.geneontology.org/formats/oboInOwl#>                                                    \r\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>                                                    \r\n"
                + "SELECT ?obj ?label ?dbcode                                                    \r\n"
                + "WHERE  {                                                               \r\n"
                + "    ?anon rdfs:label ?dbcode .                                                    \r\n"
                + "    ?obj oboInOwl:hasDbXref ?anon .                                                    \r\n"
                + "    ?obj rdfs:label ?label .                                                    \r\n"
                + "    FILTER (REGEX(?dbcode, \"UMLS_CUI:\"))                                                    \r\n"
                + "}";

        Query q = QueryFactory.create( queryString );
        QueryExecution qexec = QueryExecutionFactory.create( q, model );
        try {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                // String label = OntologyTools.varToString( "label", soln );
                String URI = OntologyTools.varToString( "obj", soln );
                String cui = OntologyTools.varToString( "dbcode", soln );

                // UMLS_CUI:C00123 is split and we use the second half
                cui = cui.split( ":" )[1];

                CUIMap.put( cui, URI );
                /*
                 * System.out.print( label + " " ); System.out.println( cui + " " ); System.out.println( URI + " " );
                 */
                //                
                // if ( x.isAnon() ) continue; // some reasoners will return these.
            }
        } finally {
            qexec.close();
        }
    }

    public static void main( String args[] ) {
        DiseaseOntologyMapper test = new DiseaseOntologyMapper();
        // /String cui =
        System.out.println( test.convert( "C0020492", null ) );
    }

}
