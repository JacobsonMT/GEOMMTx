package ubic.GEOMMTx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.evaluation.CreateSpreadSheet;
import ubic.gemma.ontology.OntologyLoader;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OntologyLabelLoader {
    protected static Log log = LogFactory.getLog( OntologyLabelLoader.class );

    public Model loadOntologies() {
        Model model = ModelFactory.createDefaultModel();

        RDFNode nullNode = null;
        Selector labelSelector = new SimpleSelector( null, RDFS.label, nullNode );


        
        OntModel birnLex = OntologyLoader.loadMemoryModel( "http://purl.org/nbirn/birnlex/ontology/birnlex.owl" );
        log.info( "loaded birnLex..." );
        model.add( birnLex.listStatements( labelSelector ) );
        log.info( "Done merging Birnlex..." );
        birnLex.close();

        
        OntModel FMAlite = OntologyLoader
                .loadMemoryModel( "http://www.berkeleybop.org/ontologies/obo-all/fma_lite/fma_lite.owl" );
        log.info( "loaded FMA" );
        model.add( FMAlite.listStatements( labelSelector ) );
        log.info( "Done merging FMA..." );
        FMAlite.close();

        OntModel DO = OntologyLoader
                .loadMemoryModel( "http://www.berkeleybop.org/ontologies/obo-all/disease_ontology/disease_ontology.owl" );
        log.info( "loaded Disease ontology" );
        model.add( DO.listStatements( labelSelector ) );
        log.info( "Done merging Disease Ontology..." );
        DO.close();

        return model;
    }

}
