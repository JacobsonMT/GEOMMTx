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
package ubic.GEOMMTx.filters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ubic.GEOMMTx.LabelLoader;
import ubic.GEOMMTx.SetupParameters;

import com.hp.hpl.jena.rdf.model.Model;

public class UninformativeFilter extends AbstractFilter {
    Set<String> frequentURLs;

    public String getName() {
        return "Frequent URL/class/concept remover";
    }

    public Set<String> getFrequentURLs() {
        return frequentURLs;
    }

    public UninformativeFilter() throws Exception {
        frequentURLs = new HashSet<String>();
        BufferedReader f = new BufferedReader( new FileReader( SetupParameters.config
                .getString( "gemma.annotator.uselessFrequentURLsFile" ) ) );
        String line;
        while ( ( line = f.readLine() ) != null ) {
            frequentURLs.add( line );
        }
        f.close();
    }

    @Override
    public int filter( Model model ) {
        return removeMentionsURLs( model, frequentURLs );
    }
/*
 * this main method prints out the labels of the uninformative URIs
 */
    public static void main( String args[] ) throws Exception {
        Map<String, String> labels = LabelLoader.readLabels();
        
        BufferedReader f = new BufferedReader( new FileReader( SetupParameters.config
                .getString( "gemma.annotator.uselessFrequentURLsFile" ) ) );
        String line;
        while ( ( line = f.readLine() ) != null ) {
            System.out.println( labels.get( line ) + " -> " + line );
        }
        f.close();

    }

}
