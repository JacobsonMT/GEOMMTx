package ubic.GEOMMTx.mappers;

import java.util.Collection;
import java.util.Set;

import ubic.GEOMMTx.CUIMapper;
import ubic.GEOMMTx.UMLSSourceCode;

public class NullMapper implements CUIMapper {

    public Set<String> convert( String CUI, Collection<UMLSSourceCode> sourceCodes ) {
        return null;
    }

}
