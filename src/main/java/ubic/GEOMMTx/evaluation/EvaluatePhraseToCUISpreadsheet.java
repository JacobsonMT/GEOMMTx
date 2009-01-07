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
package ubic.GEOMMTx.evaluation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import ubic.GEOMMTx.SetupParameters;

/**
 * This class loads in the excel files that were checked by annotators. It gathers the responses and computes precision
 * and recall
 * 
 * @author lfrench
 */
public class EvaluatePhraseToCUISpreadsheet {
    protected static Log log = LogFactory.getLog( EvaluatePhraseToCUISpreadsheet.class );

    /**
     * @param args
     */
    public static void main( String[] args ) throws Exception {
        // TODO Auto-generated method stub
        EvaluatePhraseToCUISpreadsheet evaluator = new EvaluatePhraseToCUISpreadsheet();
        // evaluator.runThroughFiles( "PtoCPaul.xls", "PtoCSuzanne.xls" );
        // evaluator.runThroughFiles( "Paul", "Suzanne" );
        System.out.println( evaluator.getRejectedSUIs() );
        System.out.println( evaluator.getRejectedSUIs().size() );
    }

    public EvaluatePhraseToCUISpreadsheet() {

    }

    public Set<CUISUIPair> getRejectedSUIs() throws Exception {
        return getRejectedSUIs( SetupParameters.config.getString( "gemma.annotator.CUISUIEvaluationFile" ) );
    }

    public Set<CUISUIPair> getRejectedSUIs( String file ) throws Exception {
        PhraseToCUISchema schema = new PhraseToCUISchema();
        HSSFSheet sheet = ExcelUtil.getSheetFromFile( file, "Sheet0" );

        // start at one, skip the header
        int row = 1;
        int nullCount = 0;
        int CUIPos = schema.getPosition( "CUI" );
        int SUIPos = schema.getPosition( "SUI" );
        int rejectPos = schema.getPosition( "Reject" );
        Set<CUISUIPair> rejected = new HashSet<CUISUIPair>();
        Set<CUISUIPair> all = new HashSet<CUISUIPair>();

        // if we get two blank lines in a row, then exit
        while ( nullCount < 2 ) {

            String CUI = ExcelUtil.getValue( sheet, row, CUIPos );
            String SUI = ExcelUtil.getValue( sheet, row, SUIPos );
            String reject = ExcelUtil.getValue( sheet, row, rejectPos );

            // System.out.println( CUI );
            // System.out.println( BCUI );
            if ( CUI == null ) {
                nullCount++;
            } else {
                nullCount = 0;
                all.add( new CUISUIPair( CUI, SUI ) );
                if ( reject.equals( "X" ) ) {
                    rejected.add( new CUISUIPair( CUI, SUI ) );
                    // delete all mentions that have this CUI and SUI combination
                }
            }
            row++;
        }
        // System.out.println( seen );
        // System.out.println( seen.size() );
        log.info( "All CUI SUI parings:" + all.size() );
        return rejected;
    }

    public Map<String, String> loadComments( HSSFSheet sheet ) {
        Map<String, String> comments = new HashMap<String, String>();

        return comments;
    }

    public void runThroughFiles( String file1, String file2 ) throws Exception {
        // DoubleMatrixNamed confusion = new DenseDoubleMatrix2DNamed( 2, 2 );
        // confusion.setRowName( arg0 )
        PhraseToCUISchema schema = new PhraseToCUISchema();
        double[][] confusion = new double[2][2];
        HSSFSheet sheetA = ExcelUtil.getSheetFromFile( file1, "Sheet0" );
        HSSFSheet sheetB = ExcelUtil.getSheetFromFile( file2, "Sheet0" );

        // start at one, skip the header
        int row = 1;
        int nullCount = 0;
        int CUIPos = schema.getPosition( "CUI" );
        int rejectPos = schema.getPosition( "Reject" );
        int mentionPos = schema.getPosition( "mentionLabel" );
        int SUIPos = schema.getPosition( "SUI" );
        int phrasePos = schema.getPosition( "phraseLabel" );
        int comPos = schema.getPosition( "Comment" );

        while ( nullCount < 2 ) {
            String ACUI = ExcelUtil.getValue( sheetA, row, CUIPos );
            String BCUI = ExcelUtil.getValue( sheetB, row, CUIPos );
            String SUI = ExcelUtil.getValue( sheetB, row, SUIPos );
            String mentionLabel = ExcelUtil.getValue( sheetB, row, mentionPos );

            // System.out.println( ACUI );
            // System.out.println( BCUI );
            if ( ACUI == null && BCUI == null ) {
                nullCount++;
            } else {
                nullCount = 0;
                if ( !ACUI.equals( BCUI ) ) {
                    System.out.println( "non matching CUI's" );
                    System.exit( 1 );
                }
                // so we have matching CUI's
                // start going through the block
                boolean blockRejectA = false;
                boolean blockRejectB = false;
                String blockDisplay = "";

                do {
                    String rejectA = ExcelUtil.getValue( sheetA, row, rejectPos );
                    String rejectB = ExcelUtil.getValue( sheetB, row, rejectPos );
                    String commentA = ExcelUtil.getValue( sheetA, row, comPos );
                    String commentB = ExcelUtil.getValue( sheetB, row, comPos );
                    // set it up so that if we see one "X" then the block is rejected
                    blockRejectA = blockRejectA || ( rejectA != null && rejectA.equals( "X" ) );
                    blockRejectB = blockRejectB || ( rejectB != null && rejectB.equals( "X" ) );
                    blockDisplay += "  " + ExcelUtil.getValue( sheetA, row, phrasePos );
                    if ( commentA != null ) blockDisplay += " (" + file1 + ":" + commentA + ")";
                    if ( commentB != null ) blockDisplay += " (" + file2 + ":" + commentB + ")";
                    blockDisplay += "\n";

                    row++;
                    ACUI = ExcelUtil.getValue( sheetA, row, CUIPos );
                } while ( ACUI != null );

                if ( !blockRejectA && !blockRejectB ) {
                    // they agree on rejecting
                    confusion[1][1]++;
                }
                if ( blockRejectA && blockRejectB ) {
                    // they agree on keeping
                    confusion[0][0]++;

                } else {
                    if ( blockRejectA && !blockRejectB ) {
                        confusion[1][0]++;
                        System.out.println( "\"" + mentionLabel + "\" - " + file2 + " accepts, " + file1 + " rejects" );
                        System.out.println( "CUI:" + BCUI + " SUI:" + SUI );
                        System.out.println( blockDisplay );
                    } else if ( !blockRejectA && blockRejectB ) {
                        confusion[0][1]++;
                        System.out.println( "\"" + mentionLabel + "\" - " + file1 + " says keep, " + file2
                                + " says reject" );
                        System.out.println( "CUI:" + BCUI + " SUI:" + SUI );
                        System.out.println( blockDisplay );
                    }
                }

                // System.out.println( ACUI );
            }
            row++;
        }

        // get a hash with a key (CUI+SUI)+name, keep
        System.out.println( "Both keep:" + confusion[1][1] );
        System.out.println( "Both reject:" + confusion[0][0] );
        System.out.println( file2 + " rejects and " + file1 + " accepts:" + confusion[0][1] );
        System.out.println( file1 + " rejects and " + file2 + " accepts:" + confusion[1][0] );

    }

}
