package ubic.GEOMMTx.mappers;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.xerces.parsers.XMLParser;

import ubic.GEOMMTx.CUIMapper;
import ubic.GEOMMTx.UMLSSourceCode;
import ubic.gemma.ontology.OntologyLoader;
import ubic.gemma.ontology.OntologyTools;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class BirnLexMapper implements CUIMapper {
    // some concepts in brinlex are not from neuronames
    // example: C0025921 for C57B6 Mouse
    private final static String MAIN_URL = "http://fireball.drexelmed.edu/birnlex/";
    private OntModel birnLex;
    private Map<String, String> CUIMap;

    private final static String ORG_URL = "";

    public BirnLexMapper() {
        CUIMap = new HashMap<String, String>();

        // load the ontology model
        try {
            birnLex = OntologyLoader.loadPersistentModel( MAIN_URL, false  );
        } catch ( IOException e ) {
            e.printStackTrace();
            System.exit( 1 );
        }

        String queryString = "PREFIX BIRNLex_annotation_properties: <http://www.nbirn.net/birnlex/1.1/BIRNLex_annotation_properties.owl#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "SELECT ?class ?label ?cui\n"
                + "WHERE  {\n"
                + "   ?class BIRNLex_annotation_properties:UmlsCui ?cui .\n"
                + "   ?class rdfs:label ?label .\n" + "}";

        Query q = QueryFactory.create( queryString );
        QueryExecution qexec = QueryExecutionFactory.create( q, birnLex );
        try {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                String label = OntologyTools.varToString( "label", soln );
                String cui = OntologyTools.varToString( "cui", soln );
                String URI = OntologyTools.varToString( "class", soln );
                CUIMap.put( cui, URI );
                 //System.out.print( label + " " );
                 //System.out.println( cui + " " );
                 //System.out.println( URI + " " );
                //                
                // if ( x.isAnon() ) continue; // some reasoners will return these.
            }
        } finally {
            qexec.close();
        }

    }


    public String convert( String CUI, Collection<UMLSSourceCode> sourceCodes ) {
        return CUIMap.get(CUI);
    }

    public static void main( String args[] ) {
        BirnLexMapper test = new BirnLexMapper();
    }

}
