package client.hub;

import java.util.List;

import client.ClientApp;
import client.view.MapChunk;
import data.ClientMapWay;
import data.LatLongPoint;

/**
 * @author dgattey
 */
public class HubController implements Controllable {
	
	boolean					isReady;
	private LatLongPoint	appLoadPoint;
	
	private final String	hostName;
	private final int		serverPort;
	
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
	
	@Override
	public List<ClientMapWay> getRoute(final LatLongPoint a, final LatLongPoint b) {
		// TODO: connect to server, get route for 2 points
		// TODO: throw illegal argument for null/bad points
		return null;
	}
	
	@Override
	public List<ClientMapWay> getRoute(final String streetA1, final String streetA2, final String streetB1,
			final String streetB2) {
		// TODO: connect to server, get route for 4 streets
		// TODO: throw illegal argument for empty/null strings
		// TODO: throw illegal exception for no intersections to display (from backend)
		return null;
	}
	
	@Override
	public List<String> getSuggestions(final String input) {
		// TODO: connect to server, get suggestions
		return null;
	}
	
	@Override
	public MapChunk getChunk(final LatLongPoint min, final LatLongPoint max) {
		// TODO: connect to server, get chunk data
		return null;
	}
	
}
