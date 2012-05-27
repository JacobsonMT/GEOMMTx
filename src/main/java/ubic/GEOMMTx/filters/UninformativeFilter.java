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
package ubic.GEOMMTx.filters;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ubic.GEOMMTx.util.SetupParameters;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * TODO document me
 * 
 * @author lfrench
 * @version $Id$
 */
public class UninformativeFilter extends AbstractFilter implements URIFilter {

    Set<String> frequentURLs;

    public UninformativeFilter() {
        frequentURLs = new HashSet<String>();
        try {
            String uselessurlsFilePath = SetupParameters.getString( "geommtx.annotator.uselessFrequentURLsFile" );
            BufferedReader f = new BufferedReader( new FileReader( uselessurlsFilePath ) );
            String line;
            while ( ( line = f.readLine() ) != null ) {
                frequentURLs.add( line );
            }
            f.close();
        } catch ( FileNotFoundException e ) {
            throw new RuntimeException( e );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public boolean accept( String URI ) {
        return !frequentURLs.contains( URI );
    }

    @Override
    public int filter( Model model ) {
        return removeMentionsURLs( model, frequentURLs );
    }

    public Set<String> getFrequentURLs() {
        return frequentURLs;
    }

    @Override
    public String getName() {
        return "Frequent URL/class/concept remover";
    }

}
