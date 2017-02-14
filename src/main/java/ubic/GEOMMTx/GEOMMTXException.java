/*
 * The GEOMMTx project
 * 
 * Copyright (c) 2014 University of British Columbia
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

/**
 * TODO Document Me
 * 
 * @author Paul
 * @version $Id$
 */
public class GEOMMTXException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 7697201966452424516L;

    public GEOMMTXException() {
        super();
    }

    public GEOMMTXException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }

    public GEOMMTXException( String message, Throwable cause ) {
        super( message, cause );
    }

    public GEOMMTXException( String message ) {
        super( message );
    }

    public GEOMMTXException( Throwable cause ) {
        super( cause );
    }

}
