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

/**
 * TODO document me
 * 
 * @author lfrench
 * @version $Id$
 */
public class PhraseToCUISchema extends SpreadSheetSchema {
    public PhraseToCUISchema() {
        super();
        positions.put( "CUI", 0 );
        positions.put( "SUI", 1 );
        positions.put( "phraseLabel", 2 );
        positions.put( "Reject", 3 );
        positions.put( "mentionLabel", 4 );
        positions.put( "Comment", 5 );
    }
}
