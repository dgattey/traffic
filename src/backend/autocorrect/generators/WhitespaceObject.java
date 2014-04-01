package backend.autocorrect.generators;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 * A whitespace object
 * 
 * @author dgattey
 */
public class WhitespaceObject {
	
	private final Collection<Entry<String, String>>	allWords;
	public final String								word;
	public final int								wordLength;
	
	/**
	 * Default constructor, saves word length and word
	 * 
	 * @param word a string target
	 */
	public WhitespaceObject(String word) {
		this.word = word;
		wordLength = word.length();
		allWords = new HashSet<>();
	}
	
	/**
	 * Adds a pair of words to the collection of words existent in this object
	 * 
	 * @param w a new word to add
	 * @param w2 the second word to add
	 */
	public void addWords(String w, String w2) {
		allWords.add(new AbstractMap.SimpleImmutableEntry<>(w, w2));
	}
	
	/**
	 * Returns the collection of words found via searching
	 * 
	 * @return the string collection of words that matched
	 */
	public Collection<Entry<String, String>> getWords() {
		return allWords;
	}
	
}
