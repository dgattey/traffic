package client.hub;

import static data.ProtocolManager.FOOTER;
import static data.ProtocolManager.HEADER_QUERY;
import static data.ProtocolManager.TYPE_AUTOCORRECT;
import static data.ProtocolManager.TYPE_CHUNK;
import static data.ProtocolManager.TYPE_POINT;
import static data.ProtocolManager.TYPE_ROUTE_POINT;
import static data.ProtocolManager.TYPE_ROUTE_STREET;

import java.io.IOException;
import java.util.List;

import client.ClientApp;
import client.communicator.CommController;
import client.communicator.ServerCallable;
import data.ClientMapWay;
import data.LatLongPoint;
import data.ParseException;
import data.ProtocolManager;

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
	 * @throws IOException if there was an error communicating with the server
	 */
	public HubController(final String hostName, final int serverPort, final ClientApp guiApp) throws IOException {
		this.hostName = hostName;
		this.serverPort = serverPort;
		appLoadPoint = CommController.getFromServer(new ServerCallable<LatLongPoint>(hostName, serverPort) {
			
			@Override
			protected LatLongPoint writeAndGetInfo(final CommController comm) throws IOException {
				comm.write(HEADER_QUERY + ":" + TYPE_POINT + ":" + "2");
				comm.write("Thayer Street");
				comm.write("Waterman Street");
				comm.write(FOOTER);
				try {
					return ProtocolManager.parseLatLongPoint(comm.getReader());
				} catch (final ParseException e) {
					throw new IOException("<HubController> parsing appLoadPoint failed", e);
				}
			}
		});
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
		if (a == null || b == null) {
			throw new IllegalArgumentException("<HubController> null points to route find not allowed");
		}
		List<ClientMapWay> ret = null;
		if (isReady) {
			try {
				ret = CommController.getFromServer(new ServerCallable<List<ClientMapWay>>(hostName, serverPort) {
					
					@Override
					protected List<ClientMapWay> writeAndGetInfo(final CommController comm) throws IOException {
						comm.write(HEADER_QUERY + ":" + TYPE_ROUTE_POINT + ":" + "2");
						comm.write(a);
						comm.write(b);
						comm.write(FOOTER);
						return ProtocolManager.parseWayList(comm.getReader());
					}
				});
			} catch (final IOException e) {
				// TODO: ERROR HANDLING
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	@Override
	public List<ClientMapWay> getRoute(final String streetA1, final String streetA2, final String streetB1,
			final String streetB2) {
		
		// Error checking
		if (streetA1 == null || streetA1.isEmpty() || streetA2 == null || streetA2.isEmpty() || streetB1 == null
			|| streetB1.isEmpty() || streetB2 == null || streetB2.isEmpty()) {
			throw new IllegalArgumentException("<HubController> empty or null streets to route find not allowed");
		}
		
		// TODO: throw illegal exception for no intersections to display (from backend)
		List<ClientMapWay> ret = null;
		if (isReady) {
			try {
				ret = CommController.getFromServer(new ServerCallable<List<ClientMapWay>>(hostName, serverPort) {
					
					@Override
					protected List<ClientMapWay> writeAndGetInfo(final CommController comm) throws IOException {
						comm.write(HEADER_QUERY + ":" + TYPE_ROUTE_STREET + ":" + "4");
						comm.write(streetA1);
						comm.write(streetA2);
						comm.write(streetB1);
						comm.write(streetB2);
						comm.write(FOOTER);
						return ProtocolManager.parseWayList(comm.getReader());
					}
				});
			} catch (final IOException e) {
				// TODO: ERROR HANDLING
				e.printStackTrace();
			}
		}
		return ret;
		
	}
	
	@Override
	public List<String> getSuggestions(final String input) {
		List<String> ret = null;
		if (isReady) {
			try {
				ret = CommController.getFromServer(new ServerCallable<List<String>>(hostName, serverPort) {
					
					@Override
					protected List<String> writeAndGetInfo(final CommController comm) throws IOException {
						comm.write(HEADER_QUERY + ":" + TYPE_AUTOCORRECT + ":" + "1");
						comm.write(input);
						comm.write(FOOTER);
						return ProtocolManager.parseStreetList(comm.getReader());
					}
					
				});
			} catch (final IOException e) {
				// TODO: ERROR HANDLING
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	@Override
	public List<ClientMapWay> getChunk(final LatLongPoint min, final LatLongPoint max) {
		List<ClientMapWay> ret = null;
		if (isReady) {
			try {
				ret = CommController.getFromServer(new ServerCallable<List<ClientMapWay>>(hostName, serverPort) {
					
					@Override
					protected List<ClientMapWay> writeAndGetInfo(final CommController comm) throws IOException {
						comm.write(HEADER_QUERY + ":" + TYPE_CHUNK + ":" + "2");
						comm.write(min);
						comm.write(max);
						comm.write(FOOTER);
						return ProtocolManager.parseWayList(comm.getReader());
					}
					
				});
			} catch (final IOException e) {
				// TODO: ERROR HANDLING
				e.printStackTrace();
			}
		}
		return ret;
	}
}
