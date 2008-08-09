package ubic.GEOMMTx;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

public class Text2OwlModelTools {
    static public void removeMentions( Model model, Set<Resource> affectedMentions ) {
        Set<Resource> affectedPhrases = new HashSet<Resource>();
        // remove all the links from the mention to its properties
        for ( Resource mention : affectedMentions ) {
            // mention.addLiteral( isRejected, true );
            // remove all of its properties (leaves a blank mention behind)
            mention.removeProperties();

            // get its phrase, assume it only has one
            ResIterator iterator = model.listResourcesWithProperty( Vocabulary.hasMention, mention );
            Resource phrase = ( Resource ) iterator.next();

            affectedPhrases.add( phrase );
            model.remove( model.createStatement( phrase, Vocabulary.hasMention, mention ) );
        }

        // this leaves behind some orphan phrases
        // remove them
        for ( Resource phrase : affectedPhrases ) {
            // make sure it has no mentions
            if ( model.listStatements( phrase, Vocabulary.hasMention, ( RDFNode ) null ).hasNext() == false ) {
                // log.info( phrase.listProperties(RDFS.label).toSet() );
                phrase.removeProperties();

                // remove the triple pointing to that phrase
                model.remove( model.listStatements( null, Vocabulary.hasPhrase, phrase ) );
            }
        }
    }
    
    public static Model loadModel( String file ) {
        Model model = ModelFactory.createDefaultModel();
        try {
            FileInputStream fi = new FileInputStream( file );
            model.read( fi, null );
            fi.close();
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        }
        return model;
    }
}
