package ubic.GEOMMTx;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.mappers.BirnLexMapper;
import ubic.GEOMMTx.mappers.DiseaseOntologyMapper;
import ubic.GEOMMTx.mappers.FMALiteMapper;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.util.AbstractSpringAwareCLI;

public class ExpressionExperimentAnntotatorTester extends AbstractSpringAwareCLI {
    private Text2Owl text2Owl;

    protected void processOptions() {
        super.processOptions();
    }

    @Override
    protected void buildOptions() {
    }

    protected static Log log = LogFactory.getLog( ExpressionExperimentAnntotatorTester.class );

    /**
     * @param args
     */
    public static void main( String[] args ) {
        ExpressionExperimentAnntotatorTester p = new ExpressionExperimentAnntotatorTester();

        try {
            Exception ex = p.doWork( args );
            if ( ex != null ) {
                ex.printStackTrace();
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public ExpressionExperimentAnntotatorTester() {

    }

    @SuppressWarnings("unchecked")
    @Override
    protected Exception doWork( String[] args ) {
        long totaltime = System.currentTimeMillis();

        Exception err = processCommandLine( "Expression experiment annotator tester ", args );
        if ( err != null ) return err;
        ExpressionExperimentService ees = ( ExpressionExperimentService ) this.getBean( "expressionExperimentService" );

        long time = System.currentTimeMillis();
        // text2Owl = null;

        Text2Owl text2Owl = new Text2Owl();
        text2Owl.addMapper( new BirnLexMapper() );
        text2Owl.addMapper( new FMALiteMapper() );
        text2Owl.addMapper( new DiseaseOntologyMapper() );

        System.out.println( "Total initialization time:" + ( System.currentTimeMillis() - time ) / 1000 + "s" );

        time = System.currentTimeMillis();

        ExpressionExperiment experiment = ees.load( 620l );
        ees.thawLite( experiment );

        ExpressionExperimentAnntotator experimentAnn = new ExpressionExperimentAnntotator( experiment, text2Owl );

        try {
            log.info( "getName()" );
            experimentAnn.annotateName();
            experimentAnn.writeModel();
            
            log.info( "getDescription()" );
            experimentAnn.annotateDescription();
            experimentAnn.writeModel();

            log.info( "Primary Publication" );
            experimentAnn.annotateReferences();
            experimentAnn.writeModel();

            log.info( "Factors" );
            experimentAnn.annotateExperimentalDesign();
            experimentAnn.writeModel();

            log.info( "iterate BioAssays" );
            experimentAnn.annotateBioAssays();
            experimentAnn.writeModel();

            try {
                experimentAnn.writeModel();
            } catch ( Exception e ) {
                e.printStackTrace();
            }

            log.info( "--------------------------------------------" );
            log.info( ( ( System.currentTimeMillis() - time ) / 1000 )
                    + "s for whole experiment, writing out to save memory" );

            System.out.println( "Total time:" + ( System.currentTimeMillis() - totaltime ) / 1000 + "s" );

            experiment = ees.load( 1l );
            ees.thawLite( experiment );
            experimentAnn = new ExpressionExperimentAnntotator( experiment, text2Owl );
            experimentAnn.writeModel();
        } catch ( Exception e ) {
            return e;
        }
        return null;
    }

}
