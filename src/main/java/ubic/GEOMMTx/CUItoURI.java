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
package ubic.GEOMMTx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CUItoURI {
    Collection<CUIMapper> mappers;
    Map<String, Set<UMLSSourceCode>> codeMap;
    GetUMLSCodes getUMLS;

    public void CUItoURI() {
        getUMLS = new GetUMLSCodes();
        codeMap = getUMLS.getUMLSCodeMap();
        mappers = new ArrayList<CUIMapper>();
    }

    public void CUItoURI( Collection<CUIMapper> mappers ) {
        this.mappers = mappers;
    }

    // mapper takes sources, code and CUI
    public void addMapper( CUIMapper mapper ) {
        mappers.add( mapper );
    }

    // does the work, checks cache... empty set if none
    public Set<String> convert( String CUI ) {
        Set<String> URIs = new HashSet<String>();

        // get its sources+codes
        Set<UMLSSourceCode> sourceCodes = codeMap.get( CUI );

        // call all the mappers
        for ( CUIMapper mapper : mappers ) {
            URIs.addAll( mapper.convert( CUI, sourceCodes ) );
        }

        return URIs;
    }
}
