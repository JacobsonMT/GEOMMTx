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

/*
 * TODO - update for new owl files ( <obo_annot:UmlsCui> )
 *      - update for a URL having more than one linked CUI
 */
public class BirnLexMapper extends AbstractToUMLSMapper implements CUIMapper {
    private OntModel birnLex;

    public String getMainURL() {
        return "http://purl.org/nbirn/birnlex/ontology/birnlex.owl";
    }

    public BirnLexMapper() {
        super();
    }

    public void loadFromOntology() {
        CUIMap = new HashMap<String, String>();

        // load the ontology model
        try {
            birnLex = OntologyLoader.loadMemoryModel( getMainURL() );
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        }

        String queryString = "PREFIX obo_annot: <http://purl.org/nbirn/birnlex/ontology/annotation/OBO_annotation_properties.owl#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "SELECT ?class ?label ?cui\n"
                + "WHERE  {\n" + "   ?class obo_annot:UmlsCui ?cui .\n" + "   ?class rdfs:label ?label .\n" + "}";

        Query q = QueryFactory.create( queryString );
        QueryExecution qexec = QueryExecutionFactory.create( q, birnLex );
        try {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                String label = OntologyTools.varToString( "label", soln );
                String cui = OntologyTools.varToString( "cui", soln );
                String URI = OntologyTools.varToString( "class", soln );
                //some have blank UMLS codes
                if ( !cui.equals( "" ) ) {
                    if(CUIMap.get(cui)!=null) {
                        System.out.print( label + "|" );
                        System.out.print( cui + "|" );
                        System.out.println( URI + "|" + CUIMap.get(cui));
                        
                    }
                    CUIMap.put( cui, URI );
                }

                //System.out.print( label + "|" );
                //System.out.print( cui + "|" );
                //System.out.println( URI + "|" );
                //                
                // if ( x.isAnon() ) continue; // some reasoners will return these.
            }
        } finally {
            qexec.close();
        }

    }

    public static void main( String args[] ) {
        BirnLexMapper test = new BirnLexMapper();
        test.loadFromOntology();
        test.save();
        // test.bonfire();
    }

}
