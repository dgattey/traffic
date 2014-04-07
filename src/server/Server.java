package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import main.Utils;
import data.MapException;

/**
 * Main maps server that listens for incoming client connections and starts them on separate threads
 * 
 * @author aiguha
 */
public class Server extends Thread {
	
	private final int					_port;
	private final ServerSocket			_socket;
	private boolean						_running;
	private final ResponseController	_response;
	
	public Server(final String ways, final String nodes, final String index, final String hostName,
			final int trafficPort, final int serverPort) throws IOException, MapException {
		if (serverPort <= 1024) {
			throw new IllegalArgumentException("<Server> Ports under 1025 are reserved.");
		}
		if (Utils.anyNullOrEmpty(ways, nodes, index, hostName)) {
			throw new IllegalArgumentException("<Server> Non-null, non-empty arguments expected.");
		}
		
		_port = serverPort;
		_socket = new ServerSocket(_port);
		_response = new ResponseController(ways, nodes, index, hostName, trafficPort, serverPort);
	}
	
	@Override
	public void run() {
		_running = true;
		while (_running) {
			try {
				final Socket clientConnection = _socket.accept();
				System.out.println("Connected to a client!");
				final ClientHandler c = new ClientHandler(clientConnection, _response);
				c.start();
			} catch (final IOException e) {
				Utils.printError("<Server> Failed to accept clients.");
				System.exit(1);
			}
			
		}
	}
	
	/**
	 * Stop waiting for connections, close all connected clients, and close this server's ServerSocket
	 * 
	 * @throws IOException if any socket is invalid.
	 */
	public void kill() throws IOException {
		_running = false;
		_socket.close();
	}
}
