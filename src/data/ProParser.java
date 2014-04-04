package data;

import java.io.BufferedReader;
import java.io.IOException;

import client.view.MapChunk;

/**
 * Protocol-based Parser<br>
 * Tools for parsing according to our unique protocol
 * 
 * @author aiguha
 */
public class ProParser {
	
	static final String	LLP_TAG		= "#llp:";
	static final String	LLP_DELIM	= " ";
	
	static final String	WAY_TAG		= "<way: ";
	
	static final String	CLOSE_TAG	= ">";
	
	public static void checkForOpeningTag(final String line, final String tag) throws ParseException {
		if (!line.startsWith(tag)) {
			throw new ParseException("Missing Opening Tag: " + tag);
		}
	}
	
	public static void checkForClosingTag(final String line) throws ParseException {
		if (!line.equals(CLOSE_TAG)) {
			throw new ParseException("Missing Closing Tag");
		}
	}
	
	public static LatLongPoint parseLatLongPoint(final BufferedReader r) throws ParseException, IOException {
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
	
	public static ClientMapWay parseClientMapWay(final BufferedReader r) throws IOException, ParseException {
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
	
	public static MapChunk parseMapChunk(final BufferedReader r) {
		throw new UnsupportedOperationException();
	}
	
}
