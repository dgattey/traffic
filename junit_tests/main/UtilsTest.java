package main;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import server.autocorrect.ACParser;
import client.ArgParser;

/**
 * Tests public static methods related to autocorrect
 * 
 * @author dgattey
 */
public class UtilsTest {
	
	/**
	 * Test that checks the command line parser - checks each flag and an object if exists
	 */
	@Test
	public void checkCommandLineParsing() {
		final ArgParser parser = Main.createFlagParser(3);
		
		final String file = "dummyFileNameHere.txt";
		parser.parse(new String[] { file, file, file });
		assertFalse(parser.existsFlag("asdfasdfasd"));
		assertTrue(parser.getArguments().contains(file));
		
		// Invalid flag
		boolean bad = false;
		try {
			parser.parse(new String[] { "--errror!" });
		} catch (final IllegalArgumentException e) {
			bad = true;
		}
		assertTrue(bad);
		
		// Invalid number of files, without flag
		bad = false;
		try {
			parser.parse(new String[] { file, file });
		} catch (final IllegalArgumentException e) {
			bad = true;
		}
		assertTrue(bad);
	}
	
	/**
	 * Checks the min methods of the utilities class
	 */
	@Test
	public void checkMinUtilities() {
		// Checks signs and different conditions
		assertTrue(Utils.min(5, 12, 0) == 0);
		assertTrue(Utils.min(23, -34, -34) == -34);
		assertTrue(Utils.min(3, 3, 6) == 3);
		
		// Checks different conditions and one elements
		assertTrue(Utils.getMinOfArray(new int[] { 4, 5, 1, 2, 8, 1 }) == 1);
		assertTrue(Utils.getMinOfArray(new int[] { -1, -2, 0, 123 }) == -2);
		assertTrue(Utils.getMinOfArray(new int[] { 84 }) == 84);
	}
	
	/**
	 * Checks the max methods of the utilities class
	 */
	@Test
	public void checkMaxUtilities() {
		// Checks signs and different conditions
		assertTrue(Utils.max(5, 12, 0) == 12);
		assertTrue(Utils.max(23, -34, 23) == 23);
		assertTrue(Utils.max(3, 3, 6) == 6);
		assertTrue(Utils.max(-12, -12, -2) == -2);
	}
	
	/*
	 * Checks null tests
	 */
	@Test
	public void checkNulls() {
		assertFalse(Utils.anyNullOrEmpty("hello"));
		assertTrue(Utils.anyNullOrEmpty(""));
		assertTrue(Utils.anyNullOrEmpty((String) null));
		assertTrue(Utils.anyNullOrEmpty((String) null, "asdfa", "NOT"));
		assertFalse(Utils.anyNullOrEmpty("asdfasda", "asdfasda", "NOT"));
		
		assertFalse(Utils.isNullOrEmpty("hello"));
		assertTrue(Utils.isNullOrEmpty(""));
		assertTrue(Utils.isNullOrEmpty((String) null));
	}
	
	/**
	 * Checks parsing of a line of text into a list of strings
	 */
	@Test
	public void simpleLineParser() {
		final List<String> list = new ArrayList<>();
		list.add("simple");
		assertTrue(ACParser.parse("simple").equals(list));
		
		list.clear();
		list.add("spaces");
		list.add("exist");
		list.add("here");
		assertTrue(ACParser.parse("spaces exist here").equals(list));
		
	}
	
}
