package server.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
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
	
	ACController		_autocorrect;
	KDTreeController	_kdtree;
	
	public ResponseController(final String ways, final String nodes, final String index, final TrafficController t)
			throws MapException, IOException {
		System.out.println("Server is loading...");
		IOController.setup(ways, nodes, index);
		_kdtree = new KDTreeController();
		_autocorrect = new ACController();
		setTrafficController(t);
		System.out.println("Loaded server!");
	}
	
	/**
	 * Attempts to set a traffic controller for the graph
	 * 
	 * @param t a Traffic Controller
	 */
	public void setTrafficController(final TrafficController t) {
		GraphController.setTrafficMap((t != null) ? t.getMap() : null);
	}
	
	/**
	 * Parses request and produces autocorrect response
	 * 
	 * @param r reader
	 * @param w writer
	 * @throws IOException failed to read or write
	 */
	public void autocorrectResponse(final BufferedReader r, final Writer w) throws IOException {
		if (!isReady()) {
			return;
		}
		try {
			// Get street name
			final String input = ProtocolManager.parseStreetName(r);
			
			// Find suggestions
			final List<String> sugg = _autocorrect.suggest(input);
			ProtocolManager.checkForResponseFooter(r.readLine());
			
			// Build Response
			final StringBuilder response = new StringBuilder(256);
			response.append(ProtocolManager.R_AC);
			response.append("\n");
			response.append(ProtocolManager.encodeSuggestions(sugg));
			response.append(ProtocolManager.FOOTER);
			response.append("\n");
			w.write(response.toString());
			w.flush();
		} catch (final ParseException e) {
			errorResponse(w, e);
		}
	}
	
	/**
	 * Parse request and produces routes from names
	 * 
	 * @param r reader
	 * @param w writer
	 * @throws IOException if reading or writing failed
	 */
	public void routeFromNamesResponse(final BufferedReader r, final Writer w) throws IOException {
		if (!isReady()) {
			return;
		}
		try {
			// Get four street names
			final String street1 = ProtocolManager.parseStreetName(r);
			final String street2 = ProtocolManager.parseStreetName(r);
			final String street3 = ProtocolManager.parseStreetName(r);
			final String street4 = ProtocolManager.parseStreetName(r);
			ProtocolManager.checkForResponseFooter(r.readLine());
			
			// Find their intersections
			final MapNode inter1 = IOController.findIntersection(street1, street2);
			final MapNode inter2 = IOController.findIntersection(street3, street4);
			
			// Find the shortest route
			final List<MapWay> path = GraphController.getShortestPathWays(inter1, inter2);
			// Build Response according to protocol
			final StringBuilder response = new StringBuilder(256);
			response.append(ProtocolManager.R_RS);
			response.append("\n");
			response.append(ProtocolManager.encodeMapWayList(path));
			response.append(ProtocolManager.FOOTER);
			response.append("\n");
			w.write(response.toString());
			w.flush();
		} catch (final MapException | ParseException e) {
			errorResponse(w, e);
		}
		
	}
	
	/**
	 * Parses request and produces routes from clicks
	 * 
	 * @param r reader
	 * @param w writer
	 * @throws IOException if reading or writing failed
	 */
	public void routeFromClicksResponse(final BufferedReader r, final Writer w) throws IOException {
		if (!isReady()) {
			return;
		}
		try {
			// Parse two points
			final LatLongPoint p1 = ProtocolManager.parseLatLongPoint(r);
			final LatLongPoint p2 = ProtocolManager.parseLatLongPoint(r);
			
			// Find Closest Neighbors
			final MapNode n1 = _kdtree.getNeighbor(p1);
			final MapNode n2 = _kdtree.getNeighbor(p2);
			
			// Find Route
			final List<MapWay> route = GraphController.getShortestPathWays(n1, n2);
			
			// Build Response
			final StringBuilder response = new StringBuilder(256);
			response.append(ProtocolManager.R_RP);
			response.append("\n");
			response.append(ProtocolManager.encodeMapWayList(route));
			response.append(ProtocolManager.FOOTER);
			response.append("\n");
			w.write(response.toString());
			w.flush();
		} catch (final ParseException | MapException e) {
			errorResponse(w, e);
		}
		
	}
	
	/**
	 * Parses request and produces a map chunk
	 * 
	 * @param r client reader
	 * @param w client writer
	 * @throws IOException if reading or writing failed
	 */
	public void mapDataResponse(final BufferedReader r, final Writer w) throws IOException {
		if (!isReady()) {
			return;
		}
		try {
			// Find min LatLongPoint of mapchunk to be generated
			final LatLongPoint p1 = ProtocolManager.parseLatLongPoint(r);
			final LatLongPoint p2 = ProtocolManager.parseLatLongPoint(r);
			
			// Find corresponding mapchunk
			final List<MapWay> chunk = IOController.getChunkOfWays(p1, p2);
			ProtocolManager.checkForResponseFooter(r.readLine());
			
			// Build Response
			final StringBuilder response = new StringBuilder(256);
			response.append(ProtocolManager.R_MC);
			response.append("\n");
			response.append(ProtocolManager.encodeMapWayList(chunk));
			response.append(ProtocolManager.FOOTER);
			response.append("\n");
			w.write(response.toString());
			w.flush();
		} catch (final ParseException | DataSetException e) {
			errorResponse(w, e);
		}
		
	}
	
	/**
	 * Responds with error message
	 * 
	 * @param w writer
	 * @param e exception that was thrown somewhere down the chain
	 * @throws IOException if writing failed
	 */
	public void errorResponse(final Writer w, final Exception e) throws IOException {
		if (!isReady()) {
			return;
		}
		// Build Response
		final StringBuilder response = new StringBuilder(256);
		response.append(ProtocolManager.R_ER);
		response.append(ProtocolManager.encodeError(e == null ? "No message" : e.getMessage()));
		response.append(ProtocolManager.FOOTER);
		response.append("\n");
		w.write(response.toString());
		w.flush();
	}
	
	/**
	 * Checks whether whole controller is setup
	 * 
	 * @return if IOController, KDTree, and Autocorrect is all setup
	 */
	public boolean isReady() {
		return IOController.isSetup() && _kdtree != null && _autocorrect != null;
	};
	
}
