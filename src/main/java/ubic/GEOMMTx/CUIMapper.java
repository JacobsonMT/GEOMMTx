package ubic.GEOMMTx;

import java.util.Collection;
import java.util.Set;

public interface CUIMapper {
    /**
     * Given a CUI, and UMLS codes and sources attatched to it, return its URIs
     * 
     * @param CUI UMLS concept identifier
     * @param sourceCodes UMLS code+source pairings 
     * @return a URI
     */
    public Set<String> convert( String CUI, Collection<UMLSSourceCode> sourceCodes );
}
