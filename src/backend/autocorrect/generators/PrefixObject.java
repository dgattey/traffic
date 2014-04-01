package backend.autocorrect.generators;

import java.util.Collection;
import java.util.HashSet;

/**
 * A prefix object to use
 * 
 * @author dgattey
 */
public class PrefixObject {
	
	public final String			word;
	private Collection<String>	allWords;
	
	/**
	 * The default constructor
	 * 
	 * @param word a string target
	 */
	public PrefixObject(String word) {
		this.word = word;
		allWords = new HashSet<>();
	}
	
	/**
	 * Makes allWords equal to the given collection
	 * 
	 * @param allWords a collection of words
	 */
	public void setWords(Collection<String> allWords) {
		if (allWords != null) {
			this.allWords = allWords;
		}
	}
	
	/**
	 * Returns all words
	 * 
	 * @return a collection of strings representing words
	 */
	public Collection<String> getAllWords() {
		return allWords;
	}
}
