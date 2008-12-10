GEOMMTx,  Semantic Annotator for Biomedical Text

Requires:
UMLS isntallation
MMTx installation
Internet access

Installation:
Copy mmtxProjectJS.jar from your MMTx installation folder to the project root folder
Edit Annotator.properties to point to cui_source_loc from your MMTx installation folder (nls/mmtx/data/2006/mmtx)
Edit Annotator.properties to point to a MRCONSO.RRF file from a UMLS installation (META/)
Run mvn install to download libraries

Execution:
ubic.GEOMMTx.ExampleAnnotator.java provides a simple class for executing the pipeline.  It's input is the first command line argument, and it's output is example.rdf
Note: the first run may take some time as it makes the mappings for BIRNLex and Disease Ontology (after the first run its stored locally)

Contact: leonfrench@gmail.com
