package frontend;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import main.Main;
import main.Utils;

import org.junit.Test;

import server.autocorrect.ACParser;
import client.ArgParser;

/**
 * Tests public static methods related to autocorrect
 * 
 * @author dgattey
 */
public class PublicStaticTest {
	
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
	 * Checks the utilities class
	 */
	@Test
	public void checkUtilities() {
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
