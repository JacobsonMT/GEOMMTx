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
            URIs.add( mapper.convert( CUI, sourceCodes ) );
        }

        return URIs;
    }
}
