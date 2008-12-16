/*
 * The Gemma project
 * 
 * Copyright (c) 2007 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
        Model model = ProjectRDFModelTools.loadModel( "mergedRDFBirnLexUpdate.afterUseless.axon4.rdf" );
        //Model model = ProjectRDFModelTools.loadModel( "mergedRDFBirnLexUpdate.afterrejected.testing.rdf" );

        // test
        List<AbstractFilter> filters = new LinkedList<AbstractFilter>();
        filters.add( new ExperimentalFactorFilter() );
        filters.add( new CUISUIFilter() );
        filters.add( new CUIIRIFilter() );
        filters.add( new BIRNLexFMANullsFilter() );
        filters.add( new UninformativeFilter() );
        // low score filter not used

        for ( AbstractFilter filter : filters ) {
            log.info( "Mentions:" + ProjectRDFModelTools.getMentionCount( model ) );
            log.info( "Running: " + filter.getName() );
            int result = filter.filter( model );
            log.info( "Removed: " + result );
        }
        model.write( new FileWriter( "mergedRDFBirnLexUpdate.afterUseless.axon4.filtered.rdf" ) );
        log.info( "Final Mentions:" + ProjectRDFModelTools.getMentionCount( model ) );

    }

}
