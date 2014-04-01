package autocorrect;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;

import org.junit.Test;

import backend.autocorrect.Corpus;
import backend.autocorrect.generators.LEDObject;
import backend.autocorrect.generators.PrefixObject;
import backend.autocorrect.generators.WhitespaceObject;

public class FullCorpusTest extends Corpus {
	
	/**
	 * Tests bigram adding
	 */
	@Test
	public void testAddBigram() {
		// Basics
		addBigram("a", "b");
		addBigram("a", "b");
		assertTrue(getBigramCount("a", "b") == 2);
		
		// Conflicting second strings
		addBigram("a", "c");
		assertTrue(getBigramCount("a", "c") == 1);
		
		// Different first strings
		addBigram("d", "e");
		addBigram("d", "e");
		addBigram("d", "e");
		assertTrue(getBigramCount("d", "e") == 3);
		
		// Nonexistent first and second
		assertTrue(getBigramCount("z", "e") == 0);
		assertTrue(getBigramCount("a", "g") == 0);
	}
	
	/**
	 * Tests adding unigrams
	 */
	@Test
	public void testAddUnigram() {
		makeCorp();
		
		// Basics
		assertTrue(getUnigramCount("a") == 1);
		assertTrue(getUnigramCount("b") == 2);
		
		// Longer strings with conflicts
		assertTrue(getUnigramCount("here") == 10);
		assertTrue(getUnigramCount("heroo") == 7);
	}
	
	/**
	 * Makes a reasonable corpus
	 */
	private void makeCorp() {
		// Basics
		addUnigram("a");
		addUnigram("b");
		addUnigram("b");
		
		// Longer strings
		for (int i = 0; i < 10; i++) {
			addUnigram("here");
		}
		
		// More strings
		for (int i = 0; i < 7; i++) {
			addUnigram("heroo");
		}
	}
	
	/**
	 * Tests existence of unigrams
	 */
	@Test
	public void checkExists() {
		makeCorp();
		
		// Simple
		assertTrue(existsWord("a"));
		assertTrue(existsWord("b"));
		
		// Longer + conflicting prefixes
		assertTrue(existsWord("here"));
		assertTrue(existsWord("heroo"));
		
		// Bad ones - prefixes and otherwise
		assertFalse(existsWord("he"));
		assertFalse(existsWord("h"));
		assertFalse(existsWord("different"));
		
	}
	
	/**
	 * Checks making prefix suggestions
	 */
	@Test
	public void testPrefixSugg() {
		makeCorp();
		final Collection<String> c = new HashSet<>();
		
		// Small prefix
		PrefixObject p = new PrefixObject("he");
		makeSuggestions(p);
		c.add("roo");
		c.add("re");
		assertTrue(p.getAllWords().equals(c));
		
		// No prefix, all words
		p = new PrefixObject("");
		makeSuggestions(p);
		c.clear();
		c.add("heroo");
		c.add("here");
		c.add("a");
		c.add("b");
		assertTrue(p.getAllWords().equals(c));
		
		// Non-existent prefix
		p = new PrefixObject("go");
		makeSuggestions(p);
		c.clear();
		assertTrue(p.getAllWords().equals(c));
	}
	
	/**
	 * Checks making LED suggestions
	 */
	@Test
	public void testLEDString() {
		makeCorp();
		addUnigram("ero");
		addUnigram("erro");
		final Collection<String> c = new HashSet<>();
		
		// LED of 2
		LEDObject p = new LEDObject("hero", 2);
		makeSuggestions(p);
		c.add("erro");
		c.add("here");
		c.add("heroo");
		c.add("ero");
		assertTrue(p.getWords().equals(c));
		
		// LED of 5000 (obviously all the words)
		p = new LEDObject("hero", 5000);
		makeSuggestions(p);
		c.add("a");
		c.add("b");
		assertTrue(p.getWords().equals(c));
		
		// LED of 1
		p = new LEDObject("hero", 1);
		makeSuggestions(p);
		c.remove("erro");
		c.remove("a");
		c.remove("b");
		assertTrue(p.getWords().equals(c));
		
	}
	
	/**
	 * Checks making whitespace suggestions
	 */
	@Test
	public void testWhitespaceSuggestions() {
		makeCorp();
		addUnigram("ah");
		addUnigram("ere");
		final Collection<Entry<String, String>> c = new HashSet<>();
		
		// Checking with multiple possibilities
		WhitespaceObject p = new WhitespaceObject("ahere");
		makeSuggestions(p);
		c.add(new SimpleEntry<>("a", "here"));
		c.add(new SimpleEntry<>("ah", "ere"));
		assertTrue(p.getWords().equals(c));
		
		// No possibilities
		p = new WhitespaceObject("blah");
		makeSuggestions(p);
		c.clear();
		assertTrue(p.getWords().equals(c));
	}
	
}
