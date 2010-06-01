/*
 * The GEOMMTx project
 * 
 * Copyright (c) 2009-2010 University of British Columbia
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

import static org.junit.Assert.assertNotNull;
import gov.nih.nlm.kss.api.KSSRetrieverV5_0;

import java.rmi.Naming;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author lfrench
 * @version $Id$
 */
public class GetUMLSCodesTest {

    private static Log log = LogFactory.getLog( GetUMLSCodesTest.class );

    private static GetUMLSCodes umlscodez;

    @BeforeClass
    public static void setup() throws Exception {
        umlscodez = new GetUMLSCodes();
    }

    @Test
    public void test() throws Exception {
        Map<String, Set<UMLSSourceCode>> map = umlscodez.getUMLSCodeMap();
        assertNotNull( map.get( "C0814999" ) );
    }

    /**
     * This test requires RMI access which is per-IP address, so I have disabled it.
     * 
     * @throws Exception
     */
    @Test
    public void meSHTest() throws Exception {
        // Map<String, Set<UMLSSourceCode>> map = umlscodez.getUMLSCodeMap();
        // Set<String> meshCodes = new HashSet<String>();
        // log.info( map.get( "C0030567" ) );
        //
        // String name = "//umlsks.nlm.nih.gov/KSSRetriever";
        // int bothC;
        // int meshC;
        // int omimC;
        // bothC = 0;
        // meshC = 0;
        // omimC = 0;
        //
        // KSSRetrieverV5_0 retriever = ( KSSRetrieverV5_0 ) Naming.lookup( name );
        //
        // assertNotNull( retriever );
        //
        // char[] result = retriever.getSemanticType( "2008AA", "C0001175" );
        // String conceptName = new String( result );
        // log.info( "Concept Name in XML: " + conceptName );
        //
        // result = retriever.getSemanticType( "2008AA", "C0001175" );
        // conceptName = new String( result );
        // log.info( "Concept Name in XML: " + conceptName );
        //
        // int i = 0;
        // for ( String CUI : map.keySet() ) {
        // if ( i++ % 10000 == 0 ) log.info( ( i - 1.0 ) / 1170000.0 );
        //
        // boolean both = false;
        // boolean mesh = false;
        // boolean omim = false;
        //
        // for ( UMLSSourceCode code : map.get( CUI ) ) {
        // if ( code.getSource().startsWith( "MSH" ) ) {
        // mesh = true;
        // meshCodes.add( code.getCode() );
        // }
        // if ( code.getSource().startsWith( "OMIM" ) && !code.getCode().contains( "." ) ) {
        // omim = true;
        // }
        // }
        //
        // if ( omim ) {
        // char[] result2 = retriever.getSemanticType( "2008AA", CUI );
        // String conceptName2 = new String( result2 );
        // omim = ( conceptName2.contains( "T047" ) || conceptName2.contains( "T019" ) );
        // }
        //
        // both = mesh && omim;
        // if ( both ) bothC++;
        // if ( mesh ) meshC++;
        // if ( omim ) omimC++;
        // }
        //
        // log.info( "OMIM=" + omimC );
        // log.info( "Mesh=" + meshC );
        // log.info( "both=" + bothC );
        // log.info( "Unique Mesh codes=" + meshCodes.size() );
        // log.info( "Size:" + map.size() );

    }

}
