package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import main.Utils;

/**
 * Wraps the client socket and encapsulates all IO
 * 
 * @author aiguha
 */
public class ClientHandler extends Thread {
	
	private final Socket				_client;
	private final BufferedReader		_input;
	private final PrintWriter			_output;
	
	private final ResponseController	_response;
	
	/**
	 * Constructs a ClientHandler on the given client
	 * 
	 * @param client the client to handle
	 * @throws IOException if the client socket is invalid
	 * @throws IllegalArgumentException if client is null
	 */
	public ClientHandler(final Socket client, final ResponseController response) throws IOException {
		if (client == null) {
			throw new IllegalArgumentException("Cannot accept null arguments.");
		}
		
		_client = client;
		_input = new BufferedReader(new InputStreamReader(_client.getInputStream()));
		_output = new PrintWriter(_client.getOutputStream(), true);
		_response = response;
	}
	
	public void dispatch(final String request) {
		
	}
	
	/**
	 * 
	 */
	@Override
	public void run() {
		try {
			// Reads line by line appending to a request, in case line analysis has to be done in the future before
			// dispatching
			String line = null;
			final StringBuilder request = new StringBuilder(64);
			while ((line = _input.readLine()) != null) {
				request.append(line + "\n");
			}
			dispatch(request.toString());
		} catch (final IOException e) {
			Utils.printError("<ClientHandler> Error while reading request from Client.");
		}
	}
	
	/**
	 * Send a string to the client via the socket
	 * 
	 * @param message response to send
	 */
	public void send(final String message) {
		_output.write(message + "\n");
		_output.flush();
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
