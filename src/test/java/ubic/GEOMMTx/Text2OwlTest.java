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

import org.junit.Test;

import ubic.GEOMMTx.mappers.BirnLexMapper;
import ubic.GEOMMTx.util.SetupParameters;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author paul
 * @version $Id$
 */
public class Text2OwlTest {

    @Test
    public void test() throws Exception {

        Text2Owl text2Owl = new Text2Owl( null, 0, SetupParameters.getStringArray( "geommtx.annotator.mmtxOptions" ) );
        text2Owl.addMapper( new BirnLexMapper() );

        Model model = ModelFactory.createDefaultModel();
        Resource root = model.createResource( "http://www.bioinformatics.ubca.ca/testing/umls#Sample" );

        model = text2Owl.processText(
                "Expression data from adult laboratory mouse brain hemispheres hippocampus cerebellum leg", root );

        StringWriter fout = new StringWriter();
        model.write( fout );
        fout.close();

        String results = fout.toString();
        System.err.println( results );
        assertTrue( results.contains( "purl.org" ) );
        assertTrue( results.contains( "BIRNLex" ) );

    }

}
