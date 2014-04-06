package server;

import java.io.IOException;
import java.util.List;

import server.autocorrect.ACController;
import server.graph.GraphController;
import server.io.DataSetException;
import server.io.IOController;
import server.kdtree.KDTreeController;
import data.LatLongPoint;
import data.MapException;
import data.MapNode;
import data.MapWay;
import data.ParseException;
import data.ProtocolManager;

public class ResponseController {
	
	private final ACController		_autocorrect;
	private final KDTreeController	_kdtree;
	
	public ResponseController(final String ways, final String nodes, final String index, final String hostName,
			final int trafficPort, final int serverPort) throws MapException, IOException {
		IOController.setup(ways, nodes, index);
		_kdtree = new KDTreeController();
		_autocorrect = new ACController();
	}
	
	public synchronized void autocorrectResponse(final ClientHandler c) throws IOException {
		try {
			// Get street name
			final String input = ProtocolManager.parseStreetName(c.getReader());
			
			// Find suggestions
			final List<String> sugg = _autocorrect.suggest(input);
			
			// Build Response
			final StringBuilder response = new StringBuilder(256);
			response.append(ProtocolManager.AC_R);
			response.append("\n");
			response.append(ProtocolManager.encodeSuggestions(sugg));
			response.append("\n");
			c.send(response.toString());
		} catch (final ParseException e) {
			errorResponse(c, e);
		}
	}
	
	/**
	 * Parse client request and perform response
	 * 
	 * @param c
	 * @throws IOException
	 */
	public synchronized static void routeFromNamesResponse(final ClientHandler c) throws IOException {
		try {
			// Get four street names
			final String street1 = ProtocolManager.parseStreetName(c.getReader());
			final String street2 = ProtocolManager.parseStreetName(c.getReader());
			final String street3 = ProtocolManager.parseStreetName(c.getReader());
			final String street4 = ProtocolManager.parseStreetName(c.getReader());
			
			// Find their intersections
			final MapNode inter1 = IOController.findIntersection(street1, street2);
			final MapNode inter2 = IOController.findIntersection(street3, street4);
			
			// Find the shortest route
			final List<MapWay> path = GraphController.getShortestPathWays(inter1, inter2);
			// Build Response according to protocol
			final StringBuilder response = new StringBuilder(256);
			response.append(ProtocolManager.RS_R);
			response.append("\n");
			response.append(ProtocolManager.encodeMapWayList(path));
			response.append(ProtocolManager.FOOTER);
			response.append("\n");
			c.send(response.toString());
		} catch (final MapException | ParseException e) {
			errorResponse(c, e);
		}
		
	}
	
	public synchronized void routeFromClicksResponse(final ClientHandler c) throws IOException {
		try {
			// Parse two points
			final LatLongPoint p1 = ProtocolManager.parseLatLongPoint(c.getReader());
			final LatLongPoint p2 = ProtocolManager.parseLatLongPoint(c.getReader());
			
			// Find Closest Neighbors
			final MapNode n1 = _kdtree.getNeighbor(p1);
			final MapNode n2 = _kdtree.getNeighbor(p2);
			
			// Find Route
			final List<MapWay> route = GraphController.getShortestPathWays(n1, n2);
			
			// Build Response
			final StringBuilder response = new StringBuilder(256);
			response.append(ProtocolManager.RP_R);
			response.append("\n");
			response.append(ProtocolManager.encodeMapWayList(route));
			response.append(ProtocolManager.FOOTER);
			response.append("\n");
			c.send(response.toString());
			
		} catch (final ParseException | MapException e) {
			errorResponse(c, e);
		}
		
	}
	
	public synchronized static void mapDataResponse(final ClientHandler c) throws IOException {
		try {
			// Find min LatLongPoint of mapchunk to be generated
			final LatLongPoint p1 = ProtocolManager.parseLatLongPoint(c.getReader());
			final LatLongPoint p2 = ProtocolManager.parseLatLongPoint(c.getReader());
			
			// Find corresponding mapchunk
			final List<MapWay> chunk = IOController.getChunkOfWays(p1, p2);
			
			// Build Response
			final StringBuilder response = new StringBuilder(256);
			response.append(ProtocolManager.MC_R);
			response.append("\n");
			response.append(ProtocolManager.encodeMapWayList(chunk));
			response.append(ProtocolManager.FOOTER);
			response.append("\n");
			c.send(response.toString());
			
		} catch (final ParseException | DataSetException e) {
			errorResponse(c, e);
		}
		
	}
	
	public synchronized static void errorResponse(final ClientHandler c, final Exception e) {
		// Build Response
		final StringBuilder response = new StringBuilder(256);
		response.append(ProtocolManager.ER_R);
		response.append(ProtocolManager.encodeError(e == null ? "No message" : e.getMessage()));
		response.append(ProtocolManager.FOOTER);
		response.append("\n");
		c.send(response.toString());
	};
	
}
