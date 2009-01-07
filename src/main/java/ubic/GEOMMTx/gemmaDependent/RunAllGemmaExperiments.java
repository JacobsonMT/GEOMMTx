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
package ubic.GEOMMTx.gemmaDependent;

import java.util.Collection;

import ubic.GEOMMTx.Text2Owl;
import ubic.GEOMMTx.mappers.BirnLexMapper;
import ubic.GEOMMTx.mappers.DiseaseOntologyMapper;
import ubic.GEOMMTx.mappers.FMALiteMapper;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.util.AbstractSpringAwareCLI;

/**
 * Runs though all experiments in Gemma and produces a RDF file for each. Right now it does not use any filters on the
 * resulting mentions.
 * 
 * @author Leon
 */
public class RunAllGemmaExperiments extends AbstractSpringAwareCLI {

    public static void main( String[] args ) {
        RunAllGemmaExperiments p = new RunAllGemmaExperiments();

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

    private Text2Owl text2Owl;

    public RunAllGemmaExperiments() {
    }

    @Override
    protected void buildOptions() {
    }

    @Override
    protected Exception doWork( String[] args ) {
        long totaltime = System.currentTimeMillis();
        Exception err = processCommandLine( "GEOMMTx ", args );
        if ( err != null ) return err;

        ExpressionExperimentService ees = ( ExpressionExperimentService ) this.getBean( "expressionExperimentService" );
        Collection<ExpressionExperiment> experiments = ees.loadAll();

        long time = System.currentTimeMillis();

        loadText2Owl();

        System.out.println( "Total initialization time:" + ( System.currentTimeMillis() - time ) / 1000 + "s" );

        int c = 0;
        int badWrites = 0;
        for ( ExpressionExperiment experiment : experiments ) {
            c++;

            log.info( "Experiment number:" + c + " of " + experiments.size() + " ID:" + experiment.getId() );

            time = System.currentTimeMillis();

            ees.thawLite( experiment );

            ExpressionExperimentAnntotator experimentAnn = new ExpressionExperimentAnntotator( experiment, text2Owl );

            experimentAnn.annotateAll();

            try {
                experimentAnn.writeModel();
            } catch ( Exception e ) {
                badWrites++;
                e.printStackTrace();
            }

            // write it out to save memory
            log.info( "--------------------------------------------" );
            log.info( ( ( System.currentTimeMillis() - time ) / 1000 ) + "s for whole experiment, writing out" );
        }

        log.info( badWrites + " failed model writes" );

        System.out.println( "Total time:" + ( System.currentTimeMillis() - totaltime ) / 1000 + "s" );
        return null;
    }

    @Override
    protected void processOptions() {
        super.processOptions();
    }

    /**
     * Hopefully this resets the memory leaks in MMTx
     */
    private void loadText2Owl() {
        text2Owl = null;
        System.gc();
        text2Owl = new Text2Owl();
        text2Owl.addMapper( new BirnLexMapper() );
        text2Owl.addMapper( new FMALiteMapper() );
        text2Owl.addMapper( new DiseaseOntologyMapper() );
    }
}