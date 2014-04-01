package backend.autocorrect.generators;

import java.util.Map;

import backend.autocorrect.Corpus;
import backend.autocorrect.SuggestionToken;

/**
 * @author dgattey
 */
public class PrefixGenerator implements Generator {
	
	@Override
	public void generate(Map<String, SuggestionToken> suggestions, Corpus corpus, String pWord, String lastWord) {
		final PrefixObject p = new PrefixObject(lastWord);
		corpus.makeSuggestions(p);
		for (String s : p.getAllWords()) {
			s = lastWord.concat(s);
			final int bigramCount = pWord.isEmpty() ? 0 : corpus.getBigramCount(pWord, s);
			suggestions.put(s, new SuggestionToken(s, corpus.getUnigramCount(s), bigramCount));
		}
	}
	
}
