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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.util.SetupParameters;

/**
 * @author leon
 * @version $Id$
 */
public class GetUMLSCodes {

    private String location;

    private Map<String, Set<UMLSSourceCode>> SABMap;

    /**
     * If true, limit hwo many codes are read
     */
    private boolean testMode = false;

    private static final int TEST_MODE_LIMIT = 100000;

    protected static Log log = LogFactory.getLog( GetUMLSCodes.class );

    public GetUMLSCodes( boolean testMode ) {
        this( SetupParameters.getString( "geommtx.annotator.cui_code_loc" ), testMode );
    }

    // location of MRCONSO.RRF
    public GetUMLSCodes( String location, boolean testMode ) {
        this.testMode = testMode;
        this.location = location;
        try {
            loadUMLSCodeMap();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public GetUMLSCodes() {
        this( false );
    }

    /**
     * @return
     */
    public Map<String, Set<UMLSSourceCode>> getUMLSCodeMap() {
        return SABMap;
    }

    /**
     * @throws IOException
     */
    public void loadUMLSCodeMap() throws IOException {
        int noCodeCount = 0;
        SABMap = new HashMap<>();

        try (BufferedReader f = new BufferedReader( new FileReader( location ) );) {
            String line;
            String[] tokens;

            int count = 0;
            while ( ( line = f.readLine() ) != null ) {
                tokens = line.split( "\\|" );
                String CUI = tokens[0];
                String SAB = null, CODE = null;

                // SAB is source vocab
                SAB = tokens[11];
                CODE = tokens[13];

                if ( CODE.equals( "NOCODE" ) ) {
                    noCodeCount++;
                    continue;
                }

                if ( SAB.startsWith( "MSH" ) || SAB.startsWith( "SNO" ) ) continue;

                if ( !SABMap.containsKey( CUI ) ) {
                    SABMap.put( CUI, new HashSet<UMLSSourceCode>() );
                }
                // add it to the set associated with this CUI
                SABMap.get( CUI ).add( new UMLSSourceCode( SAB, CODE ) );

                if ( ++count % 500000 == 0 ) {
                    log.info( count + " UMLS codes processed ..." );
                }

                if ( testMode && count > TEST_MODE_LIMIT ) {
                    break;
                }
            }
        }
        log.info( "Loaded UMLS Codes, nocode entries=" + noCodeCount );
    }
}
