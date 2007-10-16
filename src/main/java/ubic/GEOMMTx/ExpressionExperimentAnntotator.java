package ubic.GEOMMTx;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.gemma.model.common.description.BibliographicReference;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.experiment.ExperimentalDesign;
import ubic.gemma.model.expression.experiment.ExperimentalFactor;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class ExpressionExperimentAnntotator {
    protected static Log log = LogFactory.getLog( ExpressionExperimentAnntotator.class );

    private ExpressionExperiment experiment;
    private Model model;
    private Text2Owl text2Owl;
    private String shortName;
    private Resource root;

    /**
     * Requires the experiment is thawed lite
     * 
     * @param experiment the expression experiment to annotate
     * @param text2Owl the text to owl object for producing URI's
     */
    public ExpressionExperimentAnntotator( ExpressionExperiment experiment, Text2Owl text2Owl ) {
        this.experiment = experiment;
        this.text2Owl = text2Owl;
        shortName = "" + experiment.getId();
        clearModel();
    }

    public Model getModel() {
        return model;
    }

    public void clearModel() {
        model = ModelFactory.createDefaultModel();
        String GEOObjectURI = "http://bioinformatics.ubc.ca/Gemma/" + shortName;
        root = model.createResource( GEOObjectURI );
    }

    public void annotateName() {
        doRDF( experiment.getName(), "getName" );
    }

    public void annotateDescription() {
        doRDF( experiment.getDescription(), "getDescription" );
    }

    public void annotateAll() {
        log.info( "getName()" );
        annotateName();

        log.info( "getDescription()" );
        annotateDescription();

        log.info( "Primary Publication" );
        annotateReferences();

        log.info( "Factors" );
        annotateExperimentalDesign();

        log.info( "iterate BioAssays" );
        annotateBioAssays();
    }

    public void annotateBioAssays() {
        for ( BioAssay ba : experiment.getBioAssays() ) {
            if ( ba.getName() != null ) {
                doRDF( ba.getName(), "ba.getName" );
            }

            if ( ba.getDescription() != null ) {
                doRDF( ba.getDescription(), "ba.getDescription" );
            }
        }
    }

    public void annotateExperimentalDesign() {
        ExperimentalDesign design = experiment.getExperimentalDesign();
        if ( design != null ) {
            doRDF( design.getDescription(), "design.getDescription" );

            Collection<ExperimentalFactor> factors = design.getExperimentalFactors();
            if ( factors != null ) {
                for ( ExperimentalFactor factor : factors ) {
                    doRDF( factor.getName(), "factor.getName" );

                    doRDF( factor.getDescription(), "factor.getDescription" );
                }
            }
        }
    }

    public void annotateReferences() {
        BibliographicReference ref = experiment.getPrimaryPublication();
        if ( ref != null ) {
            doRDF( ref.getTitle(), "ref.getTitle" );
            if ( ref.getAbstractText() != null ) {
                doRDF( ref.getAbstractText(), "ref.getAbstractText" );
            }
        }

        // Secondary Publications
        Collection<BibliographicReference> others = experiment.getOtherRelevantPublications();
        if ( others != null ) {
            for ( BibliographicReference other : others ) {
                doRDF( other.getTitle(), "other.getTitle" );

                if ( other.getAbstractText() != null ) {
                    doRDF( other.getAbstractText(), "other.getAbstractText" );
                }
            }
        }
    }

    public void writeModel() throws IOException {
        model.write( new FileWriter( shortName + ".rdf" ) );
    }

    /**
     * @param text the text to be annotated
     * @param desc the description of the text (attatched to the shortname)
     */
    public void doRDF( String text, String desc ) {
        text = text.replaceAll( "Source GEO sample is GSM[0-9]+", "" );
        text = text.replaceAll( "Last updated [(]according to GEO[)].+[\\d]{4}", "" );

        String cleanText = desc.replaceAll( "[()]", "" );
        String thisObjectURI = root.getURI() + "#" + cleanText;
        Resource thisResource = model.createResource( thisObjectURI );

        // connect root to this resource
        root.addProperty( model.createProperty( "http://www.purl.org/leon/umls#describedBy" ), thisResource );

        // this is to avoid text2Owl init times while testing, should be refactored
        if ( text2Owl == null ) return;

        // a bit strange here, since it takes in the root
        model = text2Owl.processText( text, root );
    }
    
    public static void main(String[] args) {
        
    }

}
