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

public class UMLSSourceCode {
    private String source;
    private String code;

    public UMLSSourceCode( String source ) {
        super();
        this.source = source;
        this.code = null;
    }

    public UMLSSourceCode( String source, String code ) {
        super();
        this.source = source;
        this.code = code;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final UMLSSourceCode other = ( UMLSSourceCode ) obj;
        if ( code == null ) {
            if ( other.code != null ) return false;
        } else if ( !code.equals( other.code ) ) return false;
        if ( source == null ) {
            if ( other.source != null ) return false;
        } else if ( !source.equals( other.source ) ) return false;
        return true;
    }

    public String getCode() {
        return code;
    }

    public String getSource() {
        return source;
    }

    public boolean hasCode() {
        return code == null;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( code == null ) ? 0 : code.hashCode() );
        result = PRIME * result + ( ( source == null ) ? 0 : source.hashCode() );
        return result;
    }

    public void setCode( String code ) {
        this.code = code;
    }

    public void setSource( String source ) {
        this.source = source;
    }

    @Override
    public String toString() {
        return code + "[" + source + "]";
    }
}
