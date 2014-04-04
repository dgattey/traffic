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
	
	private final Socket				_client;
	private final BufferedReader		_input;
	private final PrintWriter			_output;
	
	private final ResponseController	_response;
	
	private final String				AC	= "@q:AC";
	private final String				RS	= "@q:RS";
	private final String				RC	= "@q:RC";
	private final String				MC	= "@q:MC";
	
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
	
	void dispatch() {
		String req_start = "";
		try {
			req_start = _input.readLine();
		} catch (final IOException e) {
			_response.errorResponse(this);
		}
		switch (req_start) {
		case AC:
			_response.autocorrectResponse(this);
			break;
		case RS:
			_response.routeFromNamesResponse(this);
			break;
		case RC:
			_response.routeFromClicksResponse(this);
			break;
		case MC:
			_response.mapDataResponse(this);
		default:
			_response.errorResponse(this);
		}
		
	}
	
	/**
	 * 
	 */
	@Override
	public void run() {
		// The worker thread is created so the main client thread may listen to heartbeats from
		// the client end, to know when a client hangs up unexpectedly
		final Thread worker = new Thread() {
			
			@Override
			public void run() {
				dispatch();
			}
		};
		
		worker.start();
		while (worker.isAlive()) {
			// Check if peer connection still exists
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
