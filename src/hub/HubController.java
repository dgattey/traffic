package hub;

import java.io.IOException;
import java.util.List;

import backend.autocorrect.ACController;
import backend.graph.GraphController;
import backend.io.DataSetException;
import backend.io.IOController;
import backend.kdtree.KDTreeController;
import frontend.app.GUIApp;
import frontend.app.REPLApp;

/**
 * @author dgattey
 */
public class HubController implements Controllable {
	
	ACController			autocorrect;
	KDTreeController		kdtree;
	boolean					isReady;
	private LatLongPoint	appLoadPoint;
	
	/**
	 * Opens up connections to the backend controllers and loads files into memory
	 * 
	 * @param ways the ways file
	 * @param nodes the nodes file
	 * @param index the index file
	 * @param app if the hub is for a GUI (and if so, load autocorrect)
	 * @throws IOException if there was a problem opening files
	 * @throws MapsException if the backend had a problem
	 */
	public HubController(final String ways, final String nodes, final String index, final GUIApp app)
			throws IOException, MapsException {
		isReady = false;
		new Thread() {
			
			@Override
			public void run() {
				try {
					int completed = 0;
					final int total = 3;
					IOController.setup(ways, nodes, index);
					app.getViewController().updateProgress(++completed, total);
					Thread.sleep(200);
					
					kdtree = new KDTreeController();
					app.getViewController().updateProgress(++completed, total);
					Thread.sleep(200);
					
					autocorrect = new ACController();
					app.getViewController().updateProgress(++completed, total);
					Thread.sleep(200);
					
					isReady = true;
					appLoadPoint = getNearestIntersection("Thayer Street", "Waterman Street").getPoint();
					app.getViewController().loadMap();
					System.out.println("Map loaded!");
				} catch (IOException | MapsException | InterruptedException e) {
					Utilities.printError(String.format("<Hub> Backend could not start (%s)", e.getMessage()));
					System.exit(1);
				}
			}
		}.start();
		
	}
	
	/**
	 * Opens up connections to the backend controllers and loads files into memory
	 * 
	 * @param ways the ways file
	 * @param nodes the nodes file
	 * @param index the index file
	 * @param app if the hub is for a REPL
	 * @throws IOException if there was a problem opening files
	 * @throws MapsException if the backend had a problem
	 */
	public HubController(final String ways, final String nodes, final String index, final REPLApp app)
			throws IOException, MapsException {
		isReady = false;
		try {
			IOController.setup(ways, nodes, index);
			kdtree = new KDTreeController();
			isReady = true;
			appLoadPoint = getNearestIntersection("Thayer Street", "Waterman Street").getPoint();
		} catch (IOException | MapsException e) {
			Utilities.printError(String.format("<Hub> Backend could not start (%s)", e.getMessage()));
			System.exit(1);
		}
	}
	
	@Override
	public MapNode getNearestIntersection(final LatLongPoint p) {
		if (isReady && p != null) {
			return kdtree.getNeighbor(p);
		}
		return null;
	}
	
	@Override
	public MapNode getNearestIntersection(final String street1, final String street2) {
		if (isReady) {
			try {
				final MapNode intersection = IOController.findIntersection(street1, street2);
				return intersection;
			} catch (MapsException | IOException e) {
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
			} catch (IOException | MapsException e) {}
		}
		return null;
	}
	
	@Override
	public List<String> getSuggestions(final String input) {
		if (isReady && input != null && !input.isEmpty() && autocorrect != null) {
			return autocorrect.suggest(input.trim());
		}
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
	
	public LatLongPoint getAppLoadPoint() {
		return appLoadPoint;
	}
	
}
