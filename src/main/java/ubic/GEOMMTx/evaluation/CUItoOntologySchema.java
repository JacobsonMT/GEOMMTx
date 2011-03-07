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
package ubic.GEOMMTx.evaluation;

import java.util.Arrays;

/**
 * TODO Document Me
 * 
 * @author lfrench
 * @version $Id$
 */
public class CUItoOntologySchema extends SpreadSheetSchema {

    public static void main( String[] args ) {
        CUItoOntologySchema test = new CUItoOntologySchema();
        System.out.println( "Header:" + Arrays.asList( test.getHeaderRow() ).toString() );
    }

    /**
     * @param args
     */
    public CUItoOntologySchema() {
        super();
        positions.put( "CUI", 0 );
        positions.put( "mappedTerm", 1 );
        positions.put( "CUILabel", 2 );
        positions.put( "Reject", 3 );
        positions.put( "mappedTermLabel", 4 );
        positions.put( "Comment", 5 );
    }

}
