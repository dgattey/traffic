package frontend.app;

import hub.HubController;
import hub.LatLongPoint;
import hub.MapNode;
import hub.MapWay;
import hub.MapsException;
import hub.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * REPL class for the command line interface - app runner
 * 
 * @author dgattey
 */
public class REPLApp extends App {
	
	/**
	 * Uses the App constructor and starts a REPL
	 * 
	 * @param ways the ways filename
	 * @param nodes the nodes filename
	 * @param index the index filename
	 * @param debug if we should be in debug mode
	 * @throws IOException if there was an error reading a file
	 * @throws MapsException if the hub had an error
	 */
	public REPLApp(final String ways, final String nodes, final String index, final boolean debug) throws IOException,
			MapsException {
		super(ways, nodes, index, debug);
		hub = new HubController(ways, nodes, index, this);
	}
	
	/**
	 * Starts the command line!
	 */
	@Override
	public void start() {
		final Scanner reader = new Scanner(System.in);
		System.out.println("Ready");
		while (reader.hasNextLine()) {
			final String line = reader.nextLine();
			try {
				// Tokenize, if null it meant empty line so quit
				final List<String> tokens = tokenize(line);
				if (tokens == null) {
					break;
				}
				
				// Try to parse doubles, but parse strings otherwise
				MapNode[] nodes = parseDoubles(tokens);
				if (nodes == null) {
					nodes = parseStrings(tokens);
				}
				
				System.out.println();
				if (debug) {
					for (final MapNode n : nodes) {
						if (n == null) {
							System.out.println("No intersection found");
							continue;
						}
					}
					System.out.println("Finding a route...");
				}
				
				// Nodes will now exist, so get route
				final List<MapWay> route = hub.getRoute(nodes[0], nodes[1]);
				printRoute(route);
				System.out.println();
			} catch (final IllegalArgumentException e) {
				// CTRL-D
				if (e.getMessage().isEmpty()) {
					break;
				}
				
				// Other error
				Utilities.printError("<REPL> " + e.getMessage());
			}
		}
		reader.close();
	}
	
	/**
	 * Prints a given route to sysout
	 * 
	 * @param route the list of maplines to print out as the route
	 */
	private static void printRoute(final List<MapWay> route) {
		if (route == null || route.isEmpty()) {
			System.out.println("Route couldn't be found between those points");
			return;
		}
		
		for (final MapWay l : route) {
			System.out.println(l);
		}
	}
	
	/**
	 * Takes input and turns it into tokens. Splits on whitespace and checks that the length is four and nothing is
	 * empty
	 * 
	 * @param input the user input
	 * @return a list of tokens representing user input
	 */
	private static List<String> tokenize(final String input) {
		// Parse into string
		final List<String> tokens = splitWords(input.trim());
		
		// Size errors
		if (tokens == null) {
			throw new IllegalArgumentException("");
		}
		
		// Make sure nothing is empty
		for (final String s : tokens) {
			if (s == null || s.isEmpty()) {
				throw new IllegalArgumentException("Empty line");
			}
		}
		if (tokens.isEmpty()) {
			return null;
		}
		
		if (tokens.size() != 4) {
			throw new IllegalArgumentException("Enter four items (either intersections or lat long points)");
		}
		
		return tokens;
	}
	
	/**
	 * Splits words based off spaces and quotes (spaces in quotes won't trigger a split)
	 * 
	 * @param s the given string
	 * @return a list of tokens
	 */
	private static List<String> splitWords(final String s) {
		if (s.isEmpty()) {
			return new ArrayList<>();
		}
		
		return Arrays.asList(s.split("[ ]+(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
	}
	
	/**
	 * Tries to create MapNodes from doubles that may have been entered
	 * 
	 * @param tokens the user's inputs in tokens
	 * @return an array of map nodes for the start and end
	 */
	private MapNode[] parseDoubles(final List<String> tokens) {
		final MapNode[] returnNodes = new MapNode[2];
		try {
			final List<Double> doubles = new ArrayList<>();
			for (final String token : tokens) {
				final Double dbl = Double.parseDouble(token);
				doubles.add(dbl);
			}
			final LatLongPoint p1 = new LatLongPoint(doubles.get(0), doubles.get(1));
			final LatLongPoint p2 = new LatLongPoint(doubles.get(2), doubles.get(3));
			
			// Get the nodes based off intersection
			returnNodes[0] = hub.getNearestIntersection(p1);
			returnNodes[1] = hub.getNearestIntersection(p2);
		} catch (final NumberFormatException e) {
			return null;
		}
		return returnNodes;
	}
	
	/**
	 * Try to parse the strings, knowing that the doubles failed
	 * 
	 * @param tokens the tokens from user input
	 * @return a map node array of start and end node
	 */
	private MapNode[] parseStrings(final List<String> tokens) {
		final MapNode[] returnNodes = new MapNode[2];
		final List<String> streets = new ArrayList<>();
		for (final String token : tokens) {
			// Make sure the string was surrounded with quotes
			if (token.startsWith("\"") && token.endsWith("\"")) {
				final String street = token.substring(1, token.length() - 1);
				streets.add(street);
			}
		}
		
		if (streets.size() != 4) {
			throw new IllegalArgumentException("Couldn't parse input (make sure to surround streets with quotes)");
		}
		// Get the nodes based off intersection
		returnNodes[0] = hub.getNearestIntersection(streets.get(0), streets.get(1));
		returnNodes[1] = hub.getNearestIntersection(streets.get(2), streets.get(3));
		return returnNodes;
	}
}
