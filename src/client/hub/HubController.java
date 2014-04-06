package client.hub;

import static data.ProtocolManager.AC_Q;
import static data.ProtocolManager.FOOTER;
import static data.ProtocolManager.MC_Q;
import static data.ProtocolManager.RP_Q;
import static data.ProtocolManager.RS_Q;

import java.io.IOException;
import java.util.List;

import client.ClientApp;
import client.communicator.CommController;
import client.communicator.ServerCallable;
import data.ClientMapWay;
import data.LatLongPoint;
import data.ProtocolManager;

/**
 * @author dgattey
 */
public class HubController implements Controllable {
	
	private static final LatLongPoint	APP_LOAD_POINT	= new LatLongPoint(41.827196, -71.400369);
	private final boolean				isReady;
	private final String				hostName;
	private final int					serverPort;
	
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
		isReady = true;
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
		return APP_LOAD_POINT;
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
						comm.write(RP_Q + "2");
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
						comm.write(RS_Q + "4");
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
						comm.write(AC_Q + "1");
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
						comm.write(MC_Q + "2");
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
