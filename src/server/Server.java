package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import main.Utils;

/**
 * Main maps server that listens for incoming client connections and starts them on separate threads
 * 
 * @author aiguha
 */
public class Server extends Thread {
	
	private final int			port;
	private final ServerSocket	socket;
	private boolean				running;
	
	public Server(final int port) throws IOException {
		if (port <= 1024) {
			throw new IllegalArgumentException("<Server> Ports below 1025 are reserved.");
		}
		
		this.port = port;
		// TODO: Set up a server socket that will listen to socket connection requests
		socket = new ServerSocket(this.port);
	}
	
	@Override
	public void run() {
		running = true;
		// TODO: Set up a while loop to receive all the socket connection
		// requests made by a client
		while (running) {
			try {
				final Socket clientConnection = socket.accept();
				System.out.println("Connected to a client!");
				final ClientHandler c = new ClientHandler(clientConnection);
				c.start();
			} catch (final IOException e) {
				Utils.printError("<Server> Failed to accept clients.");
				System.exit(1);
			}
			
		}
	}
}
