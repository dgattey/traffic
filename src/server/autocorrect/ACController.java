package server.autocorrect;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import data.MapException;
import server.io.IOController;

/**
 * Runs our autocorrector for the map nodes
 * 
 * @author dgattey
 */
public class ACController {
	
	private final Corpus		corpus;
	private final ACEngine		engine;
	private final static int	LED_DIST	= 3;
	private final static int	NUM_SUGGS	= 10;
	
	/**
	 * Creates a new autocorrect object given a filename for the FileParser to read from
	 * 
	 * @throws IOException an i/o error happened in the backend
	 * @throws MapException a maps error occured in the IOController, corpus, or ACEngine
	 */
	public ACController() throws MapException, IOException {
		corpus = new Corpus();
		corpus.addUnigrams(IOController.getAllWayNames());
		engine = new ACEngine(corpus, LED_DIST);
	}
	
	/**
	 * Suggests a list of strings given a target
	 * 
	 * @param target the string we're looking for
	 * @return a list of string representing the suggestions for target
	 */
	public List<String> suggest(final String target) {
		return engine.generateAndRank(Arrays.asList(target), NUM_SUGGS);
	}
	
}
