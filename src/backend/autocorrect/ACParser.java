package backend.autocorrect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses input from the user on the command line
 * 
 * @author dgattey
 */
public class ACParser {
	
	/**
	 * Parses a string input. Removes all spaces, changes to lower case, and splits on non-lowercase characters.
	 * 
	 * @param s the string to parse
	 * @return a string array representing tokens or words
	 */
	public static List<String> parse(String s) {
		s = s.trim();
		return splitWordsBySpaceOrQuotes(s);
	}
	
	/**
	 * Checks for only trailing whitespace. If there is any leading whitespace at all, it returns false.
	 * 
	 * @param s a string to check
	 * @return if s had only trailing whitespace
	 */
	static boolean hasOnlyTrailingSpaces(final String s) {
		final int fullLength = s.length();
		final int noLeadingLength = trimLeadingSpaces(s).length();
		
		// There were leading spaces
		if (noLeadingLength != fullLength) {
			return false;
		}
		return fullLength != s.trim().length();
	}
	
	/**
	 * Removes leading whitespace
	 * 
	 * @param s a string to use
	 * @return a new string without leading whitespace
	 */
	private static String trimLeadingSpaces(final String s) {
		return s.replaceAll("^\\s+", "");
	}
	
	/**
	 * Splits a string on everything but lowercase characters. Converts String[] to ArrayList for easier manipulation
	 * later.
	 * 
	 * @param s a string to split
	 * @return a string array representing the tokens s formed
	 */
	static List<String> splitWordsByLowercase(final String s) {
		final String[] arr = s.split("[^a-z]+");
		return new ArrayList<>(Arrays.asList(arr));
	}
	
	/**
	 * Splits words based off spaces and quotes (no spaces in quotes will trigger a split)
	 * 
	 * @param s the given string
	 * @return a list of tokens
	 */
	private static List<String> splitWordsBySpaceOrQuotes(final String s) {
		final List<String> matches = new ArrayList<>();
		if (s.isEmpty()) {
			return matches;
		}
		final Matcher regexMatcher = Pattern.compile("[^\\s\"]+|\"([^\"]*)\"").matcher(s);
		while (regexMatcher.find()) {
			if (regexMatcher.group(1) != null) {
				// Quotes
				matches.add(regexMatcher.group(1));
			} else {
				// Other word
				matches.add(regexMatcher.group());
			}
		}
		return matches;
	}
	
}
