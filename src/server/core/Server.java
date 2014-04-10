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
	
	private boolean						_running;
	private final ServerSocket			_socket;
	private final ResponseController	_response;
	final TrafficController				_traffic;
	
	public Server(final String ways, final String nodes, final String index, final String hostName,
			final int trafficPort, final int serverPort) throws IOException, MapException {
		if (serverPort <= 1024) {
			throw new IllegalArgumentException("<Server> Ports under 1025 are reserved.");
		}
		if (Utils.anyNullOrEmpty(ways, nodes, index, hostName)) {
			throw new IllegalArgumentException("<Server> Non-null, non-empty arguments expected.");
		}
		
		System.out.println("Server is loading...");
		_traffic = new TrafficController(hostName, trafficPort);
		_response = new ResponseController(ways, nodes, index, _traffic);
		_socket = new ServerSocket(serverPort);
		System.out.println("Loaded server! Type \"status\" to see what's happening");
	}
	
	/**
	 * @return returns the response controller
	 */
	public ResponseController getRC() {
		return _response;
	}
	
	@Override
	public void run() {
		_running = true;
		while (_running && !Thread.currentThread().isInterrupted()) {
			try {
				final Socket clientConnection = _socket.accept();
				final ClientHandler c = new ClientHandler(clientConnection, this);
				c.start();
			} catch (final IOException e) {
				if (Thread.currentThread().isInterrupted()) {
					Utils.printMessage("<Server> Shutting Down...");
				} else {
					Utils.printError("<Server> Failed to accept clients.");
				}
				break;
			}
		}
	}
	
	/**
	 * Adds a client to subs
	 * 
	 * @param c the client to be added
	 * @throws IOException if sending failed
	 */
	public void subscribeToTraffic(final ClientHandler c) throws IOException {
		_traffic.sendAll(c);
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
