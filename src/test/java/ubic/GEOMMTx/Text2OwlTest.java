/*
 * The GEOMMTx project
 * 
 * Copyright (c) 2009 University of British Columbia
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

import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ubic.GEOMMTx.mappers.NIFSTDMapper;
import ubic.GEOMMTx.util.SetupParameters;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Test of text scanning into ontology terms.
 * 
 * @author paul
 * @version $Id$
 */
public class Text2OwlTest {

    private static Log log = LogFactory.getLog( Text2OwlTest.class );

    @Test
    public void test() throws Exception {

        Text2Owl text2Owl = new Text2Owl( null, 0, SetupParameters.getStringArray( "geommtx.annotator.mmtxOptions" ) );
        text2Owl.addMapper( new NIFSTDMapper() );

        Model model = ModelFactory.createDefaultModel();
        Resource root = model.createResource( "http://www.bioinformatics.ubca.ca/testing/umls#Sample" );

        model = text2Owl.processText(
                "Expression data from adult laboratory mouse brain hemispheres hippocampus cerebellum leg", root );

        try (StringWriter fout = new StringWriter();) {
            model.write( fout );

            String results = fout.toString();
            log.debug( results );
            assertTrue( results.contains( "purl.org" ) );
            assertTrue( results.contains( "NIF" ) );
        }

    }

}
