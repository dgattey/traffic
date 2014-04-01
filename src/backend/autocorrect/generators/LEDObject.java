package backend.autocorrect.generators;

import hub.Utilities;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents an LEDObject to pass around
 * 
 * @author dgattey
 */
public class LEDObject {
	
	private final Collection<String>	allWords;
	public final int					dist;
	public final String					word;
	public final int					cols;
	
	/**
	 * Sets final vars for use in generation
	 * 
	 * @param word a string representing a word to check out
	 * @param dist the max LED
	 */
	public LEDObject(final String word, final int dist) {
		allWords = new HashSet<>();
		this.word = word;
		this.dist = dist;
		cols = word.length();
	}
	
	/**
	 * Adds a word to the collection of words existent in the LED
	 * 
	 * @param word a new word to add
	 */
	public void addWord(final String word) {
		allWords.add(word);
	}
	
	/**
	 * Returns the collection of words found via searching for LED
	 * 
	 * @return the string collection of words that matched the LED
	 */
	public Collection<String> getWords() {
		return allWords;
	}
	
	/**
	 * Finds the edit distance between two strings
	 * 
	 * @param s1 one string
	 * @param s2 a second string
	 * @return an int representing the LED between the two
	 */
	public static int getEditDistance(final String s1, final String s2) {
		final int s1Length = s1.length();
		final int s2Length = s2.length();
		
		final int[][] values = new int[s1Length + 1][s2Length + 1];
		
		for (int i = 0; i <= s1Length; i++) {
			values[i][0] = i;
		}
		
		for (int j = 0; j <= s2Length; j++) {
			values[0][j] = j;
		}
		
		for (int i = 1; i <= s1Length; i++) {
			for (int j = 1; j <= s2Length; j++) {
				final int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
				
				// Takes the minimum of deletion, insertion, and substitution
				values[i][j] = Utilities.min(values[i - 1][j] + 1, values[i][j - 1] + 1, values[i - 1][j - 1] + cost);
			}
		}
		return (values[s1Length][s2Length]);
	}
	
}
