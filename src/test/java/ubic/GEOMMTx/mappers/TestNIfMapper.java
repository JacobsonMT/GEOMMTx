/*
 * The GEOMMTx project
 * 
 * Copyright (c) 2010 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.GEOMMTx.mappers;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import ubic.GEOMMTx.GetUMLSCodes;
import ubic.GEOMMTx.UMLSSourceCode;

/**
 * @author paul
 * @version $Id$
 */
public class TestNIfMapper {

    @Test
    public void test() {
        NIFSTDMapper test = new NIFSTDMapper();
        test.loadFromOntology();
        test.save();
        assertTrue( test.countOnetoMany() > 0 );
        assertTrue( test.getAllURLs().size() > 0 );

    }

    @Test
    public void test2() {
        GetUMLSCodes codes = new GetUMLSCodes( true );
        NIFSTDMapper test = new NIFSTDMapper();
        test.loadFromOntology();
        Set<UMLSSourceCode> map = codes.getUMLSCodeMap().get( "C0024109" );

        assertTrue( map.size() > 0 );

        Set<String> URL = test.convert( "C0024109", codes.getUMLSCodeMap().get( "C0024109" ) );

        int count = 0;
        for ( String concept : codes.getUMLSCodeMap().keySet() ) {
            URL = test.convert( concept, codes.getUMLSCodeMap().get( concept ) );
            if ( URL != null ) count++;// =URL.size();
        }
        assertTrue( count > 0 );

        // URL =test.convert( "C0006104", codes.getUMLSCodeMap().get( "C0006104" ) );
        // System.out.println( URL );
        // System.out.println( fma.getAllURLs().size() );
    }

}
