package backend.autocorrect.comparators;

import hub.Utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import backend.autocorrect.SuggestionToken;

/**
 * This class handles calculation of ranks and ordering
 * 
 * @author aiguha
 */
public class KeyboardComparator extends SuggestionComparator {
	
	private Map<Character, Set<Character>>	proxTable;
	
	/**
	 * Reads in Keyboard Mapping file to set up Key-Proximity based smart ranking
	 */
	public KeyboardComparator() {
		proxTable = new HashMap<>();
		try {
			final BufferedReader buf = new BufferedReader(new FileReader("lib/qwerty.txt"));
			String input;
			while ((input = buf.readLine()) != null) {
				final char[] line = input.replaceAll("[^a-zA-Z]", "").toLowerCase().trim().toCharArray();
				if (line.length < 1) {
					continue;
				}
				proxTable.put(line[0], new HashSet<Character>());
				
				for (int i = 1; i < line.length; i++) {
					proxTable.get(line[0]).add(line[i]);
				}
				
			}
			buf.close();
			
		} catch (final IOException e) {
			Utilities.printError("Autocorrect: QWERTY file wasn't found");
			proxTable = null;
		}
	}
	
	/**
	 * Gives a score for qwerty distance for suggestion (assumes qwerty table is populated)
	 * 
	 * @param suggestion a string that represents a possible suggestion
	 * @return a score representing how good the suggestion is
	 */
	public int getSmartScore(final String suggestion) {
		if (target == null || suggestion == null) {
			return 0;
		}
		final int oLen = target.length();
		final int sLen = suggestion.length();
		int score = 0;
		int i = 0;
		while (i < oLen && i < sLen) {
			final char org = target.charAt(i);
			final char rep = suggestion.charAt(i);
			if (org == rep) {
				score++;
			} else {
				final Set<Character> inner = proxTable.get(org);
				if (inner != null && inner.contains(rep)) {
					score++;
				}
			}
			i++;
		}
		return score;
	}
	
	@Override
	public int smartCompare(final SuggestionToken o1, final SuggestionToken o2) {
		return getSmartScore(o2.string) - getSmartScore(o1.string);
	}
}
