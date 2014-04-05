package client.hub;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import client.ClientApp;
import data.ClientMapWay;
import data.LatLongPoint;
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
		if (a == null || b == null) {
			throw new IllegalArgumentException("null points in route finding");
		}
		List<ClientMapWay> ret = null;
		if (isReady) {
			final ExecutorService executor = Executors.newSingleThreadExecutor();
			final Callable<List<ClientMapWay>> callable = new Callable<List<ClientMapWay>>() {
				
				@Override
				public List<ClientMapWay> call() throws Exception {
					final ClientCommunicator comm = new ClientCommunicator(hostName, serverPort);
					List<ClientMapWay> route = null;
					try {
						comm.connect();
						// TODO: Fix protocol strings
						comm.write("<route:points");
						comm.write(a);
						comm.write(b);
						comm.write(">");
						route = ProtocolManager.parseRoute(comm.getReader());
						comm.disconnect();
					} catch (final IOException e) {
						// TODO: Fix up exception handling
						e.printStackTrace();
					}
					return route;
				}
			};
			
			// Make it work and get return value
			try {
				ret = executor.submit(callable).get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO: Fix up exception handling
				e.printStackTrace();
			}
			executor.shutdown();
		}
		return ret;
	}
	
	// TODO: Fix repeated code everywhere for Callable, etc
	
	@Override
	public List<ClientMapWay> getRoute(final String streetA1, final String streetA2, final String streetB1,
			final String streetB2) {
		
		// Error checking
		if (streetA1 == null || streetA1.isEmpty() || streetA2 == null || streetA2.isEmpty() || streetB1 == null
			|| streetB1.isEmpty() || streetB2 == null || streetB2.isEmpty()) {
			throw new IllegalArgumentException("empty or null street names");
		}
		
		// TODO: throw illegal exception for no intersections to display (from backend)
		List<ClientMapWay> ret = null;
		if (isReady) {
			final ExecutorService executor = Executors.newSingleThreadExecutor();
			final Callable<List<ClientMapWay>> callable = new Callable<List<ClientMapWay>>() {
				
				@Override
				public List<ClientMapWay> call() throws Exception {
					final ClientCommunicator comm = new ClientCommunicator(hostName, serverPort);
					List<ClientMapWay> route = null;
					try {
						comm.connect();
						// TODO: Fix protocol strings
						comm.write("<route:street");
						comm.write(streetA1);
						comm.write(streetA2);
						comm.write(streetB1);
						comm.write(streetB2);
						comm.write(">");
						route = ProtocolManager.parseRoute(comm.getReader());
						comm.disconnect();
					} catch (final IOException e) {
						// TODO: Fix up exception handling
						e.printStackTrace();
					}
					return route;
				}
			};
			
			// Make it work and get return value
			try {
				ret = executor.submit(callable).get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO: Fix up exception handling
				e.printStackTrace();
			}
			executor.shutdown();
		}
		return ret;
		
	}
	
	@Override
	public List<String> getSuggestions(final String input) {
		List<String> ret = null;
		if (isReady) {
			final ExecutorService executor = Executors.newSingleThreadExecutor();
			final Callable<List<String>> callable = new Callable<List<String>>() {
				
				@Override
				public List<String> call() throws Exception {
					final ClientCommunicator comm = new ClientCommunicator(hostName, serverPort);
					List<String> sugg = null;
					try {
						comm.connect();
						// TODO: Fix protocol strings
						comm.write("<ac");
						comm.write(input);
						comm.write(">");
						sugg = ProtocolManager.parseStreetList(comm.getReader());
						comm.disconnect();
					} catch (final IOException e) {
						// TODO: Fix up exception handling
						e.printStackTrace();
					}
					return sugg;
				}
			};
			
			// Make it work and get return value
			try {
				ret = executor.submit(callable).get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO: Fix up exception handling
				e.printStackTrace();
			}
			executor.shutdown();
		}
		return ret;
	}
	
	@Override
	public List<ClientMapWay> getChunk(final LatLongPoint min, final LatLongPoint max) {
		List<ClientMapWay> ret = null;
		if (isReady) {
			final ExecutorService executor = Executors.newSingleThreadExecutor();
			final Callable<List<ClientMapWay>> callable = new Callable<List<ClientMapWay>>() {
				
				@Override
				public List<ClientMapWay> call() throws Exception {
					final ClientCommunicator comm = new ClientCommunicator(hostName, serverPort);
					List<ClientMapWay> chunk = null;
					try {
						comm.connect();
						comm.write("<chunk");
						comm.write(min);
						comm.write(max);
						comm.write(">");
						chunk = ProtocolManager.parseMapChunk(comm.getReader());
						comm.disconnect();
					} catch (final IOException e) {
						// TODO: Fix up exception handling
						e.printStackTrace();
					}
					return chunk;
				}
			};
			
			// Make it work and get return value
			try {
				ret = executor.submit(callable).get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO: Fix up exception handling
				e.printStackTrace();
			}
			executor.shutdown();
		}
		return ret;
	}
}
