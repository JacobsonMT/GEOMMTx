package ubic.GEOMMTx;

import java.util.Collection;

public interface CUIMapper {
    /**
     * Given a CUI, and UMLS codes and sources attatched to it, return its URI
     * 
     * @param CUI UMLS concept identifier
     * @param sourceCodes UMLS code+source pairings 
     * @return a URI
     */
    public String convert( String CUI, Collection<UMLSSourceCode> sourceCodes );
}
