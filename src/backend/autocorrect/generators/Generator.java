package backend.autocorrect.generators;

import java.util.Map;

import backend.autocorrect.Corpus;
import backend.autocorrect.SuggestionToken;

/**
 * Interface for any kind of concrete generator to implement
 * 
 * @author dgattey
 */
public interface Generator {
	
	/**
	 * Generates suggestions based off type of generator, added as tokens to the
	 * suggestions collection
	 * 
	 * @param suggestions a map from token name -> token representing
	 *            suggestions to use - mapped because it's easier to remove
	 *            duplicates
	 * @param corpus the corpus of words, including bigram and unigram frequency
	 * @param penultimateWord the second to last user token
	 * @param lastWord the last user token to generate suggestions for
	 */
	void generate(Map<String, SuggestionToken> suggestions, Corpus corpus, String penultimateWord, String lastWord);
}
