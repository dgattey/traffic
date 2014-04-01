package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Wraps the client socket and encapsulates all IO
 * 
 * @author aiguha
 */
public class ClientHandler extends Thread {
	
	private final Socket			_client;
	private final BufferedReader	_input;
	private final PrintWriter		_output;
	
	/**
	 * Constructs a ClientHandler on the given client
	 * 
	 * @param client the client to handle
	 * @throws IOException if the client socket is invalid
	 * @throws IllegalArgumentException if client is null
	 */
	public ClientHandler(final Socket client) throws IOException {
		if (client == null) {
			throw new IllegalArgumentException("Cannot accept null arguments.");
		}
		
		_client = client;
		_input = new BufferedReader(new InputStreamReader(_client.getInputStream()));
		_output = new PrintWriter(_client.getOutputStream(), true);
	}
	
	/**
	 * Close this socket and its related streams.
	 * 
	 * @throws IOException Passed up from socket
	 */
	public void kill() throws IOException {
		// Close all the streams after the client disconnects.
		_client.close();
		_input.close();
		_output.close();
	}
	
}
