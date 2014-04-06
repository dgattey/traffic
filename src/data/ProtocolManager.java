package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Protocol Manager <br>
 * Tools for parsing according to our unique protocol
 * 
 * @author aiguha
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
	public static final String	TYPE_ERROR			= "er";
	
	public static final String	AC_Q				= HEADER_QUERY + DELIM + TYPE_AUTOCORRECT + DELIM;
	public static final String	RS_Q				= HEADER_QUERY + DELIM + TYPE_ROUTE_STREET + DELIM;
	public static final String	RP_Q				= HEADER_QUERY + DELIM + TYPE_ROUTE_POINT + DELIM;
	public static final String	MC_Q				= HEADER_QUERY + DELIM + TYPE_CHUNK + DELIM;
	
	public static final String	AC_R				= HEADER_RESPONSE + DELIM + TYPE_AUTOCORRECT + DELIM;
	public static final String	RS_R				= HEADER_RESPONSE + DELIM + TYPE_ROUTE_STREET + DELIM;
	public static final String	RP_R				= HEADER_RESPONSE + DELIM + TYPE_ROUTE_POINT + DELIM;
	public static final String	MC_R				= HEADER_RESPONSE + DELIM + TYPE_CHUNK + DELIM;
	public static final String	ER_R				= HEADER_RESPONSE + DELIM + TYPE_ERROR + DELIM;
	
	public synchronized static void checkForOpeningTag(final String line, final String tag) throws ParseException {
		if (!line.startsWith(tag)) {
			throw new ParseException("Missing Opening Tag: " + tag);
		}
	}
	
	public synchronized static void checkForClosingTag(final String line) throws ParseException {
		if (!line.equals(CLOSE_TAG)) {
			throw new ParseException("Missing Closing Tag");
		}
	}
	
	public synchronized static String parseStreetName(final BufferedReader r) throws ParseException, IOException {
		if (r == null) {
			throw new ParseException("Null reader");
		}
		return r.readLine();
	}
	
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
			throw new ParseException("Invalid LLP Data Format");
		}
	}
	
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
		checkForClosingTag(CLOSE_TAG);
		if (id == null || name == null || startID == null || endID == null || id.isEmpty()) {
			throw new ParseException("Null or empty fields. Cannot parse.");
		}
		return new ClientMapWay(id, name, new ClientMapNode(startID, start), new ClientMapNode(endID, end));
	}
	
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
		build.append(CLOSE_TAG); // Closes list
		return build.toString();
	}
	
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
		return build.toString();
	}
	
	public synchronized static String encodeError(final String error) {
		return "ERROR:" + error + "\n";
	}
	
	public static List<ClientMapWay> parseWayList(final BufferedReader reader) {
		throw new UnsupportedOperationException();
	}
	
	public static List<String> parseStreetList(final BufferedReader reader) {
		throw new UnsupportedOperationException();
	}
}
