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

import java.util.Arrays;

public class CheckHighLevelSchema extends SpreadSheetSchema {
    public CheckHighLevelSchema() {
        super();
        positions.put( "Dataset", 0 );
        positions.put( "Name", 1 );
        positions.put( "Reject", 2 );
        positions.put( "Comment", 3 );
        positions.put( "URL", 4 );
    }
    
    public static void main( String[] args ) {
        // TODO Auto-generated method stub
        CheckHighLevelSchema test = new CheckHighLevelSchema();
        System.out.println( "Header:" + Arrays.asList(test.getHeaderRow()).toString() );
    }
}
