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

import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.filters.BIRNLexFMANullsFilter;
import ubic.gemma.model.association.GOEvidenceCode;
import ubic.gemma.model.common.description.VocabCharacteristic;
import ubic.gemma.ontology.FMAOntologyService;
import ubic.gemma.ontology.MgedOntologyService;
import ubic.gemma.ontology.OntologyService;
import ubic.gemma.ontology.OntologyTerm;

/**
 * @author leon, paul
 * @version $Id$
 */
public class PredictedCharacteristicFactory {
    Map<String, String> labels;
    OntologyTerm fmaMolecule;
    OntologyService FMAOS;

    protected static Log log = LogFactory.getLog( PredictedCharacteristicFactory.class );

    /**
     * Constructor requires a label map in the format of URI -> label
     * 
     * @param labels
     * @param FMAOS ontology service with FMA loaded
     */
    public PredictedCharacteristicFactory( Map<String, String> labels, OntologyService FMAOS ) {
        // ontology serv
        this.FMAOS = FMAOS;
        this.labels = labels;

        // term for Biological macromolecule in FMA (FMAID=63887)
        fmaMolecule = FMAOS.getTerm( "http://purl.org/obo/owl/FMA#FMA_63887" );

    }

    public VocabCharacteristic getCharacteristic( String URI ) {
        VocabCharacteristic c = VocabCharacteristic.Factory.newInstance();
        c.setValueUri( URI );
        c.setValue( labels.get( URI ) );

        String category = getCategory( URI );

        c.setCategory( category );
        c.setCategoryUri( MgedOntologyService.MGED_ONTO_BASE_URL + "#" + category );

        c.setEvidenceCode( GOEvidenceCode.IEA );

        return c;
    }

    public String getCategory( String URI ) {
        // infer the category
        String category = null;

        if ( URI.contains( "/owl/FMA#" ) ) {
            OntologyTerm term = FMAOS.getTerm( URI );

            boolean direct = false; // get all parents
            Collection<OntologyTerm> parents = term.getParents( direct );
            
            // test if its a Biological macromolecule in FMA
            if ( parents.contains( fmaMolecule ) ) {
                log.info( "URI is biological macromolecule in FMA" );
                category = "Compound";
            } else {
                category = "OrganismPart";
            }
        } else if ( URI.contains( "BIRNLex-Anatomy" ) ) {
            category = "OrganismPart";
        } else if ( URI.contains( "/owl/DOID#" ) ) {
            category = "DiseaseState";
        } else {
            log.debug( "Could not infer category for : " + URI );
        }
        return category;
    }
}
