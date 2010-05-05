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
package ubic.GEOMMTx.mappers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.CUIMapper;
import ubic.GEOMMTx.UMLSSourceCode;
import ubic.GEOMMTx.util.SetupParameters;

/**
 * This class is for mapping to an ontology that has CUI's in it, here you are going backwards from an ontology to UMLS
 * 
 * @author Leon
 * @version $Id$
 */
public abstract class AbstractToUMLSMapper implements CUIMapper {
    protected static Log log = LogFactory.getLog( AbstractToUMLSMapper.class );

    //
    protected Map<String, Set<String>> CUIMap;

    // save
    // loadFromDisk
    // loadfromOntology

    public AbstractToUMLSMapper() {
        super();
        try {
            log.info( "getting " + getMainURL() );
            loadFromDisk();
            log.info( "loaded from disk, mappings:" + CUIMap.size() );
        } catch ( Exception e ) {
            log.info( "can't load from disk, loading from ontology" );
            loadFromOntology();
            log.info( "loaded, mappings:" + CUIMap.size() );
            save();
        }
    }

    /**
     * Converts a UMLS concept into a set of URI's for a specific ontology
     * 
     * @param CUI UMLS concept identifier
     * @return
     */
    public Set<String> convert( String CUI, Collection<UMLSSourceCode> sourceCodes ) {
        return CUIMap.get( CUI );
    }

    public int countOnetoMany() {
        int result = 0;
        for ( Set<String> URISet : CUIMap.values() ) {
            if ( URISet.size() > 1 ) result++;
        }
        return result;
    }

    /**
     * @return
     */
    public Set<String> getAllURLs() {
        Set<String> result = new HashSet<String>();
        for ( Set<String> URLs : CUIMap.values() ) {
            if ( URLs != null ) {
                result.addAll( URLs );
            }
        }
        return result;
    }

    /**
     * @return
     */
    public String getFileName() {
        return SetupParameters.getString( "geommtx.data", System.getProperty( "user.dir" ) )
                + this.getClass().getName() + ".mappings";
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void loadFromDisk() throws Exception {
        ObjectInputStream o = new ObjectInputStream( new FileInputStream( getFileName() ) );
        CUIMap = ( Map<String, Set<String>> ) o.readObject();
        o.close();
    }

    /**
     * 
     */
    public void save() {
        try {
            ObjectOutputStream o = new ObjectOutputStream( new FileOutputStream( getFileName() ) );
            o.writeObject( CUIMap );
            o.close();
            log.info( "Wrote CUI mappings to " + getFileName() );
        } catch ( IOException e ) {
            log.info( "Cannot save CUI mappings to " + getFileName() + ": " + e.getMessage() );
        }
    }

    abstract String getMainURL();

    abstract void loadFromOntology();
}