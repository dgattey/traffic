package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import main.Utils;
import data.ProtocolManager;

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
	
	void dispatch() {
		String req_start = "";
		try {
			req_start = _input.readLine();
			switch (req_start) {
			case ProtocolManager.AC_Q:
				_response.autocorrectResponse(this);
				break;
			case ProtocolManager.RS_Q:
				ResponseController.routeFromNamesResponse(this);
				break;
			case ProtocolManager.RP_Q:
				_response.routeFromClicksResponse(this);
				break;
			case ProtocolManager.MC_Q:
				ResponseController.mapDataResponse(this);
			default:
				ResponseController.errorResponse(this, null);
			}
		} catch (final IOException e) {
			// It's possible that the IOException was caused by writing to a closed socket, in which case trying
			// to write again doesn't make a whole lot of sense. I suppose we just try responsding and then "kill" the
			// client
			ResponseController.errorResponse(this, e);
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
		try {
			Thread.sleep(2000);
			
			while (worker.isAlive()) {
				// Check if peer connection still exists
			}
		} catch (final InterruptedException e) {}
		kill();
		
	}
	
	public BufferedReader getReader() {
		return _input;
	}
	
	/**
	 * Send a string to the client via the socket
	 * 
	 * @param message response to send
	 */
	public void send(final String message) {
		_output.write(message);
		_output.flush();
	}
	
	/**
	 * Close this socket and its related streams.
	 * 
	 * @throws IOException Passed up from socket
	 */
	public void kill() {
		// Close all the streams after the client disconnects.
		try {
			_client.close();
			_input.close();
			_output.close();
		} catch (final IOException e) {
			Utils.printError("<ClientHandler> Unrecoverable IO error in client");
		}
		
	}
	
}
