package ubic.GEOMMTx;

import java.util.Collection;

import ubic.GEOMMTx.mappers.BirnLexMapper;
import ubic.GEOMMTx.mappers.DiseaseOntologyMapper;
import ubic.GEOMMTx.mappers.FMALiteMapper;
import ubic.gemma.model.common.description.Characteristic;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.util.AbstractSpringAwareCLI;

public class EvaluateCharacteristics extends AbstractSpringAwareCLI {


    public EvaluateCharacteristics() {
    }

    @Override
    protected void buildOptions() {
    }

    public static void main( String[] args ) {
        Main p = new Main();

        // DocumentRange t = null;

        try {
            Exception ex = p.doWork( args );
            if ( ex != null ) {
                ex.printStackTrace();
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Exception doWork( String[] args ) {
        long totaltime = System.currentTimeMillis();
        Exception err = processCommandLine( "Get characteristics ", args );
        if ( err != null ) return err;

        ExpressionExperimentService ees = ( ExpressionExperimentService ) this.getBean( "expressionExperimentService" );
        Collection<ExpressionExperiment> experiments = ees.loadAll();

        long time = System.currentTimeMillis();

        int c = 0;
        for ( ExpressionExperiment experiment : experiments ) {
            c++;

            log.info( "Experiment number:" + c + " of " + experiments.size() + " ID:" + experiment.getId() );

            time = System.currentTimeMillis();

            ees.thawLite( experiment );

            Collection<Characteristic> characters = experiment.getCharacteristics();
            for (Characteristic ch : characters ) {
                log.info( ch.toString());
                log.info( ch.getValue());
            }

            log.info( "--------------------------------------------" );
            log.info( ( ( System.currentTimeMillis() - time ) / 1000 ) + "s for whole experiment, writing out" );
        }

        System.out.println( "Total time:" + ( System.currentTimeMillis() - totaltime ) / 1000 + "s" );
        return null;
    }

    protected void processOptions() {
        super.processOptions();
    }
}
