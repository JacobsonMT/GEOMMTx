package ubic.GEOMMTx;

import gov.nih.nlm.nls.mmtx.MMTxAPI;
import gov.nih.nlm.nls.nlp.textfeatures.Candidate;
import gov.nih.nlm.nls.nlp.textfeatures.Document;
import gov.nih.nlm.nls.nlp.textfeatures.FinalMapping;
import gov.nih.nlm.nls.nlp.textfeatures.Phrase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.assembler.acceptance.AllAccept.SetupDatabase;

public class MMTxRunner {
	private static final long serialVersionUID = 1L;
	
	protected static Log log = LogFactory.getLog( MMTxRunner.class );
	
	private MMTxAPI MMTx;

	private int scoreThreshold;

	public MMTxRunner() {
		this(new String[] {});
	}
    

	public MMTxRunner(String[] options) {
		this(SetupParameters.scoreThreshold, options);
	}

	
	private Cache memoryOnlyCache;

	public MMTxRunner(int scoreThreshold, String[] options) {
		this.scoreThreshold = scoreThreshold;
		CacheManager singletonManager = CacheManager.create();
        
        memoryOnlyCache = singletonManager.getCache("realCache");
        if (memoryOnlyCache == null) {
            memoryOnlyCache = new Cache("realCache", 25, false, false, 5000, 1500);
            singletonManager.addCache(memoryOnlyCache);
        }

		try {
			MMTx = new MMTxAPI(options);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	

	public List<Phrase> getPhrases(String text) {
		// check to see if we done it before
		Element element = memoryOnlyCache.get(text);
		if (element != null) {
			log.info("using phrase cache");
			return (List<Phrase>)(memoryOnlyCache.get(text).getObjectValue());
		}

		Document doc = null;
		List<Phrase> results = new ArrayList<Phrase>();

		// MMTX processing
		try {
			doc = MMTx.processDocument(text);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		if (doc.getPhrases() == null)
			return results;

		for (Object phraseObj : doc.getPhrases()) {
			results.add((Phrase) phraseObj);
		} // end for

		memoryOnlyCache.put(new Element(text, results));
		return results;
	}

	public Collection<Candidate> getConcepts(Phrase phrase) {
		Collection<Candidate> results = new ArrayList<Candidate>();

		List finalMappings = phrase.getFinalMappings();

		// somtimes finalMappings is null, guess this happens when it can't find
		// anything
		if (finalMappings == null)
			return results;

		// go through the mappings
		for (Object mappingIterator : finalMappings) {
			FinalMapping aMapping = (FinalMapping) mappingIterator;

			// go through the concepts which are Candidates
			for (Object cObj : aMapping.getConcepts()) {
				Candidate concept = (Candidate) cObj;
				if (concept.getFinalScore() > scoreThreshold) {
					results.add(concept);
				}
			} // end for
		} // end for
		return results;
	}

	public Collection<Candidate> getConcepts(String text) {
		Document doc = null;
		Collection<Candidate> results = new ArrayList<Candidate>();

		for (Phrase p : getPhrases(text)) {
			results.addAll(getConcepts(p));
		} // end for
		return results;
	}

	public int getScoreThreshold() {
		return scoreThreshold;
	}

	public void setScoreThreshold(int scoreThreshold) {
		this.scoreThreshold = scoreThreshold;
	}

}
