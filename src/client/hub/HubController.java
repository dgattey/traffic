package client.hub;

import static data.ProtocolManager.AC_Q;
import static data.ProtocolManager.AC_R;
import static data.ProtocolManager.FOOTER;
import static data.ProtocolManager.MC_Q;
import static data.ProtocolManager.MC_R;
import static data.ProtocolManager.RP_Q;
import static data.ProtocolManager.RP_R;
import static data.ProtocolManager.RS_Q;
import static data.ProtocolManager.RS_R;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import main.Utils;
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
	
	private static final LatLongPoint	APP_LOAD_POINT	= new LatLongPoint(41.827196, -71.400369);
	
	private final ClientApp				app;
	private final String				hostName;
	private final int					serverPort;
	private boolean						connected;
	private final UUID					hubID;
	
	/**
	 * Main Constructor for HubController
	 * 
	 * @param hostName the host name to use as the server's hostname
	 * @param serverPort the server port to use when connecting to the server
	 * @param app the app that's running this hub controller
	 * @throws IOException if there was an error communicating with the server
	 */
	public HubController(final String hostName, final int serverPort, final ClientApp app) throws IOException {
		this.hostName = hostName;
		this.serverPort = serverPort;
		this.app = app;
		hubID = UUID.randomUUID();
		
		// Constant thread checking connection - will also update the label
		new Timer().scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				updateConnection();
			}
			
		}, new Date(), 2000);
	}
	
	/**
	 * Checks the connected status and updates the view appropriately
	 */
	protected void updateConnection() {
		final boolean previous = connected;
		connected = CommController.checkConnection(hostName, serverPort);
		app.getViewController().setConnectionLabel(connected);
		if (!previous && connected || app.getViewController().getChunks().isEmpty()) {
			app.getViewController().chunk();
		}
	}
	
	/**
	 * Checks whether this hub is connected
	 * 
	 * @return a boolean representing connection status to server
	 */
	public boolean isConnected() {
		return connected;
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
		if (isConnected()) {
			try {
				ret = CommController.getFromServer(new ServerCallable<List<ClientMapWay>>(hostName, serverPort) {
					
					@Override
					protected List<ClientMapWay> writeAndGetInfo(final CommController comm) throws IOException,
							ParseException {
						comm.writeWithNL(RP_Q + hubID.toString());
						comm.write(a);
						comm.write(b);
						comm.writeWithNL(FOOTER);
						
						final BufferedReader reader = comm.getReader();
						comm.checkForResponseHeader(RP_R);
						final List<ClientMapWay> ret = ProtocolManager.parseWayList(reader);
						comm.checkForResponseFooter();
						return ret;
					}
				});
			} catch (final IOException | ParseException e) {
				handleError(e);
			}
		}
		if (ret == null) {
			throw new IllegalArgumentException("<HubController> route was null");
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
		if (isConnected()) {
			try {
				ret = CommController.getFromServer(new ServerCallable<List<ClientMapWay>>(hostName, serverPort) {
					
					@Override
					protected List<ClientMapWay> writeAndGetInfo(final CommController comm) throws IOException,
							ParseException {
						comm.writeWithNL(RS_Q + hubID.toString());
						comm.writeWithNL(streetA1);
						comm.writeWithNL(streetA2);
						comm.writeWithNL(streetB1);
						comm.writeWithNL(streetB2);
						comm.writeWithNL(FOOTER);
						
						final BufferedReader reader = comm.getReader();
						comm.checkForResponseHeader(RS_R);
						final List<ClientMapWay> ret = ProtocolManager.parseWayList(reader);
						comm.checkForResponseFooter();
						return ret;
					}
				});
			} catch (final IOException | ParseException e) {
				handleError(e);
			}
		}
		if (ret == null) {
			throw new IllegalArgumentException("<HubController> route was null");
		}
		return ret;
		
	}
	
	@Override
	public List<String> getSuggestions(final String input) {
		List<String> ret = null;
		if (isConnected()) {
			try {
				ret = CommController.getFromServer(new ServerCallable<List<String>>(hostName, serverPort) {
					
					@Override
					protected List<String> writeAndGetInfo(final CommController comm) throws IOException,
							ParseException {
						comm.writeWithNL(AC_Q);
						comm.writeWithNL(input);
						comm.writeWithNL(FOOTER);
						
						final BufferedReader reader = comm.getReader();
						comm.checkForResponseHeader(AC_R);
						final List<String> ret = ProtocolManager.parseStreetList(reader);
						comm.checkForResponseFooter();
						return ret;
					}
					
				});
			} catch (final IOException | ParseException e) {
				handleError(e);
			}
		}
		return ret;
	}
	
	@Override
	public List<ClientMapWay> getChunk(final LatLongPoint min, final LatLongPoint max) {
		List<ClientMapWay> ret = null;
		if (isConnected()) {
			try {
				ret = CommController.getFromServer(new ServerCallable<List<ClientMapWay>>(hostName, serverPort) {
					
					@Override
					protected List<ClientMapWay> writeAndGetInfo(final CommController comm) throws IOException,
							ParseException {
						comm.writeWithNL(MC_Q);
						comm.write(min);
						comm.write(max);
						comm.writeWithNL(FOOTER);
						
						final BufferedReader reader = comm.getReader();
						comm.checkForResponseHeader(MC_R);
						final List<ClientMapWay> ret = ProtocolManager.parseWayList(reader);
						comm.checkForResponseFooter();
						return ret;
					}
					
				});
			} catch (final IOException | ParseException e) {
				handleError(e);
			}
		}
		return ret;
	}
	
	/**
	 * Prints an error out that happened in communicating with server
	 * 
	 * @param e an exception
	 */
	private static void handleError(final Exception e) {
		Utils.printError("<HubController> Communication failed: " + e.getMessage());
	}
}
