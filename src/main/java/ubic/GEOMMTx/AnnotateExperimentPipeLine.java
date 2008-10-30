package ubic.GEOMMTx;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.filters.AbstractFilter;
import ubic.GEOMMTx.filters.BIRNLexFMANullsFilter;
import ubic.GEOMMTx.filters.CUIIRIFilter;
import ubic.GEOMMTx.filters.CUISUIFilter;
import ubic.GEOMMTx.filters.ExperimentalFactorFilter;
import ubic.GEOMMTx.filters.FrequentFilter;
import ubic.GEOMMTx.mappers.BirnLexMapper;
import ubic.GEOMMTx.mappers.DiseaseOntologyMapper;
import ubic.GEOMMTx.mappers.FMALiteMapper;
import ubic.gemma.model.association.GOEvidenceCode;
import ubic.gemma.model.common.description.Characteristic;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.util.AbstractSpringAwareCLI;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * A class that starts with a experiment ID and in the end outputs the predicted annotation URL's, after filtering
 * 
 * @author leon
 * @version $Id$
 * 
 */
public class AnnotateExperimentPipeLine extends AbstractSpringAwareCLI {
    private Text2Owl text2Owl;
    protected static Log log = LogFactory.getLog( AnnotateExperimentPipeLine.class );
    boolean loadOntologies = false;
    private List<AbstractFilter> filters;

    protected void processOptions() {
        super.processOptions();
    }

    @Override
    protected void buildOptions() {
        Option expOption = OptionBuilder.hasArg().isRequired().withArgName( "Expression experiment identifier" )
                .withDescription( "Expression experiment identifier." ).withLongOpt( "experiment" ).create( 'e' );
        addOption( expOption );
    }

    /**
     * @param args
     */
    public static void main( String[] args ) {
        AnnotateExperimentPipeLine p = new AnnotateExperimentPipeLine();

        try {
            Exception ex = p.doWork( args );
            if ( ex != null ) {
                ex.printStackTrace();
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public AnnotateExperimentPipeLine() {
        // init MMTx
        text2Owl = new Text2Owl();
        text2Owl.addMapper( new BirnLexMapper() );
        text2Owl.addMapper( new FMALiteMapper() );
        text2Owl.addMapper( new DiseaseOntologyMapper() );

        try {
            filters = new LinkedList<AbstractFilter>();
            filters.add( new CUISUIFilter() );
            filters.add( new CUIIRIFilter() );
            // filters.add( new BIRNLexFMANullsFilter() );
            filters.add( new FrequentFilter() );
        } catch ( Exception e ) {
            // TODO
            e.printStackTrace();
            log.error( e.getMessage() );
        }
    }

    public Set<String> getAnnotations( ExpressionExperiment e ) {
        long time = System.currentTimeMillis();

        Set<String> finalAnnotations;

        ExpressionExperimentAnntotator experimentAnn = new ExpressionExperimentAnntotator( e, text2Owl );

        try {
            log.info( "getName()" );
            experimentAnn.annotateName();
            // experimentAnn.writeModel();

            log.info( "getDescription()" );
            experimentAnn.annotateDescription();
            // experimentAnn.writeModel();

            log.info( "Publications" );
            experimentAnn.annotateReferences();
            // experimentAnn.writeModel();

            log.info( "iterate BioAssays" );
            experimentAnn.annotateBioAssays();
            // experimentAnn.writeModel();

        } catch ( Exception ee ) {
            ee.printStackTrace();
            log.error( ee.getMessage() );
        }

        Model model = experimentAnn.getModel();

        for ( AbstractFilter filter : filters ) {
            // log.info( "Mentions:" + Text2OwlModelTools.getMentionCount( model ) );
            // log.info( "Running: " + filter.getName() );
            int result = filter.filter( model );
            // log.info( "Removed: " + result );
        }
        log.info( "Final Mentions:" + ProjectRDFModelTools.getMentionCount( model ) );

        // write the file somewhere?
        try {
            experimentAnn.writeModel();
        } catch ( Exception ee ) {
            ee.printStackTrace();
            log.error( ee.getMessage() );
        }
        log.info( ( ( System.currentTimeMillis() - time ) / 1000 ) + "s for whole experiment" );

        finalAnnotations = ProjectRDFModelTools.getURLsFromSingle( model );

        return finalAnnotations;
    }

    /*
     * clears the cache of the MMTx runner
     */
    public void clearMMTxCache() {
        text2Owl.clearCache();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Exception doWork( String[] args ) {

        Exception err = processCommandLine( "Expression experiment annotator pipeline", args );
        if ( err != null ) return err;

        ExpressionExperimentService ees = ( ExpressionExperimentService ) this.getBean( "expressionExperimentService" );

        long time = System.currentTimeMillis();
        // text2Owl = null;

        time = System.currentTimeMillis();

        // ExpressionExperiment experiment = ees.load( 620l );
        ExpressionExperiment experiment = ees.load( Long.parseLong( getOption( "e" ).getValue() ) );
        ees.thawLite( experiment );

        Map<String, String> labels = null;
        try {
            labels = LabelLoader.readLabels();
        } catch ( Exception e ) {
            log.warn( "Couldnt load labels" );
        }

        Set<String> predictedAnnotations = getAnnotations( experiment );
        // for (String URI: predictedAnnotations) {
        // Characteristic c = Characteristic.Factory.newInstance();
        // c.setValue( URI );
        // //c.setCategory( ? )
        // c.setEvidenceCode( GOEvidenceCode.IEA );
        // //audit trail?
        // experiment.getCharacteristics().add( c );
        // }

        // System.out.println( "Time todo first:" + ( System.currentTimeMillis() - time ) );
        //
        // time = System.currentTimeMillis();
        // // for(int i=0; i<50;i++) {
        // // clearMMTxCache();
        // // long singleTime = System.currentTimeMillis();
        // // getAnnotations( experiment );
        // // System.out.println( "Time "+i+":" + ( System.currentTimeMillis() - singleTime ) );
        // // }
        // System.out.println( "Time todo another 50:" + ( System.currentTimeMillis() - time ) );
        //
        // for ( String annotation : getAnnotations( experiment ) ) {
        // System.out.println( labels.get( annotation ) + " - " + annotation );
        //
        // }
        System.out.println( "Total Time:" + ( System.currentTimeMillis() - time ) );
        return null;
    }

}
