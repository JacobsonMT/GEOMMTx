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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import ubic.GEOMMTx.HashMapStringSet;
import ubic.GEOMMTx.SetupParameters;
import ubic.GEOMMTx.filters.UninformativeFilter;

public class CheckHighLevelSpreadSheetReader {
    public Map<String, Set<String>> getRejectedAnnotations() throws Exception {
        return getAcceptedAnnotations( SetupParameters.config.getString( "gemma.annotator.highLevelSpreadsheetFile" ) );
    }

    public Map<String, Set<String>> getAcceptedAnnotations( String file ) throws Exception {
        // CheckHighLevelSchema schema = new CheckHighLevelSchema();
        UninformativeFilter f = new UninformativeFilter();

        HSSFSheet sheet = ExcelUtil.getSheetFromFile( file, "Sheet1" );

        // start at one, skip the header
        int row = 2;
        int nullCount = 0;
        // int datasetPos = schema.getPosition( "Dataset" );
        // int URIPos = schema.getPosition( "URL" );
        // int rejectPos = schema.getPosition( "Reject" );
        int datasetPos = 0;
        int URIPos = 1;
        // FINAL is 7
        // both reject is 13
        // both agree is 14
        // agreement is 8
        int rejectPos = 7;
        HashMapStringSet accepted = new HashMapStringSet();
        HashMapStringSet all = new HashMapStringSet();

        // if we get a blank lines in a row, then exit
        while ( nullCount == 0 ) {
            String dataset = ExcelUtil.getValue( sheet, row, datasetPos );
            String URI = ExcelUtil.getValue( sheet, row, URIPos );
            String finalDecision = ExcelUtil.getValue( sheet, row, rejectPos );

            // System.out.println( CUI );
            // System.out.println( BCUI );
            if ( dataset == null ) {
                nullCount++;
            } else {
                // =HYPERLINK("http://bioinformatics.ubc.ca/Gemma/expressionExperiment/showExpressionExperiment.html?id=137";"137")
                // turns into 137
                dataset = dataset.substring( dataset.lastIndexOf( "?id=" ) + 4, dataset.lastIndexOf( "\"," ) );
                nullCount = 0;
                all.put( dataset, URI );
                // if the final decision is to accept
                if ( finalDecision != null && finalDecision.equals( "1.0" ) ) {
                    // if its not deemed uninformative/too frequent
                    if ( !f.getFrequentURLs().contains( URI ) ) {
                        accepted.put( dataset, URI );
                    }
                }
            }
            row++;
        }
        // System.out.println( seen );
        // System.out.println( seen.size() );
        // log.info( "All annotations in file:" + all.size() );
        // System.out.println( all.toPrettyString() );
        // System.out.println( all.getExpandedSize() );
        // System.out.println( "Number of accepted annotations:"+accepted.getExpandedSize() );
        return accepted;
    }

    public void printSourceStats( Map<String, Set<String>> annotations, String filename ) {
        DescriptionExtractor de = new DescriptionExtractor( filename );

        List<String> sources = new LinkedList<String>();
        for ( String dataset : annotations.keySet() ) {
            Set<String> URIs = annotations.get( dataset );
            sources.addAll( de.getDecriptionType( dataset, URIs ) );
        }

        System.out.println( "== rejects ==" );
        CompareToManual.printMap( CompareToManual.listToFrequencyMap( sources ) );
    }

    /**
     * @param args
     */
    public static void main( String[] args ) throws Exception {
        CheckHighLevelSpreadSheetReader test = new CheckHighLevelSpreadSheetReader();

        Map<String, Set<String>> acceptedAnnotations = test.getRejectedAnnotations();
    }

}
