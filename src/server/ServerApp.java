package server;

import main.App;

/**
 * Starts the server running
 * 
 * @author dgattey
 */
public class ServerApp extends App {
	
	/**
	 * @param ways
	 * @param nodes
	 * @param index
	 * @param hostName
	 * @param trafficPort
	 * @param serverPort
	 */
	public ServerApp(final String ways, final String nodes, final String index, final String hostName,
			final int trafficPort, final int serverPort) {
		super(hostName, serverPort);
		// TODO: Set up backend services
	}
	
	@Override
	public void start() {
		System.out.println("Server up and running!");
		while (true) {
			// TODO: Run server
		}
	}
	
}
