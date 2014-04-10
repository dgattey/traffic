package server.core;

import java.io.IOException;
import java.util.Scanner;

import main.App;
import main.Utils;
import data.MapException;

/**
 * Starts the server running
 * 
 * @author dgattey
 */
public class ServerApp extends App {
	
	private Server	server;
	private int		trafficPort;
	
	/**
	 * Starts a new Server App
	 * 
	 * @param ways the filename for ways
	 * @param nodes the filename for nodes
	 * @param index the filename for index
	 * @param hostName the host name for traffic
	 * @param trafficPort the traffic port
	 * @param serverPort the server port
	 */
	public ServerApp(final String ways, final String nodes, final String index, final String hostName,
			final int trafficPort, final int serverPort) {
		super(hostName, serverPort);
		checkPort(trafficPort, "traffic");
		try {
			server = new Server(ways, nodes, index, hostName, trafficPort, serverPort);
			this.trafficPort = trafficPort;
		} catch (IOException | MapException e) {
			Utils.printError("<ServerApp> Server could not be started.");
			System.exit(1);
		}
	}
	
	/**
	 * Just runs the server on another thread and listens for a quit line
	 */
	@Override
	public void start() {
		server.start();
		
		// Listen for any commandline input; quit on "exit" or emptyline
		final Scanner scanner = new Scanner(System.in);
		String line = null;
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			if (line.length() == 0 || line.equalsIgnoreCase("exit")) {
				server.interrupt();
				try {
					server.kill();
					break;
				} catch (final IOException e) {
					Utils.printError("<Server App> Server did not shutdown properly.");
				}
			} else if (line.equalsIgnoreCase("status")) {
				printStatus();
			}
			
		}
		scanner.close();
		System.exit(1);
	}
	
	/**
	 * Prints out a status block
	 */
	private void printStatus() {
		final StringBuilder b = new StringBuilder();
		b.append("-----------------------\n");
		b.append("SERVER STATUS\n");
		b.append(String.format("Server running with port number %d\n", serverPort));
		if (server._traffic.isConnected()) {
			b.append(String.format("Connected to traffic bot on port %d on server %s\n", trafficPort, hostName));
		} else {
			b.append("Not connected to traffic bot\n");
		}
		b.append("Traffic data saved for " + server._traffic.getMap().size() + " ways\n");
		if (server._traffic.isConnected()) {
			b.append("Number of clients connected: " + server._traffic.getPool().getSize() + "\n");
		}
		b.append("-----------------------\n");
		System.out.println(b.toString());
	}
}
