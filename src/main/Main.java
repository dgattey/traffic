package main;

import static main.Utils.USAGE_CLIENT;
import static main.Utils.USAGE_SERVER;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.core.ServerApp;
import client.ArgParser;
import client.ClientApp;
import data.MapException;

/**
 * Main class - runs the entire program
 * 
 * @author dgattey
 */
public class Main {
	
	private static App	a;
	
	/**
	 * Sets up a new parser for the command line args - creates a map from each possible command line argument to its
	 * corresponding class type for object instantiation, and tells the parser it expects three filenames
	 * 
	 * @param possibleArgs the number of possible arguments
	 * @return a new command line parser, ready to parse
	 */
	public static ArgParser createFlagParser(final int possibleArgs) {
		final Map<String, Class<?>> possibleFlags = new HashMap<>();
		return new ArgParser(possibleFlags, possibleArgs);
	}
	
	/**
	 * Main method - parses command line arguments and starts REPL or GUI
	 * 
	 * @param args command line args for the CLP to use to generate tokens
	 */
	public static void main(final String[] args) {
		
		// Find out if we're creating a client or a server
		if (args.length < 1) {
			Utils.printError("You messed up the executables (required args missing)");
			return;
		}
		Boolean isClient = null;
		ArgParser parser = new ArgParser(null);
		final String type = args[0];
		switch (type) {
		case ("server"):
			parser = createFlagParser(7);
			isClient = false;
			break;
		case ("client"):
			parser = createFlagParser(3);
			isClient = true;
			break;
		}
		if (isClient == null) {
			Utils.printError("You messed up the executables! (first arg not client or server");
			return;
		}
		
		// Check the arguments for validity and create objects as needed
		try {
			parser.parse(args);
		} catch (final IllegalArgumentException e) {
			Utils.printError(e.getMessage());
			System.out.println(isClient ? USAGE_CLIENT : USAGE_SERVER);
			return;
		}
		
		// Create an App, subtype dependent on the command args
		try {
			final List<String> appInfo = parser.getArguments();
			if (isClient) {
				final String hostName = appInfo.get(1);
				final int serverPort = Integer.parseInt(appInfo.get(2));
				a = new ClientApp(hostName, serverPort);
			} else {
				final String ways = appInfo.get(1);
				final String nodes = appInfo.get(2);
				final String index = appInfo.get(3);
				final String hostName = appInfo.get(4);
				final int trafficPort = Integer.parseInt(appInfo.get(5));
				final int serverPort = Integer.parseInt(appInfo.get(6));
				a = new ServerApp(ways, nodes, index, hostName, trafficPort, serverPort);
			}
		} catch (final IOException | MapException e) {
			Utils.printError(String.format("Couldn't start app: %s", e.getMessage()));
			return;
		} catch (final NumberFormatException e) {
			Utils.printError("Couldn't start app: port number wasn't a number");
			return;
		} catch (final IllegalArgumentException e) {
			Utils.printError(e.getMessage());
			return;
		}
		
		// Assuming all went well, start the app!
		a.start();
	}
}
