package client.hub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import data.Convertible;

/**
 * Represents class to read/write to server from the client
 * 
 * @author dgattey
 */
class ClientCommunicator {
	
	private Socket			sock;
	private PrintWriter		writer;
	private BufferedReader	reader;
	private final String	hostName;
	private final int		serverPort;
	
	/**
	 * Sets relevant details to allow connection later
	 * 
	 * @param hostName the string name of the host
	 * @param serverPort the port where the socket should connect
	 */
	public ClientCommunicator(final String hostName, final int serverPort) {
		this.hostName = hostName;
		this.serverPort = serverPort;
	}
	
	/**
	 * Connects to a server and opens the writer
	 * 
	 * @throws IOException if the server or writer failed to open
	 */
	public void connect() throws IOException {
		if (sock != null) {
			sock.close();
		}
		sock = new Socket(hostName, serverPort);
		writer = new PrintWriter(sock.getOutputStream());
	}
	
	/**
	 * Flushes the writer and opens the reader to get the response from the server
	 * 
	 * @return the reader created to the server
	 * @throws IOException if the socket is closed or if the input stream getting is bad
	 */
	public BufferedReader getReader() throws IOException {
		if (sock == null || sock.isClosed()) {
			throw new IOException("<ClientServerWriter> Socket is closed - can't open reader");
		}
		writer.flush();
		reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		return reader;
	}
	
	/**
	 * Writes some sort of convertible object to the server
	 * 
	 * @param obj the object to write to server
	 * @throws IOException if the server failed to accept the object being written
	 */
	public void write(final Convertible<?> obj) throws IOException {
		if (sock == null || sock.isClosed()) {
			throw new IOException("<ClientServerWriter> Socket is closed - can't write");
		}
		writer.write(obj.encodeObject());
	}
	
	/**
	 * Writes a string to the server (used for actual strings)
	 * 
	 * @param str the string to write to server
	 * @throws IOException if the server failed to accept the object being written
	 */
	public void write(final String str) throws IOException {
		if (sock == null || sock.isClosed()) {
			throw new IOException("<ClientServerWriter> Socket is closed - can't write string");
		}
		writer.write(str);
	}
	
	/**
	 * Shuts down the server, reader, and writer for use later
	 * 
	 * @throws IOException if something went wrong in closing everything
	 */
	public void disconnect() throws IOException {
		sock.close();
		reader.close();
		writer.close();
	}
}
