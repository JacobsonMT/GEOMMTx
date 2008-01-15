package ubic.GEOMMTx.evaluation;

import java.io.FileInputStream;
import java.util.Map;

import ubic.gemma.ontology.OntologyLoader;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class CUItoOntologySpreadsheet extends CreateSpreadSheet {
    Model linkedOntologies;

    public CUItoOntologySpreadsheet( String filename ) throws Exception {
        super( filename, new CUItoOntologySchema() );
        // load in other ontologies?
        // birnlex, Disease and FMAlite?
        // OntModel birnLex = OntologyLoader.loadPersistentModel( "http://fireball.drexelmed.edu/birnlex/", false );
        // log.info( "loaded birnlex" );
        // Model FMAlite =
        // OntologyLoader.loadPersistentModel("http://www.berkeleybop.org/ontologies/obo-all/fma_lite/fma_lite.owl",
        // false );
        // log.info( "loaded FMA" );
        // Model DO = OntologyLoader.loadPersistentModel(
        // "http://www.berkeleybop.org/ontologies/obo-all/disease_ontology/disease_ontology.owl", false );
        // log.info( "loaded Disease ontology" );

        // linkedOntologies = birnLex;
        // linkedOntologies.union( FMAlite );
        // linkedOntologies.union( DO );
        log.info( "merged all" );
    }

    public void populate( String inputFile ) throws Exception {
        OntModel birnLex = OntologyLoader.loadPersistentModel( "http://fireball.drexelmed.edu/birnlex/", false );
        log.info( "loaded birnLex..." );
        // OntModel FMAlite = OntologyLoader.loadPersistentModel(
        // "http://www.berkeleybop.org/ontologies/obo-all/fma_lite/fma_lite.owl", false );
        // log.info( "loaded FMA" );
        OntModel DO = OntologyLoader.loadPersistentModel(
                "http://www.berkeleybop.org/ontologies/obo-all/disease_ontology/disease_ontology.owl", false );
        log.info( "loaded Disease ontology" );

        Model model = ModelFactory.createDefaultModel();
        FileInputStream fi = new FileInputStream( inputFile );
        model.read( fi, null );
        fi.close();
        log.info( "Done reading..." );
        model.add( birnLex );
        log.info( "Done merging Birnlex..." );
        // model.add( FMAlite );
        //log.info( "Done merging FMA..." );
        model.add(DO);
        log.info( "Done merging Disease Ontology..." );

        String queryString = "PREFIX leon: <http://www.purl.org/leon/umls#>                                 \r\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>                                \r\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>                                     \r\n"
                + "PREFIX gss: <http://www.w3.org/2001/11/IsaViz/graphstylesheets#>                         \r\n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>                                          \r\n"
                + "PREFIX rs: <http://www.w3.org/2001/sw/DataAccess/tests/result-set#>                      \r\n"
                + "SELECT DISTINCT ?CUI ?CUILabel ?mappedTerm ?mappedTermLabel                              \r\n"
                + "WHERE {                                                                                  \r\n"
                + "    ?mention leon:hasCUI ?CUI .                                                         \r\n"
                + "    ?mention rdfs:label ?CUILabel .                                                 \r\n"
                + "    ?mention leon:mappedTerm ?mappedTerm .                                                 \r\n"
                + " ?mappedTerm rdfs:label ?mappedTermLabel . \r\n" + "} ORDER BY ASC(?CUI) ASC(?mappedTerm)";

        // sparql query
        // CUI, SUI, phrase, label
        // dataset and description URL?
        // needs mapped term, and sorted?
        Query q = QueryFactory.create( queryString );
        // go through them all and put in excel file
        QueryExecution qexec = QueryExecutionFactory.create( q, model );

        try {
            int row = 1;
            ResultSet results = qexec.execSelect();
            log.info( "Query executed" );
            while ( results.hasNext() ) {
                // get the next solution and load it into a map (varName -> value)
                Map<String, String> solutionMap = mapQuerySolution( results.nextSolution() );

                // here we take the variable names, find the position and value and put it in the excel file
                for ( String varName : solutionMap.keySet() ) {
                    Integer position = schema.getPosition( varName );
                    // if it has no mapping to the excel file, move on
                    if ( position == null ) continue;
                    String value = solutionMap.get( varName );
                    ExcelUtil.setValue( spreadsheet, row, position, value );
                }
                row++;
            }
        } finally {
            qexec.close();
        }
    }

    /**
     * @param args
     */
    public static void main( String[] args ) throws Exception {
        CreateSpreadSheet test = new CUItoOntologySpreadsheet( "tt.xls" );

        log.info( "populating" );
        test.populate( "mergedRDF.rdf" );
        log.info( "saving.." );
        test.save();
        log.info( "Done!" );

    }

}
