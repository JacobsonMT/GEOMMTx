package ubic.GEOMMTx;

import java.util.Map;

import ubic.gemma.model.association.GOEvidenceCode;
import ubic.gemma.model.common.description.VocabCharacteristic;
import ubic.gemma.ontology.MgedOntologyService;

public class PredictedCharacteristicFactory {
    Map<String, String> labels;

    public PredictedCharacteristicFactory( Map<String, String> labels ) {
        this.labels = labels;
    }

    public VocabCharacteristic getCharacteristic( String URI ) {
        VocabCharacteristic c = VocabCharacteristic.Factory.newInstance();
        c.setValueUri( URI );
        c.setValue( labels.get( URI ) );

        String category = null;
        if ( URI.contains( "/owl/FMA#" ) || URI.contains( "BIRNLex-Anatomy" ) ) {
            category = "OrganismPart";
        }
        if ( URI.contains( "/owl/DOID#" ) ) {
            category = "DiseaseState";
        }
        if ( category != null ) {
            c.setCategory( category );
            c.setCategory( MgedOntologyService.MGED_ONTO_BASE_URL + category );
        }
        //System.out.println( "Predicted category:" + category );
        c.setEvidenceCode( GOEvidenceCode.IEA );

        // audit trail?
        // experiment.getCharacteristics().add( c );
        return c;
    }

}
