package ubic.GEOMMTx.mappers;

import java.util.Collection;

import ubic.GEOMMTx.CUIMapper;
import ubic.GEOMMTx.UMLSSourceCode;

public class NullMapper implements CUIMapper {

    public String convert( String CUI, Collection<UMLSSourceCode> sourceCodes ) {
        return null;
    }

}
