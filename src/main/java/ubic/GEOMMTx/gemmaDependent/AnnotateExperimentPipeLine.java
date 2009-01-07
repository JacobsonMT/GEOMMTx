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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.LabelLoader;
import ubic.GEOMMTx.ProjectRDFModelTools;
import ubic.GEOMMTx.Text2Owl;
import ubic.GEOMMTx.filters.AbstractFilter;
import ubic.GEOMMTx.filters.CUIIRIFilter;
import ubic.GEOMMTx.filters.CUISUIFilter;
import ubic.GEOMMTx.filters.UninformativeFilter;
import ubic.GEOMMTx.mappers.BirnLexMapper;
import ubic.GEOMMTx.mappers.DiseaseOntologyMapper;
import ubic.GEOMMTx.mappers.FMALiteMapper;
import ubic.gemma.apps.ExpressionExperimentManipulatingCLI;
import ubic.gemma.model.common.description.Characteristic;
import ubic.gemma.model.common.description.VocabCharacteristic;
import ubic.gemma.model.expression.experiment.BioAssaySet;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.ontology.OntologyService;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * A class that starts with a experiment ID and in the end outputs the predicted annotation URL's, after filtering
 * 
 * @author leon
 * @version $Id$
 */
public class AnnotateExperimentPipeLine extends ExpressionExperimentManipulatingCLI {
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

    private Text2Owl text2Owl;
    protected static Log log = LogFactory.getLog( AnnotateExperimentPipeLine.class );
    boolean loadOntologies = false;

    private List<AbstractFilter> filters;

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
            filters.add( new UninformativeFilter() );
        } catch ( Exception e ) {
            // TODO
            e.printStackTrace();
            log.error( e.getMessage() );
        }
    }

    /*
     * clears the cache of the MMTx runner, usefull for benchmarking multiple runs
     */
    public void clearMMTxCache() {
        text2Owl.clearCache();
    }

    public Set<String> getAnnotations( ExpressionExperiment e ) {
        long time = System.currentTimeMillis();

        Set<String> finalAnnotations;

        ExpressionExperimentAnntotator experimentAnn = new ExpressionExperimentAnntotator( e, text2Owl );

        // go through each text source one by one
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

        // all the above calls for each text source builds a RDF model
        Model model = experimentAnn.getModel();

        // apply the filters one by one
        for ( AbstractFilter filter : filters ) {
            // log.info( "Mentions:" + Text2OwlModelTools.getMentionCount( model ) );
            // log.info( "Running: " + filter.getName() );
            int result = filter.filter( model );
            // log.info( "Removed: " + result );
        }
        log.info( "Final Mentions:" + ProjectRDFModelTools.getMentionCount( model ) );

        // write the file somewhere? we may also want to write the file before it's filtered
        try {
            experimentAnn.writeModel();
        } catch ( Exception ee ) {
            ee.printStackTrace();
            log.error( ee.getMessage() );
        }
        log.info( ( ( System.currentTimeMillis() - time ) / 1000 ) + "s for whole experiment" );

        // convert the mentions into annotations using a sparql query
        finalAnnotations = ProjectRDFModelTools.getURLsFromSingle( model );

        return finalAnnotations;
    }

    @Override
    protected Exception doWork( String[] args ) {

        Exception err = processCommandLine( "Expression experiment annotator pipeline", args );
        if ( err != null ) return err;

        ExpressionExperimentService ees = ( ExpressionExperimentService ) this.getBean( "expressionExperimentService" );
        OntologyService os = ( OntologyService ) this.getBean( "ontologyService" );

        long time = System.currentTimeMillis();
        time = System.currentTimeMillis();

        for ( BioAssaySet bas : this.expressionExperiments ) {

            log.info( "Processing: " + bas );

            ExpressionExperiment experiment = ( ExpressionExperiment ) bas;
            ees.thawLite( experiment );

            // get the rdfs:labels for the URI's
            Map<String, String> labels = null;
            try {
                labels = LabelLoader.readLabels();
            } catch ( Exception e ) {
                log.warn( "Couldnt load labels" );
            }
            // construct a factory for producing VocabCharacteristics
            PredictedCharacteristicFactory charGen = new PredictedCharacteristicFactory( labels );

            // The call that does all the work, it gets the predicted annotations
            Set<String> predictedAnnotations = getAnnotations( experiment );

            // for each URI print it and it's label and get VocabCharacteristic to represent it
            for ( String URI : predictedAnnotations ) {
                log.info( experiment + " -> " + labels.get( URI ) + " - " + URI );

                Characteristic c = charGen.getCharacteristic( URI );

                // attach the Characterisitic to the experiment
//                os.saveExpressionExperimentStatement( c, experiment );

            }
        }
        System.out.println( "Total Time:" + ( System.currentTimeMillis() - time ) );
        return null;
    }

    @Override
    protected void processOptions() {
        super.processOptions();
    }

}
