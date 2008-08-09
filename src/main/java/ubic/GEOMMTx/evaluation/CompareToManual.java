package ubic.GEOMMTx.evaluation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ubic.GEOMMTx.Text2OwlModelTools;
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

        CheckHighLevelSpreadSheet spreadsheet = new CheckHighLevelSpreadSheet( "HighLevelPredictions.xls" );
        spreadsheet.populate( newPredictions, labels, 100 );
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
        filename = "mergedRDF.rejected.removed.rdf";
        // filename = "mergedRDFBirnLexUpdate.afterUseless.rdf";
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

        setNullstoEmpty();
        cleanURLs();

        printStats();

        // filterAndPrint( "/owl/FMA#" );
        // filterAndPrint( "/owl/DOID#" );
        // filterAndPrint( "birnlex" );

        for ( String dataset : originalMMTxIDs ) {
            System.out.println( "-----------------------------------" );
            System.out.println( "ID" + dataset );
            showMe( dataset );
        }

        printComparisonsCSV();

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
        // get the mappings

        loadHumanMappings();

        mmtxURLs = getMMTXMappings( filename );
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
            Set<String> machineURLs = mmtxURLs.get( dataset );
            Set<String> humanURLs = manualURLs.get( dataset );
            machineURLs.remove( "" );
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

    private void printStats() {
        int totalHuman = 0, totalMachine = 0, totalIntersect = 0;
        for ( String dataset : originalMMTxIDs ) {
            Set<String> machineURLs = mmtxURLs.get( dataset );
            Set<String> humanURLs = manualURLs.get( dataset );
            Set<String> intersect = getIntersection( dataset );

            totalHuman += humanURLs.size();
            totalMachine += machineURLs.size();
            totalIntersect += intersect.size();
        }
        System.out.println( "Human total:" + totalHuman );
        System.out.println( "Machine:" + totalMachine );
        System.out.println( "Intersect:" + totalIntersect );
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

    private Map<String, Set<String>> getHumanMappingsFromDisk() throws Exception {
        Map<String, Set<String>> result;
        ObjectInputStream o = new ObjectInputStream( new FileInputStream( "annotator.mappings" ) );
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
            Collection<Characteristic> characters = experiment.getCharacteristics();

            Set<String> currentURL = new HashSet<String>();
            result.put( experiment.getId() + "", currentURL );

            for ( Characteristic ch : characters ) {
                if ( ch instanceof VocabCharacteristic ) {
                    VocabCharacteristic vc = ( VocabCharacteristic ) ch;
                    currentURL.add( vc.getValueUri() );
                    if ( specificLabels ) {
                        labels.put( vc.getValueUri(), vc.getValue() + "[Gemma]" );
                    } else {
                        labels.put( vc.getValueUri(), vc.getValue() );
                    }
                }
            }

        }
        return result;
    }

    private void printROCCurveValues( String filename ) {
        // table that has scores mapped to how many predictions were made at the score, and how many were correct
        Map<Integer, Integer> intersections = new HashMap<Integer, Integer>();
        Map<Integer, Integer> predictions = new HashMap<Integer, Integer>();
        // from RDF
        String queryString = "PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX leon: <http://www.purl.org/leon/umls#>\n                              "
                + "\n                                                            "
                + "SELECT DISTINCT ?dataset ?mapping ?score\n                                                            "
                + "WHERE {\n                                                            "
                + "    ?dataset leon:describedBy ?description .\n                                                            "
                + "    ?description leon:hasPhrase ?phrase .\n                                                            "
                + "    ?phrase leon:hasMention ?mention .\n                                                            "
                + "    ?mention leon:mappedTerm ?mapping .\n                                                            "
                + "    ?mention leon:hasScore ?score .\n                                                            "
                + "    ?mention rdf:label ?label .\n" + "}";

        Model model = Text2OwlModelTools.loadModel( filename );

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

    public Map<String, Set<String>> getMMTXMappings( String filename ) {

        Map<String, Set<String>> result = new HashMap<String, Set<String>>();

        // from RDF
        String queryString = "PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX leon: <http://www.purl.org/leon/umls#>\n                              "
                + "\n                                                            "
                + "SELECT ?dataset ?label ?mapping\n                                                            "
                + "\n"
                + "WHERE {\n                                                            "
                + "    ?dataset leon:describedBy ?description .\n                                                            "
                + "    ?description leon:hasPhrase ?phrase .\n                                                            "
                + "    ?phrase leon:hasMention ?mention .\n                                                            "
                + "    ?mention leon:mappedTerm ?mapping .\n                                                            "
                + "    ?mention rdf:label ?label .\n" + "}";

        Model model = Text2OwlModelTools.loadModel( filename );

        Query q = QueryFactory.create( queryString );
        // go through them all and put in excel file
        QueryExecution qexec = QueryExecutionFactory.create( q, model );

        int row = 1;
        ResultSet results = qexec.execSelect();
        log.info( "Query executed" );
        while ( results.hasNext() ) {
            row++;
            QuerySolution soln = results.nextSolution();
            String dataset = OntologyTools.varToString( "dataset", soln );
            dataset = dataset.substring( dataset.lastIndexOf( '/' ) + 1 );
            // log.info( dataset );
            // log.info( OntologyTools.varToString( "mapping", soln ) );

            Set<String> datasetURLs = result.get( dataset );
            if ( datasetURLs == null ) {
                result.put( dataset, new HashSet<String>() );
                datasetURLs = result.get( dataset );
            }
            String URL = OntologyTools.varToString( "mapping", soln );
            datasetURLs.add( URL );
            if ( specificLabels ) {
                labels.put( URL, OntologyTools.varToString( "label", soln ) + "[UMLS]" );
            } else {
                labels.put( URL, OntologyTools.varToString( "label", soln ) );
            }
        }
        return result;
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
        List inputList = new LinkedList<String>( input );
        Collections.sort( inputList );
        String result = "";
        for ( String line : input )
            result += labels.get( line ) + "->" + line + "\n";
        return result;
    }

    protected void processOptions() {
        super.processOptions();
    }

}
