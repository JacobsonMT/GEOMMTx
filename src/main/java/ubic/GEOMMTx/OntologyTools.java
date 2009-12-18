/*
 * The GEOMMTx project
 * 
 * Copyright (c) 2007 Columbia University
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

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Paul
 * @version $Id$
 */
public class OntologyTools {

    /**
     * Given a variable for a sparql query and a solution/result set it will give you a string to represent it.
     * 
     * @param var
     * @param soln
     * @return String to represent the var
     */

    public static String varToString( String var, QuerySolution soln ) {
        try {
            Resource r = soln.getResource( var );
            if ( r == null ) return null;
            return r.toString();
        } catch ( ClassCastException c ) {
            Literal l = soln.getLiteral( var );
            return l.getString();
        }
    }

}
