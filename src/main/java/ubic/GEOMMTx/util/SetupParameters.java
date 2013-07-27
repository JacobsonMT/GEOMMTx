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
package ubic.GEOMMTx.util;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.io.FileHandler;
import org.apache.commons.lang3.StringUtils;

import ubic.GEOMMTx.evaluation.CUIIRIPair;

/**
 * @author lfrench
 * @version $Id$
 */
public class SetupParameters {

    private static CompositeConfiguration config;

    /**
     * Name of the resource containing defaults that the user can override (classpath)
     */
    private static final String DEFAULT_CONFIGURATION = "geommtx.default.properties";

    // The few rejected CUI to IRI/URL pairings as determined by human curation
    public static HashSet<CUIIRIPair> rejectedCUIIRIPairs;

    static String USER_CONFIGURATION = "geommtx.properties";

    static {
        config = new CompositeConfiguration();
        config.addConfiguration( new SystemConfiguration() );

        try {
            PropertiesConfiguration pc = new PropertiesConfiguration();
            FileHandler handler = new FileHandler( pc );
            handler.setFileName( USER_CONFIGURATION );
            handler.load();

            config.addConfiguration( pc );
        } catch ( Exception e ) {
            System.out.println( "Could not load " + USER_CONFIGURATION );
            System.exit( 1 );
        }

        if ( StringUtils.isBlank( config.getString( "geommtx.home" ) ) ) {
            System.out.println( "You must define geommtx.home" );
            System.exit( 1 );
        }
        if ( StringUtils.isBlank( config.getString( "mmtx.home" ) ) ) {
            System.out.println( "You must define mmtx.home" );
            System.exit( 1 );
        }

        try {
            PropertiesConfiguration pc = new PropertiesConfiguration();
            FileHandler handler = new FileHandler( pc );
            handler.setFileName( DEFAULT_CONFIGURATION );
            handler.load();
            config.addConfiguration( pc );
        } catch ( ConfigurationException e ) {
            System.out.println( "Could not load " + DEFAULT_CONFIGURATION );
            System.exit( 1 );
        }

        rejectedCUIIRIPairs = new HashSet<CUIIRIPair>();
        rejectedCUIIRIPairs.add( new CUIIRIPair( "http://www.purl.org/umls/umls#C0001162",
                "http://purl.org/obo/owl/FMA#FMA_50869" ) );
        rejectedCUIIRIPairs.add( new CUIIRIPair( "http://www.purl.org/umls/umls#C0001271",
                "http://purl.org/obo/owl/FMA#FMA_67843" ) );
        rejectedCUIIRIPairs.add( new CUIIRIPair( "http://www.purl.org/umls/umls#C0001625",
                "http://purl.org/obo/owl/FMA#FMA_9604" ) );
        rejectedCUIIRIPairs.add( new CUIIRIPair( "http://www.purl.org/umls/umls#C0001655",
                "http://purl.org/obo/owl/FMA#FMA_74639" ) );

    }

    /**
     * @param key
     * @return
     * @see org.apache.commons.configuration.AbstractConfiguration#getBoolean(java.lang.String)
     */
    public static boolean getBoolean( String key ) {
        return config.getBoolean( key );
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     * @see org.apache.commons.configuration.AbstractConfiguration#getBoolean(java.lang.String, boolean)
     */
    public static boolean getBoolean( String key, boolean defaultValue ) {
        return config.getBoolean( key, defaultValue );
    }

    /**
     * @param key
     * @return
     * @see org.apache.commons.configuration.AbstractConfiguration#getDouble(java.lang.String)
     */
    public static double getDouble( String key ) {
        return config.getDouble( key );
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     * @see org.apache.commons.configuration.AbstractConfiguration#getDouble(java.lang.String, double)
     */
    public static double getDouble( String key, double defaultValue ) {
        return config.getDouble( key, defaultValue );
    }

    /**
     * @param key
     * @return
     * @see org.apache.commons.configuration.AbstractConfiguration#getInt(java.lang.String)
     */
    public static int getInt( String key ) {
        return config.getInt( key );
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     * @see org.apache.commons.configuration.AbstractConfiguration#getInt(java.lang.String, int)
     */
    public static int getInt( String key, int defaultValue ) {
        return config.getInt( key, defaultValue );
    }

    /**
     * @return
     * @see org.apache.commons.configuration.CompositeConfiguration#getKeys()
     */
    public static Iterator<String> getKeys() {
        return config.getKeys();
    }

    public static Iterator<String> getKeys( String k ) {
        return config.getKeys( k );
    }

    /**
     * @param key
     * @return
     * @see org.apache.commons.configuration.CompositeConfiguration#getKeys(java.lang.String)
     */
    public static String getString( String key ) {
        return config.getString( key );
    }

    public static String getString( String key, String defaultValue ) {
        return config.getString( key, defaultValue );
    }

    /**
     * @param key
     * @return
     * @see org.apache.commons.configuration.CompositeConfiguration#getStringArray(java.lang.String)
     */
    public static String[] getStringArray( String key ) {
        return config.getStringArray( key );
    }

}
