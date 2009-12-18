/*
 * The GEOMMTx project
 * 
 * Copyright (c) 2009 University of British Columbia
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
package ubic.GEOMMTx.mappers;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author paul
 * @version $Id$
 */
public class TestBirnLexMapper {

    @Test
    public void test() {
        BirnLexMapper test = new BirnLexMapper();
        test.loadFromOntology();
        test.save();
        assertTrue( test.countOnetoMany() > 0 );
        assertTrue( test.getAllURLs().size() > 0 );

    }

}
