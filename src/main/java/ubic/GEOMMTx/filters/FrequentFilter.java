package ubic.GEOMMTx.filters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ubic.GEOMMTx.LabelLoader;
import ubic.GEOMMTx.SetupParameters;

import com.hp.hpl.jena.rdf.model.Model;

public class FrequentFilter extends AbstractFilter {
    Set<String> frequentURLs;

    public String getName() {
        return "Frequent URL/class/concept remover";
    }

    public Set<String> getFrequentURLs() {
        return frequentURLs;
    }

    public FrequentFilter() throws Exception {
        frequentURLs = new HashSet<String>();
        BufferedReader f = new BufferedReader( new FileReader( SetupParameters.config
                .getString( "gemma.annotator.uselessFrequentURLsFile" ) ) );
        String line;
        while ( ( line = f.readLine() ) != null ) {
            frequentURLs.add( line );
        }
        f.close();
    }

    @Override
    public int filter( Model model ) {
        return removeMentionsURLs( model, frequentURLs );
    }
/*
 * this main method prints out the labels of the uninformative URIs
 */
    public static void main( String args[] ) throws Exception {
        Map<String, String> labels = LabelLoader.readLabels();
        System.out.println( "Uninformative Labels" );
        FrequentFilter f = new FrequentFilter();
        for ( String URI : f.getFrequentURLs() ) {
            System.out.println( labels.get( URI ) + " -> " + URI );
        }
    }

}
