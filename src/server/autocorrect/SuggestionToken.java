package server.autocorrect;

import java.util.Objects;

/**
 * Class to represent a search token for the Generator - has two strings, unigram frequency, and bigram frequency. Fully
 * immutable.
 * 
 * @author dgattey
 */
public class SuggestionToken {
	
	public final String	string;
	public final String	secondString;
	public final int	unigramFrequency;
	public final int	bigramFrequency;
	
	/**
	 * Constructs a token with the given items
	 * 
	 * @param string a result matching a word
	 * @param secondString a second result to use (whitespace matching)
	 * @param unigramFrequency count for unigram appearing in the corpus
	 * @param bigramFrequency count for bigram appearance
	 */
	public SuggestionToken(final String string, final String secondString, final int unigramFrequency,
			final int bigramFrequency) {
		this.string = string;
		this.secondString = secondString;
		this.unigramFrequency = unigramFrequency;
		this.bigramFrequency = bigramFrequency;
	}
	
	/**
	 * Alternate constructor when there is no second string
	 * 
	 * @param string a result matching a word
	 * @param unigramFrequency count for unigram appearing in the corpus
	 * @param bigramFrequency count for bigram appearance
	 */
	public SuggestionToken(final String string, final int unigramFrequency, final int bigramFrequency) {
		this(string, "", unigramFrequency, bigramFrequency);
	}
	
	/**
	 * Prints to string
	 */
	@Override
	public String toString() {
		return "<" + string + ", " + secondString + "; " + unigramFrequency + "|" + bigramFrequency + ">";
	}
	
	/**
	 * Returns human-readable representation of the token. Strings concatted together with a space between.
	 * 
	 * @return a string representing the suggestion
	 */
	public String fullString() {
		if (secondString.isEmpty()) {
			return string;
		}
		return (string + " " + secondString);
	}
	
	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof SuggestionToken)) {
			return false;
		}
		final SuggestionToken t = (SuggestionToken) o;
		return string.equals(t.string) && secondString.equals(t.secondString);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(string, secondString);
	}
	
}
