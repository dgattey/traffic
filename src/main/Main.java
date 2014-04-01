package main;

import static main.Utils.USAGE_CLIENT;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.ArgParser;
import client.ClientApp;
import data.MapException;

/**
 * Main class - runs the entire program
 * 
 * @author dgattey
 */
public class Main {
	
	private static ClientApp	a;
	private static final int	POSSIBLE_ARGS	= 2;
	
	/**
	 * Sets up a new parser for the command line args - creates a map from each possible command line argument to its
	 * corresponding class type for object instantiation, and tells the parser it expects three filenames
	 * 
	 * @return a new command line parser, ready to parse
	 */
	public static ArgParser createFlagParser() {
		final Map<String, Class<?>> possibleFlags = new HashMap<>();
		return new ArgParser(possibleFlags, POSSIBLE_ARGS);
	}
	
	/**
	 * Main method - parses command line arguments and starts REPL or GUI
	 * 
	 * @param args command line args for the CLP to use to generate tokens
	 */
	public static void main(final String[] args) {
		// TODO: CHECK THE FIRST ARG FOR CLIENT OR SERVER, CHANGE USAGE TOO
		
		final ArgParser parser = createFlagParser();
		
		// Check the arguments for validity and create objects as needed
		try {
			parser.parse(args);
		} catch (final IllegalArgumentException e) {
			Utils.printError(e.getMessage());
			System.out.println(USAGE_CLIENT);
			return;
		}
		
		// Create an App, subtype dependent on the GUI command line flag
		try {
			final List<String> serverInfo = parser.getArguments();
			final String hostName = serverInfo.get(0);
			final int serverPort = Integer.parseInt(serverInfo.get(1));
			a = new ClientApp(hostName, serverPort);
		} catch (final IOException | MapException | NumberFormatException e) {
			Utils.printError(String.format("Couldn't start app: %s", e.getMessage()));
			return;
		}
		
		// Assuming all went well, start the app!
		a.start();
	}
}
