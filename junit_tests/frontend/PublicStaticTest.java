package frontend;

import static hub.Utilities.GUI;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import hub.Utilities;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import backend.autocorrect.ACParser;

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
		final ArgParser parser = Main.createFlagParser();
		
		parser.parse(new String[] { "--" + GUI, "file1", "file2", "file3" });
		assertTrue(parser.existsFlag(GUI));
		
		final String file = "dummyFileNameHere.txt";
		parser.parse(new String[] { file, file, file });
		assertFalse(parser.existsFlag("asdfasdfasd"));
		assertTrue(parser.getFileNames().contains(file));
		
		// Invalid flag
		boolean bad = false;
		try {
			parser.parse(new String[] { "--" + GUI, "--errror!" });
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
		
		// Invalid number of files, with flag
		bad = false;
		try {
			parser.parse(new String[] { file, file, "--" + GUI });
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
		assertTrue(Utilities.min(5, 12, 0) == 0);
		assertTrue(Utilities.min(23, -34, -34) == -34);
		assertTrue(Utilities.min(3, 3, 6) == 3);
		
		// Checks different conditions and one elements
		assertTrue(Utilities.getMinOfArray(new int[] { 4, 5, 1, 2, 8, 1 }) == 1);
		assertTrue(Utilities.getMinOfArray(new int[] { -1, -2, 0, 123 }) == -2);
		assertTrue(Utilities.getMinOfArray(new int[] { 84 }) == 84);
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
