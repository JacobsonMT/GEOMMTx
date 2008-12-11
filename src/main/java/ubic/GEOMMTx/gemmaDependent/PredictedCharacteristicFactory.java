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
package ubic.GEOMMTx.gemmaDependent;

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
