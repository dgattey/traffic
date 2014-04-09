package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import main.Utils;

/**
 * Protocol Manager <br>
 * Tools for parsing according to our unique protocol
 * 
 * @author aiguha & dgattey
 */
public class ProtocolManager {
	
	static final String			LLP_TAG				= "#llp:";
	static final String			LLP_DELIM			= " ";
	
	static final String			WAY_TAG				= "<way:";
	static final String			NODE_TAG			= "<node:";
	static final String			WAY_LIST_TAG		= "<list:way:";
	static final String			STRING_LIST_TAG		= "<list:string:";
	
	static final String			CLOSE_TAG			= ">";
	
	public static final String	DELIM				= ":";
	public static final String	HEADER_QUERY		= "@q";
	public static final String	HEADER_RESPONSE		= "@r";
	public static final String	FOOTER				= "@x";
	public static final String	TYPE_AUTOCORRECT	= "ac";
	public static final String	TYPE_ROUTE_POINT	= "rp";
	public static final String	TYPE_ROUTE_STREET	= "rs";
	public static final String	TYPE_INTERSECTION	= "in";
	public static final String	TYPE_CHUNK			= "ch";
	public static final String	TYPE_ROUTE			= "rt";
	public static final String	TYPE_POINT			= "pt";
	public static final String	TYPE_TRAFFIC		= "tr";
	public static final String	TYPE_ERROR			= "er";
	public static final String	TYPE_HEARTBEAT		= "hb";
	
	public static final String	Q_AC				= HEADER_QUERY + DELIM + TYPE_AUTOCORRECT + DELIM;
	public static final String	Q_RS				= HEADER_QUERY + DELIM + TYPE_ROUTE_STREET + DELIM;
	public static final String	Q_RP				= HEADER_QUERY + DELIM + TYPE_ROUTE_POINT + DELIM;
	public static final String	Q_MC				= HEADER_QUERY + DELIM + TYPE_CHUNK + DELIM;
	public static final String	Q_TR				= HEADER_QUERY + DELIM + TYPE_TRAFFIC + DELIM;
	public static final String	Q_HB				= HEADER_QUERY + DELIM + TYPE_HEARTBEAT + DELIM;
	
	public static final String	R_AC				= HEADER_RESPONSE + DELIM + TYPE_AUTOCORRECT + DELIM;
	public static final String	R_RS				= HEADER_RESPONSE + DELIM + TYPE_ROUTE_STREET + DELIM;
	public static final String	R_RP				= HEADER_RESPONSE + DELIM + TYPE_ROUTE_POINT + DELIM;
	public static final String	R_MC				= HEADER_RESPONSE + DELIM + TYPE_CHUNK + DELIM;
	public static final String	R_ER				= HEADER_RESPONSE + DELIM + TYPE_ERROR + DELIM;
	public static final String	R_TR				= HEADER_RESPONSE + DELIM + TYPE_TRAFFIC + DELIM;
	
	/**
	 * Takes a string and returns the number of lines that it says will be coming
	 * 
	 * @param line a string of text from a server (as a header)
	 * @return the number of lines found
	 * @throws ParseException if the size of the string was too short
	 */
	public static int getNumberOfLines(final String line) throws ParseException {
		final String[] split = line.split(DELIM);
		if (split.length < 2) {
			throw new ParseException("Line was too short to have a length: " + line);
		}
		try {
			return Integer.parseInt(split[split.length - 1]);
		} catch (final NumberFormatException e) {
			throw new ParseException("Problem with the number of lines: " + line);
		}
	}
	
	/**
	 * Checks the line for a matching opening tag
	 * 
	 * @param line a string that represents a line read from a server/client
	 * @param tag the tag that should be there
	 * @throws ParseException if the tag didn't exist
	 */
	public static void checkForOpeningTag(final String line, final String tag) throws ParseException {
		if (!line.startsWith(tag)) {
			throw new ParseException(String.format("Missing Opening Tag (found %s instead of \"%s\")", line, tag));
		}
	}
	
	/**
	 * Checks for existence of error response
	 * 
	 * @param line a line of server response
	 * @return if there was an error found
	 */
	public static boolean hasErrorTag(final String line) {
		return line.startsWith(R_ER);
	}
	
	/**
	 * Checks the line for a closing tag
	 * 
	 * @param line a string that represents a line read from a server/client
	 * @throws ParseException if the tag didn't exist
	 */
	public static void checkForClosingTag(final String line) throws ParseException {
		if (!line.equals(CLOSE_TAG)) {
			throw new ParseException(String.format("Missing Closing Tag (found %s instead of \">\")", line));
		}
	}
	
	/**
	 * Checks the line for a footer tag
	 * 
	 * @param line a string that represents a line read from a server/client
	 * @throws ParseException if the tag was wrong
	 */
	public static void checkForResponseFooter(final String line) throws ParseException {
		if (!line.equals(FOOTER)) {
			throw new ParseException(String.format("Missing Footer (found %s instead of \"%s\")", line, FOOTER));
		}
	}
	
	/**
	 * Parses a buffered reader representing a server's stream to a street name
	 * 
	 * @param r the reader
	 * @return a new string representing a street name
	 * @throws ParseException if the reader was null
	 * @throws IOException if we couldn't read from the reader
	 */
	public synchronized static String parseStreetName(final BufferedReader r) throws ParseException, IOException {
		if (r == null) {
			throw new ParseException("Null reader");
		}
		return r.readLine();
	}
	
	/**
	 * Parses a buffered reader representing a server's stream to a latLongPoint
	 * 
	 * @param r the reader
	 * @return a new lat long point
	 * @throws ParseException if the reader was null
	 * @throws IOException if we couldn't read from the reader
	 */
	public synchronized static LatLongPoint parseLatLongPoint(final BufferedReader r) throws ParseException,
			IOException {
		try {
			String line = r.readLine();
			if (line == null) {
				throw new ParseException("Could not finish parsing.");
			}
			checkForOpeningTag(line, LLP_TAG);
			line = line.substring(LLP_TAG.length());
			final String[] lineArray = line.split(LLP_DELIM);
			if (lineArray.length != 2) {
				throw new ParseException("Invalid LLP Data Format");
			}
			final LatLongPoint toReturn = new LatLongPoint(Double.parseDouble(lineArray[0]),
					Double.parseDouble(lineArray[1]));
			return toReturn;
		} catch (final NumberFormatException e) {
			throw new ParseException("Invalid Number Format");
		}
	}
	
	/**
	 * Parses a buffered reader representing a server's stream to a client map way
	 * 
	 * @param r the reader
	 * @return a new client map way
	 * @throws ParseException if the reader was null or otherwise problematic
	 * @throws IOException if we couldn't read from the reader
	 */
	public synchronized static ClientMapWay parseClientMapWay(final BufferedReader r) throws IOException,
			ParseException {
		final String line = r.readLine();
		checkForOpeningTag(line, WAY_TAG);
		final String id = r.readLine();
		final String name = r.readLine();
		final String startID = r.readLine();
		final LatLongPoint start = parseLatLongPoint(r);
		final String endID = r.readLine();
		final LatLongPoint end = parseLatLongPoint(r);
		checkForClosingTag(r.readLine());
		if (id == null || name == null || startID == null || endID == null || id.isEmpty()) {
			throw new ParseException("Null or empty fields. Cannot parse.");
		}
		return new ClientMapWay(id, name, new ClientMapNode(startID, start), new ClientMapNode(endID, end));
	}
	
	/**
	 * Parses a reader representing a server's stream to a list of client map way
	 * 
	 * @param reader the reader
	 * @return a list of client map ways to return
	 * @throws IOException if the reader failed
	 * @throws ParseException if something wasn't as expected
	 */
	public synchronized static List<ClientMapWay> parseWayList(final BufferedReader reader) throws IOException,
			ParseException {
		final String line = reader.readLine();
		checkForOpeningTag(line, WAY_LIST_TAG);
		final int numLines = getNumberOfLines(line);
		
		// For the number of ways that exist, read them!
		final List<ClientMapWay> ways = new ArrayList<>(numLines);
		for (int i = 0; i < numLines; i++) {
			final ClientMapWay w = parseClientMapWay(reader);
			ways.add(w);
		}
		checkForClosingTag(reader.readLine());
		return ways;
	}
	
	/**
	 * Parses a reader representing a server's stream to a list of strings
	 * 
	 * @param reader the reader
	 * @return a list of strings to return
	 * @throws ParseException if something was wrong
	 * @throws IOException if the reader failed
	 */
	public static List<String> parseStreetList(final BufferedReader reader) throws ParseException, IOException {
		String line = reader.readLine();
		checkForOpeningTag(line, STRING_LIST_TAG);
		final int numLines = getNumberOfLines(line);
		
		// For the number of strings that exist, read them!
		final List<String> strings = new ArrayList<>(numLines);
		for (int i = 0; i < numLines; i++) {
			line = reader.readLine();
			if (line == null) {
				throw new ParseException("Null line in parsing street list");
			}
			strings.add(line);
		}
		checkForClosingTag(reader.readLine());
		return strings;
	}
	
	/**
	 * Parses traffic data into an entry set
	 * 
	 * @param data a line that may represent traffic data
	 * @return a new Entry for placement into a map
	 */
	public static Entry<String, Double> parseTrafficData(final String data) {
		if (data == null) {
			throw new IllegalArgumentException("<TrafficController> (internal) argument should not be null");
		}
		final String[] arr = data.split("\\t");
		if (arr.length != 2) {
			return null; // Ignore bad data quietly
		}
		try {
			return new AbstractMap.SimpleEntry<>(arr[0], Double.parseDouble(arr[1]));
		} catch (final NumberFormatException e) {
			Utils.printError("<TrafficController> received bad input: " + data);
			return null;
		}
		
	}
	
	/**
	 * Encodes list of map way to a string to send
	 * 
	 * @param l a list that we want to encode
	 * @return a new string to send through a socket
	 */
	public synchronized static String encodeMapWayList(List<MapWay> l) {
		if (l == null) {
			l = new ArrayList<>();
		}
		final StringBuilder build = new StringBuilder(128);
		build.append(WAY_LIST_TAG);
		build.append(l.size());
		build.append("\n");
		for (final MapWay way : l) {
			build.append(way.encodeObject());
		}
		build.append(CLOSE_TAG);
		build.append("\n"); // Closes list
		return build.toString();
	}
	
	/**
	 * Encodes list of string to a string to send
	 * 
	 * @param suggestions a list that we want to encode
	 * @return a new string to send through a socket
	 */
	public synchronized static String encodeSuggestions(List<String> suggestions) {
		if (suggestions == null) {
			suggestions = new ArrayList<>();
		}
		final StringBuilder build = new StringBuilder(128);
		build.append(STRING_LIST_TAG);
		build.append(suggestions.size());
		build.append("\n");
		for (final String suggestion : suggestions) {
			build.append(suggestion);
			build.append("\n");
		}
		build.append(CLOSE_TAG); // Closes list
		build.append("\n");
		return build.toString();
	}
	
	/**
	 * Just encodes the string passed in as an error
	 * 
	 * @param error a string representing an error
	 * @return a new string representing a line to send to client as an error
	 */
	public synchronized static String encodeError(final String error) {
		return "ERROR:" + error + "\n";
	}
}
