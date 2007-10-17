package ubic.GEOMMTx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class RDFMerge {
    // class for RDF filenames
    public static FilenameFilter RDFFileFileter = new FilenameFilter() {
        public boolean accept( File dir, String name ) {
            return name.toLowerCase().endsWith( ".rdf" );
        }
    };

    public static void mergeRDFFiles( File output, File[] files ) throws Exception {
        Model result = ModelFactory.createDefaultModel();
        for ( File file : files ) {
            result.read( new FileInputStream( file ), null );
        }
        result.write( new FileWriter( output ) );
    }

    public static void mergeWorkingDirRDF( String outputfile ) throws Exception {
        File workingDir = new File( "." );
        mergeRDFFiles( new File( outputfile ), workingDir.listFiles( RDFFileFileter ) );
    }

    public static void main( String args[] ) throws Exception {
        mergeWorkingDirRDF("testmerge.rdf");
    }

}
