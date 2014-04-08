package server.core;

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
	
	private final Socket			_client;
	private final BufferedReader	_input;
	private final PrintWriter		_output;
	
	private final Server			_server;
	
	/**
	 * Constructs a ClientHandler on the given client
	 * 
	 * @param client the client to handle
	 * @throws IOException if the client socket is invalid
	 * @throws IllegalArgumentException if client is null
	 */
	public ClientHandler(final Socket client, final Server server) throws IOException {
		if (client == null) {
			throw new IllegalArgumentException("Cannot accept null arguments.");
		}
		
		_client = client;
		_input = new BufferedReader(new InputStreamReader(_client.getInputStream()));
		_output = new PrintWriter(_client.getOutputStream(), true);
		_server = server;
	}
	
	/**
	 * Returns false when we don't want to kill the socket
	 * 
	 * @return if the socket should be killed
	 */
	boolean dispatch() {
		String req_start = "";
		try {
			
			// Stops the hanging issue
			if (!_server.getRC().isReady()) {
				return true;
			}
			
			// Do stuff!
			req_start = _input.readLine();
			if (req_start == null) {
				_server.getRC().errorResponse(_output, null);
			} else if (req_start.startsWith(ProtocolManager.Q_HB)) {
				
			} else if (req_start.startsWith(ProtocolManager.Q_TR)) {
				_server.subscribeToTraffic(this);
				return false;
			} else if (req_start.startsWith(ProtocolManager.Q_AC)) {
				_server.getRC().autocorrectResponse(_input, _output);
			} else if (req_start.startsWith(ProtocolManager.Q_RS)) {
				_server.getRC().routeFromNamesResponse(_input, _output);
			} else if (req_start.startsWith(ProtocolManager.Q_RP)) {
				_server.getRC().routeFromClicksResponse(_input, _output);
			} else if (req_start.startsWith(ProtocolManager.Q_MC)) {
				_server.getRC().mapDataResponse(_input, _output);
			} else {
				_server.getRC().errorResponse(_output, null);
			}
		} catch (final IOException e) {
			// It's possible that the IOException was caused by writing to a closed socket, in which case trying
			// to write again doesn't make a whole lot of sense. I suppose we just try responsding and then "kill" the
			// client
			try {
				_server.getRC().errorResponse(_output, e);
			} catch (final IOException e1) {
				kill();
			}
		}
		return true;
		
	}
	
	/**
	 * Dispatches a request, kills if it should (most cases)
	 */
	@Override
	public void run() {
		if (dispatch()) {
			kill();
		}
	}
	
	public BufferedReader getReader() {
		return _input;
	}
	
	/**
	 * Send a string to the client via the socket
	 * 
	 * @param message response to send
	 * @throws IOException
	 */
	public void send(final String message) throws IOException {
		_output.write(message);
		_output.flush();
		_client.shutdownOutput();
	}
	
	/**
	 * Sends without closing
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void sendWithoutClosing(final String message) throws IOException {
		_output.write(message + "\n");
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
