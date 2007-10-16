package ubic.GEOMMTx;

import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import ubic.GEOMMTx.mappers.BirnLexMapper;
import ubic.GEOMMTx.mappers.DiseaseOntologyMapper;
import ubic.GEOMMTx.mappers.FMALiteMapper;
import ubic.GEOMMTx.mappers.NullMapper;
import ubic.gemma.model.common.description.BibliographicReference;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.experiment.ExperimentalDesign;
import ubic.gemma.model.expression.experiment.ExperimentalFactor;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.persistence.PersisterHelper;
import ubic.gemma.util.AbstractSpringAwareCLI;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class Main extends AbstractSpringAwareCLI {

    private Text2Owl text2Owl;

    public Main() {
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
        Exception err = processCommandLine( "Expression experiment bibref finder ", args );
        if ( err != null ) return err;

        ExpressionExperimentService ees = ( ExpressionExperimentService ) this.getBean( "expressionExperimentService" );
        Collection<ExpressionExperiment> experiments = ees.loadAll();

        long time = System.currentTimeMillis();
        text2Owl = null;

//        text2Owl = new Text2Owl();
//        text2Owl.addMapper( new BirnLexMapper() );
//        text2Owl.addMapper( new FMALiteMapper() );
//        text2Owl.addMapper( new DiseaseOntologyMapper() );

        System.out.println( "Total initialization time:" + ( System.currentTimeMillis() - time ) / 1000 + "s" );

        int c = 0;
        for ( ExpressionExperiment experiment : experiments ) {
            log.info( "Experiment number:" + ++c + " of " + experiments.size() );
            // if (c++ > 10)
            // break;
            time = System.currentTimeMillis();

            ees.thawLite( experiment );

            ExpressionExperimentAnntotator experimentAnn = new ExpressionExperimentAnntotator( experiment, text2Owl );

            experimentAnn.annotateAll();

            try {
                experimentAnn.writeModel();
            } catch ( Exception e ) {
                e.printStackTrace();
            }

            log.info( "--------------------------------------------" );
            log.info( ( ( System.currentTimeMillis() - time ) / 1000 )
                    + "s for whole experiment, writing out to save memory" );
        }

        System.out.println( "Total time:" + ( System.currentTimeMillis() - totaltime ) / 1000 + "s" );
        return null;
    }

    // model.write( new FileWriter( "RDFfromText2Owl.main.rdf" ) );
    /*
     * desc becomes the URI, the text results are attatched to
     */

    public void printAndShow( String text, String indent, String shortName ) {
        // get rid of dupes

        // Collection<String> URIs = new CopyOnWriteArraySet<String>();
        // URIs.addAll( text2Owl.processText( text ) );
        // for ( String s : URIs ) {
        // // lets record what we've seen
        // Set<String> datasets = seen.get( s );
        // if ( datasets == null ) {
        // datasets = new HashSet<String>();
        // seen.put( s, datasets );
        // }
        // datasets.add( shortName );
        //
        // System.out.println( indent + s );
        // }
        // System.out.println( indent + "(" + ( ( System.currentTimeMillis() -
        // time ) / 1000 ) + "s)" );
    }

    protected void processOptions() {
        super.processOptions();
    }
}