package backend.autocorrect;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import backend.autocorrect.generators.LEDObject;
import backend.autocorrect.generators.PrefixObject;
import backend.autocorrect.generators.WhitespaceObject;
import backend.autocorrect.trie.TrieNode;
import backend.autocorrect.trie.TrieTree;

/**
 * Class representing all unigrams and all bigrams of a corpus
 * 
 * @author dgattey
 */
public class Corpus {
	
	private final TrieTree							unigrams;
	private final Map<String, Map<String, Integer>>	bigrams;
	
	/**
	 * Public constructor - creates data structures
	 */
	public Corpus() {
		unigrams = new TrieTree();
		bigrams = new HashMap<>();
	}
	
	/**
	 * Takes a and b and adds a bigram of the two to the bigrams map. Appears like this: word1 -> (word2 -> count).
	 * Example: the -> (dog -> 53).
	 * 
	 * @param a the first word of the bigram
	 * @param b the second word of the bigram
	 */
	public void addBigram(final String a, final String b) {
		if (a.isEmpty() || b.isEmpty()) {
			return;
		}
		
		Map<String, Integer> val = null;
		if (bigrams.containsKey(a)) {
			/*
			 * First word exists: either replace the map from the second word -> count with count + 1 or initialize that
			 * count to 1
			 */
			val = bigrams.get(a);
			val.put(b, val.containsKey(b) ? val.get(b) + 1 : 1);
		} else {
			/*
			 * First word doesn't exist, so simply create a new map from the second word -> 1
			 */
			val = new HashMap<>();
			val.put(b, 1);
		}
		
		// Regardless of prior existence, map first word -> val
		bigrams.put(a, val);
	}
	
	/**
	 * Adds all unigrams from a list
	 * 
	 * @param words all words to add
	 */
	public void addUnigrams(final List<String> words) {
		for (final String s : words) {
			addUnigram(s);
		}
	}
	
	/**
	 * Simply adds a unigram to the TrieTree saved
	 * 
	 * @param s the unigram to add
	 */
	public void addUnigram(final String s) {
		unigrams.add(s);
	}
	
	/**
	 * Convenience method to check existence of the given string as a word in the corpus
	 * 
	 * @param word a string to check
	 * @return if word was an actual word in the corpus, not just a prefix
	 */
	public boolean existsWord(final String word) {
		return unigrams.isWord(word);
	}
	
	/**
	 * Convenience method to return the unigram count of a given word
	 * 
	 * @param word a word to check in the unigram corpus
	 * @return the unigram count of word
	 */
	public int getUnigramCount(final String word) {
		return unigrams.getCount(word);
	}
	
	/**
	 * Returns the bigram count of a given pair of strings
	 * 
	 * @param prev the first word in the association
	 * @param curr the second word in the association
	 * @return the bigram count of prev, curr bigram if such a mapping exists, zero otherwise
	 */
	public int getBigramCount(final String prev, final String curr) {
		if (bigrams.containsKey(prev)) {
			final Integer val = bigrams.get(prev).get(curr);
			if (val != null) {
				return val;
			}
		}
		return 0; // Default
	}
	
	/**
	 * Returns prefix suggestions from the unigrams trie
	 * 
	 * @param p a prefix object to use when making suggestions
	 */
	public void makeSuggestions(final PrefixObject p) {
		final Collection<String> col = unigrams.getSubTree(p.word).getRoot().makeStrings();
		p.setWords(col);
	}
	
	/**
	 * Returns LED suggestions from the unigrams
	 * 
	 * @param led the led object to use for the number of columns and word
	 */
	public void makeSuggestions(final LEDObject led) {
		// Build first row
		final int[] currentRow = new int[led.cols + 1];
		for (int i = 0; i < currentRow.length; i++) {
			currentRow[i] = i;
		}
		for (final Entry<String, TrieNode> entry : unigrams.getRoot().getSuffixes().entrySet()) {
			final String key = entry.getKey();
			final TrieNode value = entry.getValue();
			value.getLEDWords(led, key, currentRow, key);
		}
	}
	
	/**
	 * Returns whitespace suggestions from the unigrams
	 * 
	 * @param w the whitespace object to use in calculating the words
	 */
	public void makeSuggestions(final WhitespaceObject w) {
		for (int i = 1; i < w.wordLength; i++) {
			final String firstWord = w.word.substring(0, i);
			final String secondWord = w.word.substring(i);
			
			if (unigrams.isWord(firstWord) && unigrams.isWord(secondWord)) {
				w.addWords(firstWord, secondWord);
			}
		}
	}
	
}
