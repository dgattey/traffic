package server.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.LatLongPoint;
import data.MapException;
import data.MapNode;
import data.MapWay;

/**
 * Controller for all file searching and parsing. Used by Autocorrect, KDtree and Graphs
 * 
 * @author aiguha & dgattey
 */
public class IOController {
	
	// private static boolean isSetup = false;
	
	// Required Headers
	static final List<String>			reqIndexHeaders		= Arrays.asList("name", "nodes");
	static final List<String>			reqNodesHeaders		= Arrays.asList("id", "latitude", "longitude", "ways");
	static final List<String>			reqWaysHeaders		= Arrays.asList("id", "name", "start", "end");
	
	// Delimiters
	private static final String			primaryDelimiter	= "\\t";
	private static final String			secondaryDelimiter	= ",";
	
	// Header Maps
	private static Map<String, Integer>	waysHeaderMap;
	private static Map<String, Integer>	indexHeaderMap;
	private static Map<String, Integer>	nodesHeaderMap;
	
	private static List<String>			allWayNames;
	private static Map<String, MapNode>	allMapNodes;
	
	private static Map<String, MapWay>	mapWayStore;
	
	// File names
	private static String				waysFile;
	private static String				nodesFile;
	private static String				indexFile;
	
	public static Map<String, Integer> getWaysHeaderMap() {
		return waysHeaderMap;
	}
	
	public static Map<String, Integer> getIndexHeaderMap() {
		return indexHeaderMap;
	}
	
	/**
	 * Sets up the full class
	 * 
	 * @param ways the ways file
	 * @param nodes the nodes file
	 * @param index the index file
	 * @throws MapException any internal map issue
	 * @throws IOException file or user io
	 */
	public synchronized static void setup(final String ways, final String nodes, final String index)
			throws MapException, IOException {
		waysFile = ways;
		nodesFile = nodes;
		indexFile = index;
		mapWayStore = new HashMap<>();
		try {
			waysHeaderMap = ParserTools.findHeaders(waysFile, reqWaysHeaders, primaryDelimiter);
			indexHeaderMap = ParserTools.findHeaders(indexFile, reqIndexHeaders, primaryDelimiter);
			nodesHeaderMap = ParserTools.findHeaders(nodesFile, reqNodesHeaders, primaryDelimiter);
		} catch (final DataSetException e) {
			throw new MapException("<IOController> " + e.getMessage());
		}
	}
	
	public synchronized static void tearDown() {
		waysFile = null;
		nodesFile = null;
		indexFile = null;
		allMapNodes = null;
		mapWayStore = null;
		waysHeaderMap = null;
		indexHeaderMap = null;
		nodesHeaderMap = null;
	}
	
	/**
	 * Given a line of data from a node file, creates a node and returns it after error checking
	 * 
	 * @param parsedLine a map of string to string representing the line in the file
	 * @return a node created from the given information
	 * @throws DataSetException if the parsing/converting fails
	 */
	static MapNode parseNode(final Map<String, String> parsedLine) throws DataSetException {
		final String id = parsedLine.get("id");
		final String latitude = parsedLine.get("latitude");
		final String longitude = parsedLine.get("longitude");
		final String ways = parsedLine.get("ways");
		try {
			final LatLongPoint p = new LatLongPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
			final MapNode node = MapNode.create(id, p, ParserTools.convertToList(ways, secondaryDelimiter));
			return node;
		} catch (final NumberFormatException e) {
			return null;
		}
		
	}
	
	/**
	 * Constructs a MapWay object using the key values in the parsedLine Map Parses node specific to the four headers:
	 * id, name, start, end
	 * 
	 * @param parsedLine the parsed line
	 * @return the MapWay object or null
	 * @throws DataSetException internal contract violation
	 */
	static MapWay parseWay(final Map<String, String> parsedLine) throws DataSetException {
		final String id = parsedLine.get("id");
		final String name = parsedLine.get("name");
		final String start = parsedLine.get("start");
		final String end = parsedLine.get("end");
		if (allMapNodes == null) {
			throw new DataSetException("Internal: Invalid order of operations.");
		}
		final MapWay way = MapWay.create(name, id, allMapNodes.get(start), allMapNodes.get(end));
		return way;
	}
	
	/**
	 * Parses a file into a hashmap of ids to mapnodes given the relevant information
	 * 
	 * @return a hashset of all nodes
	 * @throws IOException if the file can't be read
	 * @throws DataSetException if the parsing fails
	 */
	private synchronized static Map<String, MapNode> parseNodesFile() throws IOException, DataSetException {
		// If it's already been created, just return it
		if (allMapNodes != null) {
			throw new DataSetException("Violates internal contract");
		}
		
		// Otherwise, construct all nodes by reading from file
		allMapNodes = new HashMap<>();
		final BufferedReader b = new BufferedReader(new FileReader(nodesFile));
		
		// Skip Headers
		String line = b.readLine();
		
		// Adds each node as parsed to the mapNodeSet
		while ((line = b.readLine()) != null && !line.trim().isEmpty()) {
			final Map<String, String> parsedLine = ParserTools.parseLine(line, nodesHeaderMap, primaryDelimiter);
			final MapNode node = parseNode(parsedLine);
			if (node != null) {
				allMapNodes.put(node.getID(), node);
			}
		}
		b.close();
		return allMapNodes;
	}
	
	/**
	 * Parses a file into a list of strings representing the names of every way
	 * 
	 * @return a list of all way names
	 * @throws IOException if the file can't be read
	 * @throws DataSetException if the parsing fails
	 */
	private synchronized static List<String> parseWayNames() throws DataSetException, IOException {
		// If it's already been created, just return it
		if (allWayNames != null) {
			throw new DataSetException("Violates internal contract");
		}
		final List<String> reqHeaders = new ArrayList<>();
		reqHeaders.add("name");
		// Otherwise, construct ways by reading from file
		allWayNames = new ArrayList<>();
		final Map<String, Integer> headerMap = ParserTools.findHeaders(indexFile, reqHeaders, primaryDelimiter);
		final BufferedReader b = new BufferedReader(new FileReader(indexFile));
		
		// Skip Headers
		String line = b.readLine();
		
		// Adds each string corresponding to the name to the set
		while ((line = b.readLine()) != null && !line.trim().isEmpty()) {
			final Map<String, String> parsedLine = ParserTools.parseLine(line, headerMap, primaryDelimiter);
			allWayNames.add(parsedLine.get("name"));
		}
		
		b.close();
		return allWayNames;
	}
	
	/**
	 * Wrapper method that returns allNodes if it exists or calls parseNodesFile to create it
	 * 
	 * @return a map of node ids to mapnodes
	 * @throws IOException if there was a reading error
	 * @throws MapException if there was a DataSetException when parsing the nodes file
	 */
	public synchronized static Map<String, MapNode> getAllNodes() throws IOException, MapException {
		if (allMapNodes != null) {
			return allMapNodes;
		}
		try {
			return parseNodesFile();
		} catch (final DataSetException e) {
			throw new MapException("<IOController> " + e.getMessage());
		}
	}
	
	/**
	 * Wrapper method that returns allWayNames if it exists or calls parseWayNames to create it
	 * 
	 * @return a list of way names
	 * @throws IOException if there was a reading error
	 * @throws MapException if there was a DataSetException when parsing the ways file
	 */
	public synchronized static List<String> getAllWayNames() throws IOException, MapException {
		if (allWayNames != null) {
			return allWayNames;
		}
		try {
			return parseWayNames();
		} catch (final DataSetException e) {
			throw new MapException("<IOController> " + e.getMessage());
		}
	}
	
	/**
	 * Given the name of a way, it finds the id of the start node
	 * 
	 * @param wayName name of the street
	 * @return the id of the node that starts this street
	 * @throws IOException file io
	 * @throws DataSetException bad or missing data
	 */
	static String getStartOfWay(final String wayName) throws IOException, DataSetException {
		if (wayName == null) {
			throw new DataSetException("Internal: Invalid arguments to getStartMapNodeID.");
		}
		final String record = BinaryFileSearcher.simpleBinarySearch(indexFile, wayName, indexHeaderMap.get("name"),
				primaryDelimiter);
		if (record == null) {
			throw new DataSetException("Could not find way: " + wayName);
		}
		final Map<String, String> parsedRecord = ParserTools.parseLine(record, indexHeaderMap, primaryDelimiter);
		final List<String> nodes = ParserTools.convertToList(parsedRecord.get("nodes"), secondaryDelimiter);
		if (nodes.isEmpty()) {
			return null;
		}
		return nodes.get(0);
	}
	
	/**
	 * Constructs a node from a nodeID from the map created for the KDtree
	 * 
	 * @param nodeID the id of the node to be created
	 * @return the MapNode, or null
	 * @throws IOException file io
	 * @throws MapException interal map issues
	 */
	protected static MapNode getMapNode(final String nodeID) throws IOException, MapException {
		if (nodeID == null) {
			return null;
		}
		return IOController.getAllNodes().get(nodeID);
	}
	
	/**
	 * Constructs a MapWay using the wayID to seach the waysFile
	 * 
	 * @param wayID the id to be searched for
	 * @return the MapWay, o null
	 * @throws IOException file io
	 * @throws DataSetException bad or missing data
	 */
	static MapWay searchMapWay(final String wayID) throws IOException, DataSetException {
		if (wayID == null) {
			return null;
		}
		final String record = BinaryFileSearcher.simpleBinarySearch(waysFile, wayID, waysHeaderMap.get("id"),
				primaryDelimiter);
		if (record == null) {
			return null;
		}
		final Map<String, String> parsedRecord = ParserTools.parseLine(record, waysHeaderMap, primaryDelimiter);
		final MapWay newWay = IOController.parseWay(parsedRecord);
		return newWay;
	}
	
	/**
	 * Returns a MapWay object, either by getting it from the mapWayStore or searching for it using the searchMapWay
	 * 
	 * @param wayID the id to be found
	 * @return the MapWay or null
	 * @throws IOException file io
	 * @throws DataSetException bad or missing data
	 */
	public static MapWay getMapWay(final String wayID) throws IOException, DataSetException {
		if (wayID == null) {
			return null;
		}
		MapWay newWay = mapWayStore.get(wayID);
		if (newWay == null) {
			newWay = searchMapWay(wayID);
			if (newWay != null) {
				mapWayStore.put(newWay.getID(), newWay);
			}
		}
		return newWay;
	}
	
	/**
	 * @param street1 first street name
	 * @param street2 second street name
	 * @return the intersecting mapnode, if any
	 * @throws MapException thrown internally if dataset exception occurs
	 * @throws IOException thrown internally because of file io
	 */
	public static MapNode findIntersection(final String street1, final String street2) throws MapException, IOException {
		if (street1 == null || street2 == null) {
			return null;
		}
		try {
			final List<String> street1Matches = BinaryFileSearcher.findMatchingRecords(indexFile, street1,
					indexHeaderMap.get("name"), primaryDelimiter);
			final List<String> street2Matches = BinaryFileSearcher.findMatchingRecords(indexFile, street2,
					indexHeaderMap.get("name"), primaryDelimiter);
			for (final String s1 : street1Matches) {
				for (final String s2 : street2Matches) {
					final Map<String, String> p1 = ParserTools.parseLine(s1, indexHeaderMap, primaryDelimiter);
					final Map<String, String> p2 = ParserTools.parseLine(s2, indexHeaderMap, primaryDelimiter);
					final List<String> l1 = ParserTools.convertToList(p1.get("nodes"), secondaryDelimiter);
					final List<String> l2 = ParserTools.convertToList(p2.get("nodes"), secondaryDelimiter);
					final Set<String> set1 = new HashSet<>(l1);
					// Returns the first valid intersection
					if (set1.retainAll(l2) && !set1.isEmpty()) {
						return allMapNodes.get(set1.toArray()[0]);
					}
					
				}
			}
		} catch (final DataSetException e) {
			throw new MapException("<IOController> Internal Error in finding intersections.");
		}
		return null;
	}
	
	/**
	 * Returns first four digits of double as a string Assumes valid input
	 * 
	 * @param l a double
	 * @return the first four digits
	 */
	public static String firstFourDigits(final double l) {
		final double num = Math.round((Math.abs(l) * 100.0));
		return String.valueOf(num).substring(0, 4);
	}
	
	public static String constructWayID(final String lat, final String lon) {
		return String.format("/w/%s.%s", lat, lon);
	}
	
	/**
	 * Returns a block of ways. Does not reuse objects from the MapsDataProvider mapWayStore.
	 * 
	 * @param p1 the first point
	 * @param p2 the second point
	 * @return the list of mapways within the bounding box
	 * @throws DataSetException if data was invalid
	 * @throws IOException if file io failed
	 */
	public static List<MapWay> getChunkOfWays(final LatLongPoint p1, final LatLongPoint p2) throws DataSetException,
			IOException {
		final List<String> blockData = new ArrayList<>();
		final List<MapWay> chunk = new ArrayList<>();
		final String lat1 = firstFourDigits(p1.getLat());
		final String long1 = firstFourDigits(p1.getLong());
		final String lat2 = firstFourDigits(p2.getLat());
		final String long2 = firstFourDigits(p2.getLong());
		// Combinations of the above
		final String start1 = constructWayID(lat1, long1);
		final String end1 = constructWayID(lat1, long2);
		final String start2 = constructWayID(lat2, long1);
		final String end2 = constructWayID(lat2, long2);
		blockData.addAll(BinaryFileSearcher.getPage(waysFile, start1, end1, waysHeaderMap.get("id"), primaryDelimiter));
		blockData.addAll(BinaryFileSearcher.getPage(waysFile, start2, end2, waysHeaderMap.get("id"), primaryDelimiter));
		for (final String s : blockData) {
			final Map<String, String> parsedRecord = ParserTools.parseLine(s, waysHeaderMap, primaryDelimiter);
			final MapWay newWay = IOController.parseWay(parsedRecord);
			if (newWay != null) {
				chunk.add(newWay);
			}
		}
		if (start1.equals(end1) || start1.equals(end2) || start1.equals(start2) || start2.equals(end1)
			|| start2.equals(end2) || end1.equals(end2)) {
			System.out.println(String.format(
					"Received (%s, %s). \n\t chunking between: (%s, %s, %s, %s) \n\t blocksize: %d chunksize: %d ", p1,
					p2, start1, end1, start2, end2, blockData.size(), chunk.size()));
		}
		
		return chunk;
	}
	
	/**
	 * Returns if this is setup
	 * 
	 * @return if the whole IOController is setup
	 */
	public static boolean isSetup() {
		return !(waysFile == null || nodesFile == null || indexFile == null || allMapNodes == null
			|| mapWayStore == null || waysHeaderMap == null || indexHeaderMap == null || nodesHeaderMap == null);
	}
}
