package backend.autocorrect.comparators;

import backend.autocorrect.SuggestionToken;
import backend.autocorrect.generators.LEDObject;

/**
 * Smart comparator for tokens
 * 
 * @author dgattey
 */
public class SmartComparator extends SuggestionComparator {
	
	/**
	 * Calculates smart score, see below
	 * 
	 * @param o the token to calculate
	 * @return a double representing the score
	 */
	private double calcScore(final SuggestionToken o) {
		double score = 0;
		final double wordLength = o.string.length();
		
		// They're the same, so obviously need a huge value
		if (o.string.equals(target)) {
			return Double.MAX_VALUE;
		}
		
		// Put prefixes first, then whitespace, then edit distance(ish)
		if (o.string.startsWith(target)) {
			score = 1000.0 / wordLength;
		} else if (!o.secondString.isEmpty()) {
			score = 500.0;
		} else {
			final double led = LEDObject.getEditDistance(target, o.string);
			score = 5 * wordLength / led;
		}
		return score;
	}
	
	@Override
	int smartCompare(final SuggestionToken o1, final SuggestionToken o2) {
		final double s1 = calcScore(o1);
		final double s2 = calcScore(o2);
		
		return (int) Math.signum(s2 - s1);
	}
	
}
