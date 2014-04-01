/**
 * 
 */
package backend.autocorrect.generators;

import java.util.Map;

import backend.autocorrect.Corpus;
import backend.autocorrect.SuggestionToken;

/**
 * A basic class to assist in generating words based off LED
 * 
 * @author dgattey
 */
public class LEDGenerator implements Generator {
	
	public final int	maxDistance;
	
	/**
	 * Constructor sets default values
	 * 
	 * @param maxDistance the maximum LED to look for
	 */
	public LEDGenerator(int maxDistance) {
		this.maxDistance = maxDistance;
	}
	
	/**
	 * Generates suggestions and adds them to the suggestions array
	 */
	@Override
	public void generate(Map<String, SuggestionToken> suggestions, Corpus corpus, String pWord, String lastWord) {
		final LEDObject obj = new LEDObject(lastWord, maxDistance);
		corpus.makeSuggestions(obj);
		for (final String s : obj.getWords()) {
			final int bigramCount = pWord.isEmpty() ? 0 : corpus.getBigramCount(pWord, s);
			suggestions.put(s, new SuggestionToken(s, corpus.getUnigramCount(s), bigramCount));
		}
	}
}
