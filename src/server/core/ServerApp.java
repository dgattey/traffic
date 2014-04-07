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
		checkPort(trafficPort, "traffic");
		try {
			server = new Server(ways, nodes, index, hostName, trafficPort, serverPort);
		} catch (IOException | MapException e) {
			Utils.printError("<ServerApp> Server could not be started.");
			System.exit(1);
		}
	}
	
	@Override
	public void start() {
		server.start();
		// Listen for any commandline input; quit on "exit" or emptyline
		final Scanner scanner = new Scanner(System.in);
		String line = null;
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			if (line.length() == 0 || line.equalsIgnoreCase("exit")) {
				try {
					server.kill();
				} catch (final IOException e) {
					Utils.printError("<ServerApp> Error while killing Server.");
				}
				System.exit(0);
			}
		}
		scanner.close();
	}
	
}
