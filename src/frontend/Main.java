package frontend;

import static hub.Utilities.DEBUG;
import static hub.Utilities.GUI;
import static hub.Utilities.USAGE;
import hub.MapsException;
import hub.Utilities;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import frontend.app.App;
import frontend.app.GUIApp;
import frontend.app.REPLApp;

/**
 * Main class - runs the entire program
 * 
 * @author dgattey
 */
public class Main {
	
	private static App			a;
	private final static int	WAYS_FILE	= 0;
	private final static int	NODES_FILE	= 1;
	private final static int	INDEX_FILE	= 2;
	private static final int	NUM_FILES	= 3;
	
	/**
	 * Sets up a new parser for the command line args - creates a map from each possible command line argument to its
	 * corresponding class type for object instantiation, and tells the parser it expects three filenames
	 * 
	 * @return a new command line parser, ready to parse
	 */
	public static ArgParser createFlagParser() {
		final Map<String, Class<?>> possibleFlags = new HashMap<>();
		possibleFlags.put(GUI, null);
		possibleFlags.put(DEBUG, null);
		return new ArgParser(possibleFlags, NUM_FILES);
	}
	
	/**
	 * Main method - parses command line arguments and starts REPL or GUI
	 * 
	 * @param args command line args for the CLP to use to generate tokens
	 */
	public static void main(final String[] args) {
		final ArgParser parser = createFlagParser();
		
		// Check the arguments for validity and create objects as needed
		try {
			parser.parse(args);
		} catch (final IllegalArgumentException e) {
			Utilities.printError(e.getMessage());
			System.out.println(USAGE);
			return;
		}
		
		// Create an App, subtype dependent on the GUI command line flag
		try {
			final List<String> fileNames = parser.getFileNames();
			final String ways = fileNames.get(WAYS_FILE);
			final String nodes = fileNames.get(NODES_FILE);
			final String index = fileNames.get(INDEX_FILE);
			final boolean debug = parser.existsFlag(DEBUG);
			a = (parser.existsFlag(GUI)) ? new GUIApp(ways, nodes, index, debug) : new REPLApp(ways, nodes, index,
					debug);
		} catch (final IOException | MapsException e) {
			Utilities.printError(String.format("Couldn't start app: %s", e.getMessage()));
			return;
		}
		
		// Assuming all went well, start the app!
		a.start();
	}
}
