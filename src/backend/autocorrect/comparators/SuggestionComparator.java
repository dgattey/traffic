package backend.autocorrect.comparators;

import java.util.Comparator;

import backend.autocorrect.SuggestionToken;

/**
 * Suggestion comparator abstract class for use by the dumb and smart comparators
 * 
 * @author dgattey
 */
public abstract class SuggestionComparator implements Comparator<SuggestionToken> {
	
	protected boolean	doBigram;
	protected String	target;
	
	/**
	 * Adds information to the comparator for use when actually comparing
	 * 
	 * @param doBigram whether bigram scores should be computed
	 * @param target the target string
	 */
	public void addInformation(final boolean doBigram, final String target) {
		this.doBigram = doBigram;
		this.target = target;
	}
	
	/**
	 * Compares the two suggestions based off exact match, then bigram if two words, unigram if one word or bigram ties,
	 * alphabetical if unigram ties
	 */
	@Override
	public int compare(final SuggestionToken o1, final SuggestionToken o2) {
		
		// Null checks
		if (o1 == null && o2 == null) {
			return 0;
		} else if (o1 == null) {
			return 1;
		} else if (o2 == null) {
			return -1;
		}
		
		final String w1 = o1.string;
		final String w2 = o2.string;
		
		// Checks for exactness
		if (w1.equals(w2)) {
			return 0;
		}
		if (w1.equals(target)) {
			return -1;
		}
		if (w2.equals(target)) {
			return 1;
		}
		
		// Bigram compare
		if (doBigram) {
			final int diff = o2.bigramFrequency - o1.bigramFrequency;
			if (diff != 0) {
				return diff;
			}
		}
		
		// Smart compare if existent
		final int diff = smartCompare(o1, o2);
		if (diff != 0) {
			return diff;
		}
		
		// Unigram compare
		final int uDiff = o2.unigramFrequency - o1.unigramFrequency;
		if (uDiff != 0) {
			return uDiff;
		}
		
		// Alphabet
		return w1.compareTo(w2);
	}
	
	/**
	 * Does smart compare for this comparator
	 * 
	 * @param o1 a suggestion token to compare
	 * @param o2 a suggestion token to compare
	 * @return an int representing the difference
	 */
	abstract int smartCompare(SuggestionToken o1, SuggestionToken o2);
}
