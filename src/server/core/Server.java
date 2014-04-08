package server.core;

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
	private boolean						_running;
	private final ServerSocket			_socket;
	private final ResponseController	_response;
	private final TrafficController		_traffic;
	
	public Server(final String ways, final String nodes, final String index, final String hostName,
			final int trafficPort, final int serverPort) throws IOException, MapException {
		if (serverPort <= 1024) {
			throw new IllegalArgumentException("<Server> Ports under 1025 are reserved.");
		}
		if (Utils.anyNullOrEmpty(ways, nodes, index, hostName)) {
			throw new IllegalArgumentException("<Server> Non-null, non-empty arguments expected.");
		}
		
		_port = serverPort;
		_response = new ResponseController(ways, nodes, index, hostName, trafficPort, _port);
		_socket = new ServerSocket(_port);
		_traffic = new TrafficController(hostName, serverPort);
		_traffic.startGettingTraffic();
	}
	
	public ResponseController getRC() {
		return _response;
	}
	
	@Override
	public void run() {
		_running = true;
		while (_running) {
			try {
				final Socket clientConnection = _socket.accept();
				System.out.println("Connected to a client!");
				final ClientHandler c = new ClientHandler(clientConnection, this);
				c.start();
			} catch (final IOException e) {
				Utils.printError("<Server> Failed to accept clients.");
				System.exit(1);
			}
			
		}
	}
	
	public void addClientToTrafficPool(final ClientHandler c) {
		_traffic.getPool().add(c);
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
