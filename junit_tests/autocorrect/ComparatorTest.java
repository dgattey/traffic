package autocorrect;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import backend.autocorrect.SuggestionToken;
import backend.autocorrect.comparators.DumbComparator;
import backend.autocorrect.comparators.KeyboardComparator;
import backend.autocorrect.comparators.SmartComparator;

public class ComparatorTest {
	
	/**
	 * Tests dumb comparator
	 */
	@Test
	public void checkDumbComparator() {
		final DumbComparator c = new DumbComparator();
		c.addInformation(true, "targetWord");
		
		// Equal exactly
		assertTrue(c.compare(new SuggestionToken("word", 1, 2), new SuggestionToken("word", 1, 2)) == 0);
		
		// Different unigrams and bigrams (shouldn't matter)
		assertTrue(c.compare(new SuggestionToken("word", 1231, 2), new SuggestionToken("word", 1, 2)) == 0);
		assertTrue(c.compare(new SuggestionToken("word", 1, 2), new SuggestionToken("word", 1, 20)) == 0);
		
		// Alphabet
		assertTrue(c.compare(new SuggestionToken("a", 1, 2), new SuggestionToken("b", 1, 2)) == -1);
		assertTrue(c.compare(new SuggestionToken("b", 1, 2), new SuggestionToken("a", 1, 2)) == 1);
		
		// Target found
		assertTrue(c.compare(new SuggestionToken("targetWord", 1, 2), new SuggestionToken("other", 1, 2)) == -1);
	}
	
	/**
	 * Tests smart comparator
	 */
	@Test
	public void checkSmartComparator() {
		final SmartComparator c = new SmartComparator();
		c.addInformation(true, "targetWord");
		
		// Equal exactly
		assertTrue(c.compare(new SuggestionToken("word", 1, 2), new SuggestionToken("word", 1, 2)) == 0);
		
		// Different unigrams and bigrams (shouldn't matter)
		assertTrue(c.compare(new SuggestionToken("word", 1231, 2), new SuggestionToken("word", 1, 2)) == 0);
		assertTrue(c.compare(new SuggestionToken("word", 1, 2), new SuggestionToken("word", 1, 20)) == 0);
		
		// Alphabet
		assertTrue(c.compare(new SuggestionToken("a", 1, 2), new SuggestionToken("b", 1, 2)) == -1);
		assertTrue(c.compare(new SuggestionToken("b", 1, 2), new SuggestionToken("a", 1, 2)) == 1);
		
		// Target found
		assertTrue(c.compare(new SuggestionToken("targetWord", 1, 2), new SuggestionToken("other", 1, 2)) == -1);
		
		// Different from above - uses LED rank
		assertTrue(c.compare(new SuggestionToken("argetWor", 1, 2), new SuggestionToken("a", 1, 2)) == -1);
	}
	
	/**
	 * Tests keyboard comparator
	 */
	@Test
	public void checkKeyboardComparator() {
		final KeyboardComparator c = new KeyboardComparator();
		c.addInformation(true, "targetword");
		
		// Equal exactly
		assertTrue(c.compare(new SuggestionToken("word", 1, 2), new SuggestionToken("word", 1, 2)) == 0);
		
		// Different unigrams and bigrams (shouldn't matter)
		assertTrue(c.compare(new SuggestionToken("word", 1231, 2), new SuggestionToken("word", 1, 2)) == 0);
		assertTrue(c.compare(new SuggestionToken("word", 1, 2), new SuggestionToken("word", 1, 20)) == 0);
		
		// Alphabet
		assertTrue(c.compare(new SuggestionToken("a", 1, 2), new SuggestionToken("b", 1, 2)) == -1);
		assertTrue(c.compare(new SuggestionToken("b", 1, 2), new SuggestionToken("a", 1, 2)) == 1);
		
		// Target found
		assertTrue(c.compare(new SuggestionToken("targetword", 1, 2), new SuggestionToken("other", 1, 2)) < 0);
		
		// Smarter because keyboard compare
		assertTrue(c.compare(new SuggestionToken("targetwird", 1, 2), new SuggestionToken("targetwyrd", 1, 2)) < 0);
		assertTrue(c.compare(new SuggestionToken("targetwyrd", 1, 2), new SuggestionToken("targetwird", 1, 2)) > 0);
		assertTrue(c.compare(new SuggestionToken("targetwkrd", 1, 2), new SuggestionToken("targetwkrd", 1, 2)) == 0);
		assertTrue(c.compare(new SuggestionToken("targetwkrd", 1, 2), new SuggestionToken("targetwird", 1, 2)) > 0);
		assertTrue(c.compare(new SuggestionToken("abasdf", 1, 2), new SuggestionToken("iwe89", 1, 2)) < 0);
		
	}
}
