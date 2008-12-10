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
package ubic.GEOMMTx;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

public class Vocabulary {
    public static Property spanStart;
    public static Property spanEnd;
    public static Property hasMention;
    public static Property mappedTerm;
    public static Property hasCUI;
    public static Property hasSUI;
    public static Property hasScore;
    public static Property hasPhrase;
    public static Property describedBy;
    static {
        String gemmaAnnNS = "http://bioinformatics.ubc.ca/Gemma/ws/xml/gemmaAnnotations.owl#";
        Model model = ModelFactory.createDefaultModel();

        spanStart = model.createProperty( gemmaAnnNS + "spanStart" );
        spanEnd = model.createProperty( gemmaAnnNS + "spanEnd" );
        hasMention = model.createProperty( gemmaAnnNS + "hasMention" );
        mappedTerm = model.createProperty( gemmaAnnNS + "mappedTerm" );
        hasCUI = model.createProperty( gemmaAnnNS + "hasCUI" );
        hasSUI = model.createProperty( gemmaAnnNS + "hasSUI" );
        hasScore = model.createProperty( gemmaAnnNS + "hasScore" );
        hasPhrase = model.createProperty( gemmaAnnNS + "hasPhrase" );
        describedBy = model.createProperty( gemmaAnnNS + "describedBy" );
    }
}
