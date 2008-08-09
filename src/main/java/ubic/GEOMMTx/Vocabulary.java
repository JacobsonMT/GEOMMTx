package ubic.GEOMMTx;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

public class Vocabulary {
    static Property spanStart;
    static Property spanEnd;
    static Property hasMention;
    static Property mappedTerm;
    static Property hasCUI;
    static Property hasSUI;
    static Property hasScore;
    static Property hasPhrase;
    static {
        String leonNS = "http://www.purl.org/leon/umls#";
        Model model = ModelFactory.createDefaultModel();

        spanStart = model.createProperty( leonNS + "spanStart" );
        spanEnd = model.createProperty( leonNS + "spanEnd" );
        hasMention = model.createProperty( leonNS + "hasMention" );
        mappedTerm = model.createProperty( leonNS + "mappedTerm" );
        hasCUI = model.createProperty( leonNS + "hasCUI" );
        hasSUI = model.createProperty( leonNS + "hasSUI" );
        hasScore = model.createProperty( leonNS + "hasScore" );
        hasPhrase = model.createProperty( leonNS + "hasPhrase" );
    }
}
