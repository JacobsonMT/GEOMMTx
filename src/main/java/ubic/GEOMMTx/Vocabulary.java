package ubic.GEOMMTx;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

public class Vocabulary {
    public static Property spanStart;
    public static Property spanEnd;
    public static Property hasMention;
    public static Property mappedTerm;
    public static Property hasCUI;
    public static Property hasSUI;
    public static Property hasScore;
    public static Property hasPhrase;
    public static Property describedBy;
    static {
        String gemmaAnnNS = "http://bioinformatics.ubc.ca/Gemma/ws/xml/gemmaAnnotations.owl#";
        Model model = ModelFactory.createDefaultModel();

        spanStart = model.createProperty( gemmaAnnNS + "spanStart" );
        spanEnd = model.createProperty( gemmaAnnNS + "spanEnd" );
        hasMention = model.createProperty( gemmaAnnNS + "hasMention" );
        mappedTerm = model.createProperty( gemmaAnnNS + "mappedTerm" );
        hasCUI = model.createProperty( gemmaAnnNS + "hasCUI" );
        hasSUI = model.createProperty( gemmaAnnNS + "hasSUI" );
        hasScore = model.createProperty( gemmaAnnNS + "hasScore" );
        hasPhrase = model.createProperty( gemmaAnnNS + "hasPhrase" );
        describedBy = model.createProperty( gemmaAnnNS + "describedBy" );
    }
}
