package server.autocorrect.comparators;

import server.autocorrect.SuggestionToken;

/**
 * @author dgattey
 */
public class DumbComparator extends SuggestionComparator {
	
	@Override
	int smartCompare(final SuggestionToken o1, final SuggestionToken o2) {
		return 0;
	}
	
}
