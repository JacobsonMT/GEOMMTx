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

import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import ubic.GEOMMTx.filters.AbstractFilter;
import ubic.GEOMMTx.filters.CUIIRIFilter;
import ubic.GEOMMTx.filters.CUISUIFilter;
import ubic.GEOMMTx.filters.UninformativeFilter;
import ubic.GEOMMTx.mappers.BirnLexMapper;
import ubic.GEOMMTx.mappers.DiseaseOntologyMapper;
import ubic.GEOMMTx.mappers.FMALiteMapper;

/**
 * This is an example class for how to execute the annotation pipeline.
 * 
 * @author leon
 */
public class ExampleAnnotator {

    /**
     * 
     * @param args
     */
    public static void main( String[] args ) throws Exception {
        if ( args.length == 0) {
            System.out.println( "Usage: ExampleAnnotator \"text to be processed\"" );
            System.exit(1);
        }
        String outputFile = "example.rdf";

        String text = args[0];
        text = text.replace( ')', ' ' );
        text = text.replace( '(', ' ' );
        text = text.replace( '/', ' ' );

        // initalize MMTx
        // uses options from config file
        Text2Owl text2Owl = new Text2Owl();
        text2Owl.addMapper( new BirnLexMapper() );
        text2Owl.addMapper( new FMALiteMapper() );
        text2Owl.addMapper( new DiseaseOntologyMapper() );

        List<AbstractFilter> filters = new LinkedList<AbstractFilter>();
        // requires spreadsheet evaluation
        filters.add( new CUISUIFilter() );
        filters.add( new CUIIRIFilter() );
        // requires list of uninformative URIs
        filters.add( new UninformativeFilter() );

        Model model = ModelFactory.createDefaultModel();
        Resource root = model.createResource( "http://www.bioinformatics.ubca.ca/testing/example" );

        model = text2Owl.processText( text, root );

        FileWriter fout = new FileWriter( outputFile );
        model.write( fout );
        fout.close();
        System.out.println( "RDF graph wrote to " + outputFile );
    }

}
