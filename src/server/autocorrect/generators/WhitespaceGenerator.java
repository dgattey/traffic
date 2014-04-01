package server.autocorrect.generators;

import java.util.Map;
import java.util.Map.Entry;

import server.autocorrect.Corpus;
import server.autocorrect.SuggestionToken;

/**
 * @author dgattey
 */
public class WhitespaceGenerator implements Generator {
	
	@Override
	public void generate(Map<String, SuggestionToken> suggestions, Corpus corpus, String pWord, String lastWord) {
		final WhitespaceObject obj = new WhitespaceObject(lastWord);
		corpus.makeSuggestions(obj);
		for (final Entry<String, String> e : obj.getWords()) {
			final String s = e.getKey();
			final String s2 = e.getValue();
			final int bigramCount = pWord.isEmpty() ? 0 : corpus.getBigramCount(pWord, s);
			suggestions.put(s, new SuggestionToken(s, s2, corpus.getUnigramCount(s), bigramCount));
		}
	}
	
}
