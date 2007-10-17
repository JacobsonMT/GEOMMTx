package ubic.GEOMMTx.mappers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.GEOMMTx.UMLSSourceCode;

public abstract class AbstractMapper {
    protected static Log log = LogFactory.getLog( AbstractMapper.class );

    protected static  String MAIN_URL=null;
    
    //
    protected Map<String, String> CUIMap;

    // save
    // loadFromDisk
    // loadfromOntology

    public AbstractMapper() {
        super();
        try {
            loadFromDisk();
            log.info( "loaded from disk" );
        } catch ( Exception e ) {
            log.info( "can't load from disk, loading from ontology" );
            loadFromOntology();
            save();
        }

    }

    public void loadFromDisk() throws Exception {
        ObjectInputStream o = new ObjectInputStream(new FileInputStream(this.getClass().getName()));
        CUIMap = (Map<String, String>)o.readObject();
        o.close();
        
    }

    abstract void loadFromOntology();

    public String convert( String CUI, Collection<UMLSSourceCode> sourceCodes ) {
        return CUIMap.get( CUI );
    }

    public void save() {
        try {
            ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(this.getClass().getName()));
            o.writeObject( CUIMap );
            o.close();
        } catch ( Exception e ) {
            log.info( "cannot save CUI mappings" );
            e.printStackTrace();
        }
    }
}