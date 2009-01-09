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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.gemma.model.association.GOEvidenceCode;
import ubic.gemma.model.common.description.VocabCharacteristic;
import ubic.gemma.ontology.MgedOntologyService;

/**
 * @author leon, paul
 * @version $Id: PredictedCharacteristicFactory.java,v 1.3 2009/01/07 18:06:04
 *          paul Exp $
 */
public class PredictedCharacteristicFactory {
	Map<String, String> labels;
	protected static Log log = LogFactory
			.getLog(PredictedCharacteristicFactory.class);

	/**
	 * Constructor requires a label map in the format of URI -> label
	 * 
	 * @param labels
	 */
	public PredictedCharacteristicFactory(Map<String, String> labels) {
		this.labels = labels;
	}

	public VocabCharacteristic getCharacteristic(String URI) {
		VocabCharacteristic c = VocabCharacteristic.Factory.newInstance();
		c.setValueUri(URI);
		c.setValue(labels.get(URI));

		// infer the category
		String category = null;
		if (URI.contains("/owl/FMA#") || URI.contains("BIRNLex-Anatomy")) {
			category = "OrganismPart";
		} else if (URI.contains("/owl/DOID#")) {
			category = "DiseaseState";
		} else {
			log.debug("Could not infer category for : " + URI);
		}

		c.setCategory(category);
		c.setCategoryUri(MgedOntologyService.MGED_ONTO_BASE_URL + "#"
				+ category);

		c.setEvidenceCode(GOEvidenceCode.IEA);

		return c;
	}
}
