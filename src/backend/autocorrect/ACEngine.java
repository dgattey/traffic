package backend.autocorrect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import backend.autocorrect.comparators.DumbComparator;
import backend.autocorrect.comparators.SuggestionComparator;
import backend.autocorrect.generators.Generator;
import backend.autocorrect.generators.PrefixGenerator;
import backend.autocorrect.generators.WhitespaceGenerator;

/**
 * Generator class that takes a map of flags, the corpus, and the Levenshtein Edit Distance to make a generator that can
 * generator a list of possibilities for given tokens.
 * 
 * @author dgattey
 */
public class ACEngine {
	
	private final Corpus						corpus;
	private final Map<String, SuggestionToken>	suggestions;
	private final Collection<Generator>			generators;
	private final SuggestionComparator			comparator;
	
	/**
	 * Creates a generator object with a map of flags to booleans, a corpus, and the led count. Just sets global
	 * variables.
	 * 
	 * @param corpus the full corpus for this generator to use
	 * @param led the Levenshtein Edit Distance set by the user (defaults to 0)
	 */
	public ACEngine(final Corpus corpus, final Integer led) {
		this.corpus = corpus;
		suggestions = new HashMap<>();
		comparator = new DumbComparator();
		
		// Add all possible generators
		generators = new HashSet<>();
		generators.add(new PrefixGenerator());
		generators.add(new WhitespaceGenerator());
		generators.add(new Generator() {
			
			@Override
			/**
			 * Add generator that simply adds the result if it existed in the corpus onto
			 * the suggestions if not prefix matching
			 */
			public void generate(final Map<String, SuggestionToken> suggestions, final Corpus corpus,
					final String pWord, final String lastWord) {
				if (corpus.existsWord(lastWord)) {
					final int uCount = Integer.MAX_VALUE;
					final int bCount = pWord.isEmpty() ? 0 : corpus.getBigramCount(pWord, lastWord);
					suggestions.put(lastWord, new SuggestionToken(lastWord, uCount, bCount));
				}
			}
		});
	}
	
	/**
	 * Generates a set of tokens based off the given flags and the tokens entered by the user.
	 * 
	 * @param tokens a list of strings the user entered, parsed
	 */
	private void generate(final List<String> tokens) {
		suggestions.clear(); // Get rid of the last suggestions
		
		// Variables
		final int lastTokenIndex = tokens.size() - 1;
		final String lastWord = tokens.get(lastTokenIndex);
		String penultimateWord = "";
		if (lastTokenIndex != 0) {
			penultimateWord = tokens.get(lastTokenIndex - 1);
		}
		
		// Call generate on each of the saved generators
		for (final Generator g : generators) {
			g.generate(suggestions, corpus, penultimateWord, lastWord);
		}
	}
	
	/**
	 * Rank the given suggestions based off the tokens
	 * 
	 * @param tokens a string list representing tokenized user input
	 * @param numTokens the number of tokens to display
	 * @return a list of top five strings
	 */
	public List<String> generateAndRank(final List<String> tokens, final int numTokens) {
		generate(tokens);
		
		final String s = makeStringAllButLast(tokens);
		final int last = tokens.size();
		comparator.addInformation(last > 1, tokens.get(last - 1));
		final List<SuggestionToken> suggs = new ArrayList<>(suggestions.values());
		Collections.sort(suggs, comparator);
		final List<String> results = new LinkedList<>();
		
		// Find right size of sublist
		int end = suggs.size();
		if (end > numTokens) {
			end = numTokens; // only pick 5 items
		}
		
		// Either add "fullString" or "s fullString"
		for (final SuggestionToken t : suggs.subList(0, end)) {
			final String toAdd = s.isEmpty() ? t.fullString() : s + " " + t.fullString();
			results.add(toAdd);
		}
		return results;
	}
	
	/**
	 * Makes a string out of a string list of tokens
	 * 
	 * @param tokens the list to concat together
	 * @return a string of the sentence
	 */
	private static String makeStringAllButLast(final List<String> tokens) {
		String s = "";
		final int size = tokens.size() - 1;
		if (size < 1) {
			return s;
		}
		
		// Only go to the second to last item
		s = tokens.get(0);
		if (size < 2) {
			return s;
		}
		
		// Add all strings with spaces between
		for (final String str : tokens.subList(1, size)) {
			s = String.format("%s %s", s, str);
		}
		return s;
	}
}
