/*
 * The GEOMMTx project
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
package ubic.GEOMMTx;

import gov.nih.nlm.nls.nlp.textfeatures.Candidate;
import gov.nih.nlm.nls.nlp.textfeatures.Phrase;
import gov.nih.nlm.nls.nlp.textfeatures.UMLS_SemanticTypePointer;

import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ubic.GEOMMTx.evaluation.CUISUIPair;
import ubic.GEOMMTx.evaluation.EvaluatePhraseToCUISpreadsheet;
import ubic.basecode.dataStructure.CountingMap;
import ubic.basecode.io.excel.ExcelUtil;
import au.com.bytecode.opencsv.CSVReader;

/**
 * Basically just a class that calls MMTx to get MeSH terms from free text
 * 
 * @author leon
 */
public class MeSHMapperTest {

    protected static Log log = LogFactory.getLog( MeSHMapperTest.class );
    static String[] PHRASE_PARAMS = new String[] { "--no_acros_abbrs", "--term_processing", "--ignore_word_order",
            "--allow_concept_gaps" };
    static String[] TEXT_PARAMS = new String[] { "--an_derivational_variants", "--no_acros_abbrs" };
    public int CUISUIrejects = 0;
    Set<String> acceptedSemanticTypes;

    int answerCount;
    int atLeastOne;
    static GetUMLSCodes mapper;
    int matchedCount;
    static MMTxRunner mmtx;
    int predictedCount;
    Set<String> rejectedConcepts;
    static Set<CUISUIPair> rejectedCUISUIPairs;

    CountingMap<String> semTypeMap;

    static Map<String, Set<UMLSSourceCode>> sourceMap;

    int zeroCalls;

    @BeforeClass
    public static void setupOnce() throws Exception {
        mmtx = new MMTxRunner( null, 0, PHRASE_PARAMS );
        mapper = new GetUMLSCodes();
        sourceMap = mapper.getUMLSCodeMap();
        EvaluatePhraseToCUISpreadsheet evalSheet = new EvaluatePhraseToCUISpreadsheet();
        rejectedCUISUIPairs = evalSheet.getRejectedSUIs();
    }

    @Before
    public void setup() throws Exception {
        acceptedSemanticTypes = new HashSet<String>();
        rejectedConcepts = new HashSet<String>();
        semTypeMap = new CountingMap<String>();
        resetCounters();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPredictions() throws Exception {

        CSVReader reader = new CSVReader( new FileReader( "/testdata/LMD-FEATURES.txt" ), ',', '"' );
        List<String[]> lines = reader.readAll();
        reader.close();

        for ( String[] line : lines ) {
            String ID = line[0];
            String name = line[2];

            Set<String> predictions = getMeSHIDs( name, false );
            for ( String prediction : predictions ) {
                System.out.println( ID + "|name|" + prediction );
            }

            if ( predictions.size() == 0 ) zeroCalls++;
            predictedCount += predictions.size();
            // if ( predictedCount > 10 ) break;
        }
        // mapper.printSemMap();
        System.out.println( "Rejected anot:" + CUISUIrejects );
        System.out.println( "predictedCount:" + predictedCount );
        System.out.println( "zeroCalls:" + zeroCalls );

        for ( String[] line : lines ) {
            String ID = line[0];
            String desc = line[3];
            if ( desc.trim().equals( "" ) ) {
                zeroCalls++;
                continue;
            }

            Set<String> predictions = getMeSHIDs( desc, false );
            for ( String prediction : predictions ) {
                System.out.println( ID + "|desc|" + prediction );
            }

            if ( predictions.size() == 0 ) zeroCalls++;
            predictedCount += predictions.size();
        }
        log.info( "Rejected anot:" + CUISUIrejects );
        log.info( "predictedCount:" + predictedCount );
        log.info( "zeroCalls:" + zeroCalls );

    }

    @Test
    public void testOmimMesh() throws Exception {

        acceptedSemanticTypes = new HashSet<String>();
        acceptedSemanticTypes.add( "Congenital Abnormality" );
        acceptedSemanticTypes.add( "Disease or Syndrome" );
        acceptedSemanticTypes.add( "Neoplastic Process" );
        setAcceptedSemanticTypes( acceptedSemanticTypes );

        rejectedConcepts = new HashSet<String>();
        rejectedConcepts.add( "Disease" );
        rejectedConcepts.add( "Syndrome" );
        rejectedConcepts.add( "Carcinoma" );
        setRejectedConcepts( rejectedConcepts );

        log.info( getMeSHIDs( "Juvenile Myoclonic Epilepsy", true ) );
        // read in spreadsheet
        int howMany = 0;
        HSSFSheet sheet = ExcelUtil.getSheetFromFile( "/media/disk/SPOMIMtoMeSH.xls", "Sheet1" );
        for ( int i = 0; i < 400; i++ ) {
            // get curated set for column e(pos 4)
            StringTokenizer MeSHBreaker = new StringTokenizer( ExcelUtil.getValue( sheet, i, 4 ), " " );
            Set<String> answers = new HashSet<String>();
            while ( MeSHBreaker.hasMoreTokens() ) {
                answers.add( MeSHBreaker.nextToken() );
            }

            // the excel file denotes OMIM entries
            if ( ExcelUtil.getValue( sheet, i, 0 ).equals( "OMIM" ) ) {
                // ??
            }

            // skip swissprot
            if ( ExcelUtil.getValue( sheet, i, 0 ).equals( "SP" ) ) {
                continue;
            }

            log.info( ExcelUtil.getValue( sheet, i, 1 ) );

            // the input text/description
            // get text from col C (pos 2)
            String text = ExcelUtil.getValue( sheet, i, 2 );
            if ( text != null && text.trim().length() != 0 ) {
                howMany++;
                evaluate( text, answers );
            }

        }
        log.info( "Descriptions seen:" + howMany );
        printStats();
    }

    private void evaluate( String text, Set<String> answers ) {
        Set<String> predictions = getMeSHIDs( text, true );
        if ( predictions.size() == 0 ) zeroCalls++;
        predictedCount += predictions.size();
        answerCount += answers.size();
        answers.retainAll( predictions );
        matchedCount += answers.size();
        if ( answers.size() >= 1 ) atLeastOne++;
    }

    /**
     * @param text
     * @param useSemTypes
     * @return
     */
    private Set<String> getMeSHIDs( String text, boolean useSemTypes ) {
        // log.info(text);
        text = text.replace( ')', ' ' );
        text = text.replace( '(', ' ' );
        text = text.replace( '/', ' ' );
        text = text.replace( '\r', ' ' );
        text = text.replace( '\n', ' ' );
        // log.info(text);

        String baseUMLS = "http://www.purl.org/umls/umls#";

        Set<String> result = new HashSet<String>();
        // log.info( text + " -> " );
        for ( Phrase p : mmtx.getPhrases( text ) ) {

            for ( Candidate c : mmtx.getConcepts( p ) ) {

                if ( rejectedConcepts.contains( c.getUMLS_ConceptName() ) ) continue;

                if ( rejectedCUISUIPairs.contains( new CUISUIPair( baseUMLS + c.getCUI(), baseUMLS + c.getSUI() ) ) ) {
                    CUISUIrejects++;
                    continue;
                }

                UMLS_SemanticTypePointer[] semTypes = c.getSemanticTypes();
                boolean hasAcceptedSemanticType = !useSemTypes;

                for ( UMLS_SemanticTypePointer semType : semTypes ) {
                    // log.info( semType.getName() );
                    semTypeMap.increment( semType.getName() );
                    if ( acceptedSemanticTypes.contains( semType.getName() ) )
                        hasAcceptedSemanticType = !useSemTypes || true;
                }

                // concept does not have accepted semantic type
                if ( !hasAcceptedSemanticType ) continue;

                // if it has a source vocab listing
                if ( sourceMap.get( c.getCUI() ) != null ) {
                    // then iterate them
                    for ( UMLSSourceCode code : sourceMap.get( c.getCUI() ) ) {
                        // if it's mesh then continue
                        if ( code.getSource().startsWith( "MSH" ) ) {
                            // if we havent seen it yet, print it
                            // if ( !result.contains( code.getCode() ) ) {
                            // log.info( c.getConcept().toString() + " -> " + code.getCode() + " SCORE:"
                            // + c.getFinalScore() );
                            // }
                            result.add( p.getOriginalString() + "|" + c.getConcept().toString() + "|" + code.getCode()
                                    + "|" + c.getFinalScore() );
                        }
                    }
                }
                // if it's MeSH than keep it
            }
        }

        return result;
    }

    private void printStats() {
        log.info( "Predicted:" + predictedCount );
        log.info( "Number of inputs that had no predicted MeSH terms:" + zeroCalls );
        log.info( "Gold standard:" + answerCount );
        log.info( "Intersection:" + matchedCount );
        log.info( "Number where predicted one:" + atLeastOne );
    }

    private void resetCounters() {
        predictedCount = 0;
        answerCount = 0;
        matchedCount = 0;
        atLeastOne = 0;
    }

    private void setAcceptedSemanticTypes( Set<String> types ) {
        acceptedSemanticTypes = types;
    }

    private void setRejectedConcepts( Set<String> concepts ) {
        rejectedConcepts = concepts;
    }
}
