package ubic.GEOMMTx.mappers;

import java.util.Collection;

import ubic.GEOMMTx.CUIMapper;
import ubic.GEOMMTx.UMLSSourceCode;


public class FMALiteMapper implements CUIMapper {
    private final static String MAIN_URL = "http://purl.org/obo/owl/FMA#FMA_";

    public String getMainURL() {
        return "http://www.berkeleybop.org/ontologies/owl/FMA";
    }    
    
    public String convert(String CUI, Collection<UMLSSourceCode> sourceCodes) {
        if (sourceCodes == null) return null;
        String code = null;
        for (UMLSSourceCode sourceCode : sourceCodes) {
            // if FMA is the source
            if (sourceCode.getSource().equals( "UWDA173" )) {
                code = sourceCode.getCode();
            }
        }
        
        //doesnt have digital anatomist
        if (code == null) {
            return null;
        }
        //check to see if it exists in the ontology???
        
        return MAIN_URL + code; 
    }
}