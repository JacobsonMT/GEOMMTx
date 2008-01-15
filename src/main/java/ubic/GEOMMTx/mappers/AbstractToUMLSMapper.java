package ubic.GEOMMTx.mappers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.CUIMapper;
import ubic.GEOMMTx.UMLSSourceCode;

/**
 * This class is for mapping to an ontology that has CUI's in it, here you are going backwards from an ontology to UMLS
 * 
 * @author Leon
 */
public abstract class AbstractToUMLSMapper implements CUIMapper {
    protected static Log log = LogFactory.getLog( AbstractToUMLSMapper.class );

    //
    protected Map<String, String> CUIMap;

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


    public void loadFromDisk() throws Exception {
        ObjectInputStream o = new ObjectInputStream( new FileInputStream( getFileName() ) );
        CUIMap = ( Map<String, String> ) o.readObject();
        o.close();

    }

    abstract void loadFromOntology();

    abstract String getMainURL();

    /**
     * Converts a UMLS concept into URI for a specific ontology
     * 
     * @param CUI UMLS concept identifier
     * @return
     */
    public String convert( String CUI, Collection<UMLSSourceCode> sourceCodes ) {
        return CUIMap.get( CUI );
    }

    public String getFileName() {
        return this.getClass().getName() + ".mappings";
    }

    public void save() {
        try {
            ObjectOutputStream o = new ObjectOutputStream( new FileOutputStream( getFileName() ) );
            o.writeObject( CUIMap );
            o.close();
        } catch ( Exception e ) {
            log.info( "cannot save CUI mappings" );
            e.printStackTrace();
        }
    }
}