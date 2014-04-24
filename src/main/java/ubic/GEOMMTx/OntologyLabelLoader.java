/*
 * The GEOMMTx project
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.ontology.OntologyLoader;
import ubic.basecode.util.Configuration;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Load ontologies.
 * 
 * @author lfrench
 * @version $Id$
 */
public class OntologyLabelLoader {
    protected static Log log = LogFactory.getLog( OntologyLabelLoader.class );

    public Model loadOntologies() {
        Model model = ModelFactory.createDefaultModel();

        RDFNode nullNode = null;
        Selector labelSelector = new SimpleSelector( null, RDFS.label, nullNode );

        OntModel nifstd = OntologyLoader.loadMemoryModel( Configuration.getString( "url.nifstdOntology" ) );
        log.info( "loaded nifstd..." );
        model.add( nifstd.listStatements( labelSelector ) );
        log.info( "Done merging nifstd..." );
        nifstd.close();

        OntModel FMAlite = OntologyLoader.loadMemoryModel( Configuration.getString( "url.fmaOntology" ) );
        log.info( "loaded FMA" );
        model.add( FMAlite.listStatements( labelSelector ) );
        log.info( "Done merging FMA..." );
        FMAlite.close();

        OntModel DO = OntologyLoader.loadMemoryModel( Configuration.getString( "url.diseaseOntology" ) );
        log.info( "loaded Disease ontology" );
        model.add( DO.listStatements( labelSelector ) );
        log.info( "Done merging Disease Ontology..." );
        DO.close();

        return model;
    }

}
