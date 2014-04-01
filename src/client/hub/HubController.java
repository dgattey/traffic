package client.hub;

import java.io.IOException;
import java.util.List;

import server.graph.GraphController;
import server.io.DataSetException;
import server.io.IOController;
import client.ClientApp;
import data.LatLongPoint;
import data.MapException;
import data.MapNode;
import data.MapWay;

/**
 * @author dgattey
 */
public class HubController implements Controllable {
	
	boolean					isReady;
	private LatLongPoint	appLoadPoint;
	
	private final String	hostName;
	private final int		serverPort;
	
	/*
	 * public HubController(final String ways, final String nodes, final String index, final GUIApp app) throws
	 * IOException, MapsException { isReady = false; new Thread() {
	 * @Override public void run() { try { int completed = 0; final int total = 3; IOController.setup(ways, nodes,
	 * index); app.getViewController().updateProgress(++completed, total); Thread.sleep(200); kdtree = new
	 * KDTreeController(); app.getViewController().updateProgress(++completed, total); Thread.sleep(200); autocorrect =
	 * new ACController(); app.getViewController().updateProgress(++completed, total); Thread.sleep(200); isReady =
	 * true; appLoadPoint = getNearestIntersection("Thayer Street", "Waterman Street").getPoint();
	 * app.getViewController().loadMap(); System.out.println("Map loaded!"); } catch (IOException | MapsException |
	 * InterruptedException e) { ClientUtils.printError(String.format("<Hub> Backend could not start (%s)",
	 * e.getMessage())); System.exit(1); } } }.start(); }
	 */
	
	/**
	 * Main Constructor for HubController
	 * 
	 * @param hostName the host name to use as the server's hostname
	 * @param serverPort the server port to use when connecting to the server
	 * @param guiApp the app that's running this hub controller
	 */
	public HubController(final String hostName, final int serverPort, final ClientApp guiApp) {
		this.hostName = hostName;
		this.serverPort = serverPort;
		// TODO: setup a test socket and make sure it's connectable - perhaps to set appLoadPoint?
		// TODO: Set isReady
	}
	
	@Override
	public MapNode getNearestIntersection(final LatLongPoint p) {
		if (isReady && p != null) {
			// return kdtree.getNeighbor(p);
		}
		return null;
	}
	
	@Override
	public MapNode getNearestIntersection(final String street1, final String street2) {
		if (isReady) {
			try {
				final MapNode intersection = IOController.findIntersection(street1, street2);
				return intersection;
			} catch (MapException | IOException e) {
				return null;
			}
		}
		return null;
	}
	
	@Override
	public List<MapWay> getRoute(final MapNode start, final MapNode end) {
		if (isReady) {
			try {
				return GraphController.getShortestPathWays(start, end);
			} catch (IOException | MapException e) {}
		}
		return null;
	}
	
	@Override
	public List<String> getSuggestions(final String input) {
		/*
		 * if (isReady && input != null && !input.isEmpty() && autocorrect != null) { return
		 * autocorrect.suggest(input.trim()); }
		 */
		return null;
	}
	
	@Override
	public List<MapWay> pageInMapData(final LatLongPoint p1, final LatLongPoint p2) {
		if (isReady) {
			try {
				final List<MapWay> chunk = IOController.getChunkOfWays(p1, p2);
				return chunk;
			} catch (DataSetException | IOException e) {}
		}
		return null;
	}
	
	/**
	 * Public getter for the ready attribute
	 * 
	 * @return if the app is ready
	 */
	public boolean isReady() {
		return isReady;
	}
	
	/**
	 * Gets the public load point of the app (used as center)
	 * 
	 * @return the center point of the app
	 */
	public LatLongPoint getAppLoadPoint() {
		return appLoadPoint;
	}
	
}
