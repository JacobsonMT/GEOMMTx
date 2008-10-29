package ubic.GEOMMTx.evaluation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ubic.GEOMMTx.ExpressionExperimentAnntotator;
import ubic.GEOMMTx.LabelLoader;
import ubic.GEOMMTx.SetupParameters;
import ubic.GEOMMTx.ProjectRDFModelTools;
import ubic.GEOMMTx.mappers.BirnLexMapper;
import ubic.GEOMMTx.mappers.DiseaseOntologyMapper;
import ubic.GEOMMTx.mappers.FMALiteMapper;
import ubic.gemma.model.common.description.Characteristic;
import ubic.gemma.model.common.description.VocabCharacteristic;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.ontology.OntologyTools;
import ubic.gemma.util.AbstractSpringAwareCLI;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;

public class CompareToManual extends AbstractSpringAwareCLI {

    Map<String, String> labels;
    Map<String, Set<String>> manualURLs;
    Map<String, Set<String>> mmtxURLs;
    HashSet<String> originalMMTxIDs;
    String filename;
    boolean specificLabels = false;

    public Set<String> getIntersectExperiments() {
        Set<String> intersect = new HashSet<String>( manualURLs.keySet() );
        intersect.retainAll( mmtxURLs.keySet() );
        return intersect;
    }

    public void makeSpreadSheet() throws Exception {
        Map<String, Set<String>> newPredictions = new HashMap<String, Set<String>>();
        for ( String dataset : originalMMTxIDs ) {
            Set<String> machineURLs = new HashSet<String>( mmtxURLs.get( dataset ) );
            Set<String> humanURLs = manualURLs.get( dataset );
            machineURLs.removeAll( humanURLs );
            newPredictions.put( dataset, machineURLs );
        }

        CheckHighLevelSpreadSheet spreadsheet = new CheckHighLevelSpreadSheet( "HighLevelPredictionsPlusOne2.xls" );
        spreadsheet.populate( newPredictions, labels, 101 );
        spreadsheet.save();
    }

    public CompareToManual() {
        labels = new HashMap<String, String>();
    }

    @Override
    protected void buildOptions() {
    }

    public static void main( String[] args ) {
        CompareToManual p = new CompareToManual();

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
        // System.out.println(getMentionCount( "mergedRDF.firstrun.rdf"));
        // System.out.println(getMentionCount( "mergedRDFBirnLexUpdate.rdf"));
        // System.out.println(getMentionCount( "mergedRDFBirnLexUpdateNoExp.rdf"));
        // System.out.println(getMentionCount( "mergedRDFBirnLexUpdate.afterrejected.rdf"));
        // System.out.println(getMentionCount( "mergedRDFBirnLexUpdate.afterUseless.rdf"));
        // System.exit(1);

        // FOR SECOND RUN switch OrganismalTaxonomy in cleanURL's
        // filename = "mergedRDF.rejected.removed.rdf"; //second run
        filename = "mergedRDFBirnLexUpdate.afterUseless.rdf"; // first run

        long totaltime = System.currentTimeMillis();
        Exception err = processCommandLine( "GEOMMTx ", args );
        if ( err != null ) return err;

        // long time = System.currentTimeMillis();

        // get human and mmtx mappings
        getMappings( filename );

        log.info( "gemma intersect mmtx size=" + getIntersectExperiments().size() );
        // log.info( intersect );

        Set<String> minus = new HashSet<String>( mmtxURLs.keySet() );
        minus.removeAll( manualURLs.keySet() );
        log.info( "mmtx minus gemma size=" + minus.size() );
        log.info( minus );
        // getHumanMappingsFromServer();
        setNullstoEmpty();
        cleanURLs();

        // writeExperimentTitles();

        printStats();

        //printSourceStats();

        // print100Stats();

        // howManyMissingMappings();

        // howManyMissingMappings();

        printMMTxForTagCloud();

        printHumanForTagCloud();
        
        printMissedForTagCloud();

        // filterAndPrint( "/owl/FMA#" );
        // filterAndPrint( "/owl/DOID#" );
        // filterAndPrint( "birnlex" );

        // printStats();

        // ParentFinder parentFinder = new ParentFinder();
        // try {
        // parentFinder.init();
        // } catch ( Exception e ) {
        // e.printStackTrace();
        // }
        // mmtxURLs = parentFinder.expandToParents( mmtxURLs );
        //
        // System.out.println( "Children/leaves stats" );
        // printStats();
        //
        // mmtxURLs = parentFinder.reduceToLeaves( mmtxURLs );
        // System.out.println( "Leaves only" );
        // printStats();
        //
        // System.out.println( "Nulls: " + parentFinder.nullTerms );

        // for ( String dataset : originalMMTxIDs ) {
        // System.out.println( "-----------------------------------" );
        // System.out.println( "ID" + dataset );
        // showMe( dataset );
        // }

        // printComparisonsCSV();

        // printMissedURLs();

        // try {
        // makeSpreadSheet();
        // } catch ( Exception e ) {
        // e.printStackTrace();
        // }

        // printROCCurveValues( filename );

        System.out.println( "Total time:" + ( System.currentTimeMillis() - totaltime ) / 1000 + "s" );
        return null;
    }

    private void filterAndPrint( String filterString ) {
        getMappings( filename );
        setNullstoEmpty();
        cleanURLs();
        log.info( filterString );
        filterAllURLs( filterString );
        printStats();
    }

    private void getMappings( String filename ) {
        // get the human mappings
        loadHumanMappings();

        // get the labels
        // LabelLoader labelLoader = new LabelLoader();
        try {
            labels = LabelLoader.readLabels();
        } catch ( Exception e ) {
            log.warn( "Couldnt load labels" );
            e.printStackTrace();
            System.exit( 1 );
        }

        mmtxURLs = ProjectRDFModelTools.getURLsExperiments( filename );

        originalMMTxIDs = new HashSet<String>( mmtxURLs.keySet() );

        log.info( "mmtx size=" + mmtxURLs.size() );
        log.info( "gemma size=" + manualURLs.size() );
    }

    // if the dataset is not processed by humans or mmtx, then set its annotations to empty set
    public void setNullstoEmpty() {
        Set<String> datasets = new HashSet<String>( mmtxURLs.keySet() );
        datasets.addAll( manualURLs.keySet() );
        for ( String dataset : datasets ) {
            if ( mmtxURLs.get( dataset ) == null ) {
                mmtxURLs.put( dataset, new HashSet<String>() );
            }
            if ( manualURLs.get( dataset ) == null ) {
                manualURLs.put( dataset, new HashSet<String>() );
            }

        }
    }

    /*
     * Removes null URL's and also URL's from CHEBI Birnlex organismal taxonomy MGED Ontology
     */
    public void cleanURLs() {
        Set<String> datasets = new HashSet<String>( mmtxURLs.keySet() );
        datasets.addAll( manualURLs.keySet() );
        for ( String dataset : datasets ) {
            // Set<String> machineURLs = mmtxURLs.get( dataset );
            Set<String> humanURLs = manualURLs.get( dataset );
            humanURLs.remove( "null" );
            humanURLs.remove( null );
            humanURLs.remove( "" );

            // get rid of MGED URL's
            List<String> removeMe = new LinkedList<String>();
            for ( String url : humanURLs ) {
                if ( url.contains( "MGEDOntology.owl" ) ) {
                    removeMe.add( url );
                }
                if ( url.contains( "owl/CHEBI" ) ) {
                    removeMe.add( url );
                }
                // I didn't have this on the first run, so change it
                if ( url.contains( "OrganismalTaxonomy" ) ) {
                    removeMe.add( url );
                }
            }
            humanURLs.removeAll( removeMe );
        }
    }

    public void filterHumanURLs( String keepString ) {
        filter( keepString, manualURLs );
    }

    public void filterAllURLs( String keepString ) {
        filter( keepString, manualURLs );
        filter( keepString, mmtxURLs );
    }

    private void filter( String keepString, Map<String, Set<String>> map ) {
        for ( String dataset : map.keySet() ) {
            Set<String> URLs = map.get( dataset );
            List<String> removeMe = new LinkedList<String>();
            for ( String url : URLs ) {
                if ( url.contains( keepString ) ) {
                    // keep it
                } else {
                    removeMe.add( url );
                }
            }
            URLs.removeAll( removeMe );
        }
    }

    public Set<String> getIntersection( String dataset ) {
        Set<String> machineURLs = mmtxURLs.get( dataset );
        Set<String> humanURLs = manualURLs.get( dataset );
        Set<String> intersect = new HashSet<String>( humanURLs );
        intersect.retainAll( machineURLs );
        return intersect;
    }

    public Set<String> getMissed( String dataset ) {
        Set<String> machineURLs = mmtxURLs.get( dataset );
        Set<String> humanURLs = manualURLs.get( dataset );
        Set<String> missed = new HashSet<String>( humanURLs );
        missed.removeAll( machineURLs );
        return missed;
    }

    public Set<String> convertURLsToLabels( Set<String> URLs ) {
        Set<String> result = new HashSet<String>();
        for ( String url : URLs )
            result.add( labels.get( url ) );
        return result;
    }

    public Set<String> getIntersectionByName( String dataset ) {
        Set<String> machineLabels = convertURLsToLabels( mmtxURLs.get( dataset ) );
        Set<String> humanLabels = convertURLsToLabels( manualURLs.get( dataset ) );
        Set<String> intersect = new HashSet<String>( humanLabels );
        intersect.retainAll( machineLabels );
        return intersect;
    }

    private void printSourceStats() {
        List<String> allSources = new LinkedList<String>();
        List<String> intersectSources = new LinkedList<String>();
        DescriptionExtractor de = new DescriptionExtractor( filename );

        for ( String dataset : originalMMTxIDs ) {
            Set<String> machineURLs = mmtxURLs.get( dataset );
            Set<String> intersect = getIntersection( dataset );
            allSources.addAll( de.getDecriptionType( dataset, machineURLs ) );
            intersectSources.addAll( de.getDecriptionType( dataset, intersect ) );
        }
        // crunch them down to a hash
        System.out.println( "== all MMTx predictions ==" );
        printMap( listToFrequencyMap( allSources ) );

        System.out.println( "== all MMTx predictions that matched existing ==" );
        printMap( listToFrequencyMap( intersectSources ) );
    }

    public void printMap( Map<String, Integer> map ) {
        int total = 0;
        for ( String key : map.keySet() ) {
            total += map.get( key );
        }
        for ( String key : map.keySet() ) {
            System.out.println( key + " => " + map.get( key ) + "(" + ( float ) map.get( key ) / ( float ) total + ")" );
        }
    }

    public Map<String, Integer> listToFrequencyMap( List<String> input ) {
        Map<String, Integer> result = new HashMap<String, Integer>();
        int i;
        for ( String s : input ) {
            Integer iO = result.get( s );
            if ( iO == null )
                i = 0;
            else
                i = iO.intValue();
            result.put( s, i + 1 );
        }
        return result;
    }

    private void printStats() {
        int totalHuman = 0, totalMachine = 0, totalIntersect = 0;
        Set<String> uniqueHuman = new HashSet<String>();
        Set<String> uniqueMachine = new HashSet<String>();
        Set<String> uniqueIntersect = new HashSet<String>();
        for ( String dataset : originalMMTxIDs ) {
            Set<String> machineURLs = mmtxURLs.get( dataset );
            Set<String> humanURLs = manualURLs.get( dataset );
            Set<String> intersect = getIntersection( dataset );
            uniqueHuman.addAll( humanURLs );
            uniqueMachine.addAll( machineURLs );
            uniqueIntersect.addAll( intersect );

            totalMachine += machineURLs.size();
            totalHuman += humanURLs.size();
            totalIntersect += intersect.size();
        }
        System.out.println( "Human total:" + totalHuman + " Unique:" + uniqueHuman.size() );
        System.out.println( "Machine:" + totalMachine + " Unique:" + uniqueMachine.size() );
        System.out.println( "Intersect:" + totalIntersect + " Unique:" + uniqueIntersect.size() );
        System.out.println( "Recall:" + totalIntersect / ( float ) totalHuman );
        System.out.println( "Precision:" + totalIntersect / ( float ) totalMachine );
    }

    private void printComparisonsCSV() {
        System.out.println( "ID, machineURLs.size()" + "," + "humanURLs.size()" + "," + "intersect.size()" );
        for ( String dataset : originalMMTxIDs ) {
            Set<String> machineURLs = mmtxURLs.get( dataset );
            Set<String> humanURLs = manualURLs.get( dataset );
            Set<String> intersect = getIntersection( dataset );
            System.out.println( dataset + "," + machineURLs.size() + "," + humanURLs.size() + "," + intersect.size() );
        }
    }

    private Map<String, Set<String>> getMissedURLS() {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        for ( String dataset : originalMMTxIDs ) {
            result.put( dataset, getMissed( dataset ) );
        }
        return result;
    }

    private void printMissedURLs() {
        Map<String, Integer> missed = new HashMap<String, Integer>();
        for ( Set<String> missedURLs : getMissedURLS().values() ) {
            for ( String URI : missedURLs ) {
                Integer i = missed.get( URI );
                if ( i == null )
                    i = 1;
                else
                    i++;
                missed.put( URI, i );
            }
        }

        for ( String URI : missed.keySet() ) {
            System.out.println( labels.get( URI ) + "|" + URI + "|" + missed.get( URI ) );
        }
    }

    private Map<String, Set<String>> getHumanMappingsFromDisk() throws Exception {
        Map<String, Set<String>> result;
        ObjectInputStream o = new ObjectInputStream( new FileInputStream( SetupParameters.config
                .getString( "gemma.annotator.cachedGemmaAnnotations" ) ) );
        result = ( Map<String, Set<String>> ) o.readObject();
        o.close();

        log.info( "Loaded Gemma annotations from local disk" );
        return result;
    }

    private void saveHumanMappingsToDisk() {
        log.info( "Saved mappings" );
        try {
            ObjectOutputStream o = new ObjectOutputStream( new FileOutputStream( "annotator.mappings" ) );
            o.writeObject( manualURLs );
            o.close();

            // depreciated
            // ObjectOutputStream o2 = new ObjectOutputStream( new FileOutputStream( "label.mappings" ) );
            // o2.writeObject( labels );
            // o2.close();

            log.info( "Saved manual annotations" );
        } catch ( Exception e ) {
            log.info( "cannot save CUI mappings" );
            e.printStackTrace();
            System.exit( 1 );
        }
    }

    // return this.getClass().getName() + ".mappings";

    private void loadHumanMappings() {
        try {
            manualURLs = getHumanMappingsFromDisk();
        } catch ( Exception e ) {
            log.info( "gettings annotations from local cache failed, getting from server" );
            manualURLs = getHumanMappingsFromServer();
            saveHumanMappingsToDisk();
        }
    }

    private Map<String, Set<String>> getHumanMappingsFromServer() {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        ExpressionExperimentService ees = ( ExpressionExperimentService ) this.getBean( "expressionExperimentService" );
        Collection<ExpressionExperiment> experiments = ees.loadAll();

        int c = 0;
        for ( ExpressionExperiment experiment : experiments ) {
            c++;
            // if (c == 30) break;
            log.info( "Experiment number:" + c + " of " + experiments.size() + " ID:" + experiment.getId() );

            ees.thawLite( experiment );

            // if its mouse then keep it
            // Taxon taxon = ees.getTaxon( experiment.getId() );
            // if ( !TaxonUtility.isMouse( taxon ) ) {
            // continue;
            // }

            Collection<Characteristic> characters = experiment.getCharacteristics();

            Set<String> currentURL = new HashSet<String>();
            result.put( experiment.getId() + "", currentURL );

            for ( Characteristic ch : characters ) {
                if ( ch instanceof VocabCharacteristic ) {
                    VocabCharacteristic vc = ( VocabCharacteristic ) ch;
                    currentURL.add( vc.getValueUri() );
                    // if ( specificLabels ) {
                    // labels.put( vc.getValueUri(), vc.getValue() + "[Gemma]" );
                    // } else {
                    // labels.put( vc.getValueUri(), vc.getValue() );
                    // }
                }
            }

        }
        return result;
    }

    private void writeExperimentTitles() {
        writeExperimentTitles( SetupParameters.config.getString( "gemma.annotator.gemmaTitles" ) );
    }

    private void writeExperimentTitles( String filename ) {
        ExpressionExperimentService ees = ( ExpressionExperimentService ) this.getBean( "expressionExperimentService" );
        Collection<ExpressionExperiment> experiments = ees.loadAll();
        Model model = ModelFactory.createDefaultModel();

        int c = 0;
        for ( ExpressionExperiment experiment : experiments ) {
            c++;
            // if (c == 30) break;
            Long ID = experiment.getId();

            log.info( "Experiment number:" + c + " of " + experiments.size() + " ID:" + experiment.getId() );

            ees.thawLite( experiment );

            String GEOObjectURI = ExpressionExperimentAnntotator.gemmaNamespace + "experiment/" + ID;
            Resource expNode = model.createResource( GEOObjectURI );
            expNode.addProperty( DC.title, experiment.getName() );
        }
        try {
            model.write( new FileWriter( filename ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void printROCCurveValues( String filename ) {
        // table that has scores mapped to how many predictions were made at the score, and how many were correct
        Map<Integer, Integer> intersections = new HashMap<Integer, Integer>();
        Map<Integer, Integer> predictions = new HashMap<Integer, Integer>();
        // from RDF
        String queryString = "PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX gemmaAnn: <http://bioinformatics.ubc.ca/Gemma/ws/xml/gemmaAnnotations.owl#>\n                              "
                + "\n                                                            "
                + "SELECT DISTINCT ?dataset ?mapping ?score\n                                                            "
                + "WHERE {\n                                                            "
                + "    ?dataset gemmaAnn:describedBy ?description .\n                                                            "
                + "    ?description gemmaAnn:hasPhrase ?phrase .\n                                                            "
                + "    ?phrase gemmaAnn:hasMention ?mention .\n                                                            "
                + "    ?mention gemmaAnn:mappedTerm ?mapping .\n                                                            "
                + "    ?mention gemmaAnn:hasScore ?score .\n                                                            "
                + "    ?mention rdf:label ?label .\n" + "}";

        Model model = ProjectRDFModelTools.loadModel( filename );

        Query q = QueryFactory.create( queryString );
        // go through them all and put in excel file
        QueryExecution qexec = QueryExecutionFactory.create( q, model );

        int row = 1;
        ResultSet results = qexec.execSelect();
        log.info( "Query executed" );

        // put it into a set
        Map<String, Integer> highest = new HashMap<String, Integer>();

        while ( results.hasNext() ) {
            QuerySolution qTemp = results.nextSolution();

            // for ( QuerySolution qTemp : tempSolns ) {
            int score = Integer.parseInt( OntologyTools.varToString( "score", qTemp ) );
            String dataset = OntologyTools.varToString( "dataset", qTemp );
            dataset = dataset.substring( dataset.lastIndexOf( '/' ) + 1 );
            String URL = OntologyTools.varToString( "mapping", qTemp );
            String key = URL + "|" + dataset; // ugly
            if ( highest.get( key ) == null || highest.get( key ) < score ) {
                // put the higher score
                highest.put( key, score );
            }
        }
        log.info( "highest size:" + highest.size() );
        qexec.close();

        QueryExecution qexec2 = QueryExecutionFactory.create( q, model );
        ResultSet results2 = qexec2.execSelect();
        // results.
        while ( results2.hasNext() ) {
            QuerySolution soln = results2.nextSolution();
            // for ( QuerySolution soln : solns ) {
            row++;
            String dataset = OntologyTools.varToString( "dataset", soln );
            dataset = dataset.substring( dataset.lastIndexOf( '/' ) + 1 );
            int score = Integer.parseInt( OntologyTools.varToString( "score", soln ) );
            String URL = OntologyTools.varToString( "mapping", soln );

            String key = URL + "|" + dataset; // ugly
            if ( highest.get( key ) == score ) {
                // dont do this one again
                highest.put( key, score + 1 );

                Integer predictionsForScore = predictions.get( score );
                if ( predictionsForScore == null ) {
                    predictions.put( score, 1 );
                } else {
                    predictions.put( score, 1 + predictionsForScore );
                }

                if ( intersections.get( score ) == null ) intersections.put( score, 0 );

                // if it is correct
                if ( manualURLs.get( dataset ).contains( URL ) ) {
                    intersections.put( score, intersections.get( score ) + 1 );
                }
            }
        }
        System.out.println( "score, predictions, correct" );
        for ( int score : predictions.keySet() ) {
            System.out.println( score + "," + predictions.get( score ) + "," + intersections.get( score ) );
        }
    }

    public void showMe( String experimentID ) {
        Set<String> machineURLs = new HashSet<String>( mmtxURLs.get( experimentID ) );
        Set<String> humanURLs = new HashSet<String>( manualURLs.get( experimentID ) );
        Set<String> intersect = getIntersection( experimentID );

        machineURLs.removeAll( intersect );
        humanURLs.removeAll( intersect );

        System.out.println( "MMTx URLs:" );
        System.out.println( lineSpacedSet( machineURLs ) );
        System.out.println( "Human URLs:" );
        System.out.println( lineSpacedSet( humanURLs ) );
        if ( intersect.size() == 0 ) {
            System.out.println( "No Intersection URLs" );
        } else {

            System.out.println( "Intersection URLs:" );
            System.out.println( lineSpacedSet( intersect ) );
        }
    }

    public String lineSpacedSet( Set<String> input ) {
        List<String> outputList = new LinkedList<String>();
        for ( String line : input ) {
            outputList.add( labels.get( line ) + "->" + line );
        }
        Collections.sort( outputList );

        String result = "";
        for ( String line : outputList )
            result += line + "\n";
        return result;
    }

    public void printForTagCloud( Map<String, Set<String>> experiments ) {
        int nulls = 0;
        for ( String exp : experiments.keySet() ) {
            for ( String url : experiments.get( exp ) ) {
                // System.out.print( url + "->" );
                if ( labels.get( url ) == null ) {
                    nulls++;
                    // log.info(url);
                    continue;
                }
                System.out.println( labels.get( url ).replace( " ", "~" ) );
            }
        }
        log.info( "Number of null labels:" + nulls );
    }

    public void printMMTxForTagCloud() {
        System.out.println( "--------MMTx----------" );
        printForTagCloud( mmtxURLs );
    }

    public void printMissedForTagCloud() {
        System.out.println( "--------MISSED----------" );
        printForTagCloud( getMissedURLS() );
    }

    public void printHumanForTagCloud() {
        System.out.println( "--------HUMAN----------" );
        printForTagCloud( manualURLs );
    }

    protected void processOptions() {
        super.processOptions();
    }

    public Collection<String> removeFromHumanSeen( Set<String> removeSet ) {
        // List<String> seenHumanURLs = new LinkedList<String>();
        Set<String> seenHumanURLs = new HashSet<String>();

        for ( Set<String> seenURLs : manualURLs.values() ) {
            seenHumanURLs.addAll( seenURLs );
        }
        seenHumanURLs.removeAll( removeSet );
        return seenHumanURLs;
    }

    /*
     * Finds out how many mappings we fail to have for the human predictions
     */
    public void howManyMissingMappings() {
        Collection<String> result;

        filterAndPrint( "/owl/FMA#" );
        FMALiteMapper fma = new FMALiteMapper();
        result = removeFromHumanSeen( fma.getAllURLs() );
        System.out.println( "Seen manual FMA URL's that we have no mapping to:" + result.size() );

        filterAndPrint( "/owl/DOID#" );
        DiseaseOntologyMapper DO = new DiseaseOntologyMapper();
        result = removeFromHumanSeen( DO.getAllURLs() );
        System.out.println( "Seen manual DO URL's that we have no mapping to:" + result.size() );

        filterAndPrint( "birnlex" );
        BirnLexMapper BIRN = new BirnLexMapper();
        result = removeFromHumanSeen( BIRN.getAllURLs() );
        System.out.println( "Seen manual BIRN URL's that we have no mapping to:" + result.size() );

        // Reset
        getMappings( filename );
        setNullstoEmpty();
        cleanURLs();
    }

    public void print100Stats() {
        // the below expID's are the ones we choose for manual curation
        String[] exps100 = new String[] { "107", "114", "129", "137", "140", "155", "159", "167", "198", "199", "2",
                "20", "206", "211", "213", "216", "219", "221", "232", "241", "243", "245", "246", "257", "258", "26",
                "265", "267", "268", "277", "288", "295", "299", "302", "319", "323", "35", "36", "363", "368", "369",
                "374", "375", "380", "385", "389", "39", "403", "406", "446", "454", "455", "484", "49", "504", "510",
                "522", "524", "528", "533", "535", "54", "544", "548", "559", "571", "579", "587", "588", "591", "595",
                "596", "597", "6", "602", "606", "609", "613", "617", "619", "625", "627", "633", "639", "64", "647",
                "651", "653", "657", "66", "663", "667", "672", "699", "74", "76", "79", "80", "90", "95" };
        originalMMTxIDs = new HashSet<String>( Arrays.asList( exps100 ) );
        printStats();
    }

}
