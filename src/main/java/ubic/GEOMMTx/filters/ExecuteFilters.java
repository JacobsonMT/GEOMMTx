package ubic.GEOMMTx.filters;

import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.ProjectRDFModelTools;

import com.hp.hpl.jena.rdf.model.Model;

public class ExecuteFilters {
    protected static Log log = LogFactory.getLog( ExecuteFilters.class );

    /**
     * @param args
     */
    public static void main( String argsp[] ) throws Exception {
        Model model = ProjectRDFModelTools.loadModel( "mergedRDFBirnLexUpdateNoExp.rdf" );
        //Model model = ProjectRDFModelTools.loadModel( "mergedRDFBirnLexUpdate.afterrejected.testing.rdf" );

        // test
        List<AbstractFilter> filters = new LinkedList<AbstractFilter>();
        //filters.add( new ExperimentalFactorFilter() );
//        filters.add( new CUISUIFilter() );
//        filters.add( new CUIIRIFilter() );
        filters.add( new BIRNLexFMANullsFilter() );
        filters.add( new FrequentFilter() );
        // low score filter not used

        for ( AbstractFilter filter : filters ) {
            log.info( "Mentions:" + ProjectRDFModelTools.getMentionCount( model ) );
            log.info( "Running: " + filter.getName() );
            int result = filter.filter( model );
            log.info( "Removed: " + result );
        }
        model.write( new FileWriter( "mergedRDFBirnLexUpdateNoExp.afterUseless.rdf" ) );
        log.info( "Final Mentions:" + ProjectRDFModelTools.getMentionCount( model ) );

    }

}
