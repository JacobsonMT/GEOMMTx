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
import com.hp.hpl.jena.vocabulary.RDFS;

public class ExpressionExperimentAnntotator {
    protected static Log log = LogFactory.getLog( ExpressionExperimentAnntotator.class );

    private ExpressionExperiment experiment;
    private Model model;
    private Text2Owl text2Owl;
    private String ID;
    private Resource root;
    public static String gemmaNamespace = "http://bioinformatics.ubc.ca/Gemma/";;

    /**
     * Requires the experiment is thawed lite
     * 
     * @param experiment the expression experiment to annotate
     * @param text2Owl the text to owl object for producing URI's
     */
    public ExpressionExperimentAnntotator( ExpressionExperiment experiment, Text2Owl text2Owl ) {
        this.experiment = experiment;
        this.text2Owl = text2Owl;
        ID = "" + experiment.getId();
        clearModel();
    }

    public Model getModel() {
        return model;
    }

    public void clearModel() {
        model = ModelFactory.createDefaultModel();
        String GEOObjectURI = gemmaNamespace + "experiment/" + ID;
        root = model.createResource( GEOObjectURI );
        root.addProperty( RDFS.label, experiment.getShortName() );
    }

    public void annotateName() {
        // experiment then desc then name
        doRDF( experiment.getName(), "experiment/" + ID + "/name" );

    }

    public void annotateDescription() {
        String description = experiment.getDescription();
        if ( experiment.getId() == 444 ) {
            description = description.replace( "stroma", "stroma" );
            log.info( "fixing 444" );
        }

        doRDF( description, "experiment/" + ID + "/description" );
    }

    public void annotateAll() {
        log.info( "getName()" );
        annotateName();

        log.info( "Description" );
        annotateDescription();

        log.info( "Publications" );
        annotateReferences();

        // log.info( "Skipping Factors" );
        log.info( "Factors" );
        annotateExperimentalDesign();

        log.info( "BioAssays" );
        annotateBioAssays();
    }

    public void annotateBioAssays() {

        // this experiment hangs MMTx, runs out of memory
        if ( experiment.getId() == 576l ) {
            log.info( "skipping all Bioassays for 576" );
            return;
        }

        for ( BioAssay ba : experiment.getBioAssays() ) {
            // ba.getId()
            String nameSpaceBase = "bioAssay/" + ba.getId() + "/";
            if ( ba.getName() != null ) {
                doRDF( ba.getName().replace( "Expr(", "Expr " ), nameSpaceBase + "name" );
            }

            if ( ba.getDescription() != null ) {
                doRDF( ba.getDescription(), nameSpaceBase + "description" );
            }
            // log.info(ba.getDescription());
            log.info( ba.getName().replace( "Expr(", "Expr " ) );
        }
    }

    public void annotateExperimentalDesign() {
        ExperimentalDesign design = experiment.getExperimentalDesign();

        // Special case

        if ( design != null ) {
            String nameSpaceBase = "experimentalDesign/" + design.getId() + "/";
            if ( design.getDescription() != null ) {
                doRDF( design.getDescription(), nameSpaceBase + "description" );
            }

            Collection<ExperimentalFactor> factors = design.getExperimentalFactors();
            if ( factors != null ) {
                for ( ExperimentalFactor factor : factors ) {
                    String nameSpaceBaseFactors = "experimentalFactor/" + factor.getId() + "/";
                    doRDF( factor.getName(), nameSpaceBaseFactors + "name" );

                    doRDF( factor.getDescription(), nameSpaceBaseFactors + "description" );

                    // Collection<FactorValue> factorValues = factor.getFactorValues();
                    // for ( FactorValue factorValue : factorValues ) {
                    // log.info( factorValue.getValue() );
                    // log.info( factorValue.getId() );
                    // for ( Characteristic c : factorValue.getCharacteristics() ) {
                    // log.info( c.getName() );
                    // log.info( c.getValue() );
                    // log.info( c.getDescription());
                    // log.info( c.getId() );
                    // }
                    //
                    // // doRDF( factorValue.getValue(), nameSpaceBaseFactors + "factorValue/" + factorValue.getId());
                    // }

                }
            }
        }
    }

    public void annotateReferences() {
        BibliographicReference ref = experiment.getPrimaryPublication();
        if ( ref != null ) {
            String nameSpaceBase = "primaryReference/" + ref.getId() + "/";

            doRDF( ref.getTitle(), nameSpaceBase + "title" );
            if ( ref.getAbstractText() != null ) {
                doRDF( ref.getAbstractText(), nameSpaceBase + "abstract" );
            }
        }

        // Secondary Publications
        Collection<BibliographicReference> others = experiment.getOtherRelevantPublications();
        if ( others != null ) {
            for ( BibliographicReference other : others ) {
                String nameSpaceBase = "otherReference/" + other.getId() + "/";
                doRDF( other.getTitle(), nameSpaceBase + "title" );

                if ( other.getAbstractText() != null ) {
                    doRDF( other.getAbstractText(), nameSpaceBase + "abstract" );
                }
            }
        }
    }

    public void writeModel() throws IOException {
        model.write( new FileWriter( ID + ".rdf" ) );
    }

    /**
     * So this calls mmtx get the phrases, concepts and mappings and links them to the root node (the experiment)
     * 
     * @param text the text to be annotated
     * @param desc the description of the text, its appended on to the URI
     */
    public void doRDF( String text, String desc ) {
        if ( text.equals( "" ) ) return;
        text = text.replaceAll( "Source GEO sample is GSM[0-9]+", "" );
        text = text.replaceAll( "Last updated [(]according to GEO[)].+[\\d]{4}", "" );

        String cleanText = desc.replaceAll( "[()]", "" );
        String thisObjectURI = gemmaNamespace + cleanText;
        Resource thisResource = model.createResource( thisObjectURI );

        // connect root to this resource
        root.addProperty( model.createProperty( "http://www.purl.org/leon/umls#describedBy" ), thisResource );

        // this is to avoid text2Owl init times while testing, should be refactored
        if ( text2Owl == null ) return;

        // a bit strange here, since it takes in the root
        model = text2Owl.processText( text, thisResource );
    }

    public static void main( String[] args ) {

    }

}
