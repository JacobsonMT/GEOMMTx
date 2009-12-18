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
        Text2Owl text2Owl = new Text2Owl( null, 0, new String[] { "--an_derivational_variants", "--no_acros_abbrs" } );
        text2Owl.addMapper( new BirnLexMapper() );

        Model model = ModelFactory.createDefaultModel();
        Resource root = model.createResource( "http://www.bioinformatics.ubca.ca/testing/umls#Sample" );

        model = text2Owl.processText(
                "Expression data from adult laboratory mouse brain hemispheres hippocampus cerebellum leg", root );

        // Hippocampus CA3 acute
        // model = text2Owl.processText( "Sample # Group OD 260/280 RNA, ug/ul Actb Ct Chip 1 PregPBS 2.0 0.63 13.8 a 2
        // ", root );
        // log.info( "here" );
        // model = text2Owl.processText( "Sample # Group OD 260/280 RNA, ug/ul Actb Ct Chip 1 PregPBS 2.0 0.63 13.8 a 2
        // PregPBS 2.0 0.647 13.3 j 3 PregPBS 2.1 0.612 13.4 l 4 ", root );
        // log.info( "here" );
        // model = text2Owl.processText( "Sample # Group OD 260/280 RNA, ug/ul Actb Ct Chip 1 PregPBS 2.0 0.63 13.8 a 2
        // PregPBS 2.0 0.647 13.3 j 3 PregPBS 2.1 0.612 13.4 l 4 PregPBS 2.0 0.575 13.8 m 6 PregTiO 2.1 0.585 14.3 n 8
        // PregTiO 2.1 0.458 13.8 o 9 NormPBS 2.0 0.627 13.3 p 11", root );
        // log.info( "here" );
        // model = text2Owl.processText( "0.575 13.8 m 6 PregTiO 2.1 0.585 14.3 n 8 PregTiO 2.1 0.458 13.8 o 9 NormPBS
        // 2.0 0.627 13.3 p 11 NormPBS 2.0 0.714 13.4 b 12 NormPBS 2.1 0.462 13.8 c 13 NormTiO 2.0 0.572 15.6 d 14
        // NormTiO 2.1 0.598 13.1 e 15 NormTiO 2.1 0.682 13.6 f 16 NormTiO 2.1 0.654 13.6 g 17 PregTiO 2.1 0.586 13.7 h
        // 18 PregTiO 2.0 0.833 12.8 i 20 NormPBS 2.0 0.804 12.5 k Source GEO sample is GSM180989 Last updated
        // (according to GEO): Apr 12 2007", root );
        // log.info( "here" );
        // model = text2Owl.processText( "Sample # Group OD 260/280 RNA, ug/ul Actb Ct Chip 1 PregPBS 2.0 0.63 13.8 a 2
        // PregPBS 2.0 0.647 13.3 j 3 PregPBS 2.1 0.612 13.4 l 4 PregPBS 2.0 0.575 13.8 m 6 PregTiO 2.1 0.585 14.3 n 8
        // PregTiO 2.1 0.458 13.8 o 9 NormPBS 2.0 0.627 13.3 p 11 NormPBS 2.0 0.714 13.4 b 12 NormPBS 2.1 0.462 13.8 c
        // 13 NormTiO 2.0 0.572 15.6 d 14 NormTiO 2.1 0.598 13.1 e 15 NormTiO 2.1 0.682 13.6 f 16 NormTiO 2.1 0.654 13.6
        // g 17 PregTiO 2.1 0.586 13.7 h 18 PregTiO 2.0 0.833 12.8 i 20 NormPBS 2.0 0.804 12.5 k Source GEO sample is
        // GSM180989 Last updated (according to GEO): Apr 12 2007", root );
        // model = text2Owl.processText( "", root );

        /*
         * model = text2Owl .processText( "Serum here. Estrogen receptor status in breast cancer is associated with
         * remarkably distinct gene expression patterns. Serum at end.", root ); model = text2Owl .processText( "Serum
         * here. Estrogen receptor status in breast cancer is associated with remarkably distinct gene expression
         * patterns. Serum at end.", root ); /* model = text2Owl .processText( "Serum here. Estrogen receptor status in
         * breast cancer is associated with remarkably distinct gene expression patterns. Serum at end.", root ); model
         * = text2Owl .processText( "Serum here. Estrogen receptor status in breast cancer is associated with remarkably
         * distinct gene expression patterns. Serum at end.", root ); model = text2Owl.processText( "Breast cancer",
         * root ); model = text2Owl .processText( "Serum here. Estrogen receptor status in breast cancer is associated
         * with remarkably distinct gene expression patterns. Serum at end.", root );
         */
        StringWriter fout = new StringWriter();
        model.write( fout );
        fout.close();

        String results = fout.toString();
        System.err.println( results );
        assertTrue( results.contains( "purl.org" ) );
        assertTrue( results.contains( "BIRNLex" ) );

    }

}
