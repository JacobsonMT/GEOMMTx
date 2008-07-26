package ubic.GEOMMTx.mappers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ubic.GEOMMTx.CUIMapper;
import ubic.GEOMMTx.GetUMLSCodes;
import ubic.GEOMMTx.UMLSSourceCode;


public class FMALiteMapper implements CUIMapper {
    private final static String MAIN_URL = "http://purl.org/obo/owl/FMA#FMA_";

    public String getMainURL() {
        return "http://www.berkeleybop.org/ontologies/owl/FMA";
    }    
    
    public Set<String> convert(String CUI, Collection<UMLSSourceCode> sourceCodes) {
        if (sourceCodes == null) return null;
        String code = null;
        Set<String> codes = new HashSet<String>();
        for (UMLSSourceCode sourceCode : sourceCodes) {
            // if FMA is the source
            if (sourceCode.getSource().equals( "UWDA173" )) {
                code = sourceCode.getCode();
                codes.add( MAIN_URL + code );
            }
        }
        
        //doesnt have digital anatomist code
        if (codes.isEmpty()) {
            return null;
        }
        //check to see if it exists in the ontology???
        
        return codes; 
    }
    public static void main(String args[]) {
        GetUMLSCodes codes = new GetUMLSCodes();
        FMALiteMapper fma = new FMALiteMapper();
        Set<UMLSSourceCode> map = codes.getUMLSCodeMap().get("C0024109");
        System.out.println(map);
        Set<String> URL = fma.convert( "C0024109",  codes.getUMLSCodeMap().get("C0024109"));
        System.out.println(URL);
        
        URL = fma.convert( "C0006104",  codes.getUMLSCodeMap().get("C0006104"));
        System.out.println(URL);
    }
}