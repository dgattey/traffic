package client.communicator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import data.Convertible;
import data.ParseException;
import data.ProtocolManager;

/**
 * Represents class to read/write to server from the client
 * 
 * @author dgattey
 */
public class CommController {
	
	private Socket							sock;
	private BufferedWriter					writer;
	private BufferedReader					reader;
	private final String					hostName;
	private final int						serverPort;
	private final static ExecutorService	executor	= Executors.newFixedThreadPool(20);
	
	/**
	 * Executes a callable and returns a V representing what the server returned
	 * 
	 * @param callable a ServerCallable that will interface with server
	 * @return a V to return
	 * @throws IOException if there was an error executing the callable
	 * @throws ParseException if the thread parsed wrong
	 */
	public static <V> V getFromServer(final ServerCallable<V> callable) throws IOException, ParseException {
		try {
			return executor.submit(callable).get();
		} catch (InterruptedException | ExecutionException e) {
			if (e.getMessage() != null && e.getMessage().contains("parse")) {
				throw new ParseException(e.getMessage());
			}
			throw new IOException(e.getMessage());
		}
	}
	
	/**
	 * Sets relevant details to allow connection later
	 * 
	 * @param hostName the string name of the host
	 * @param serverPort the port where the socket should connect
	 */
	public CommController(final String hostName, final int serverPort) {
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
		writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}
	
	/**
	 * Reads from the server for a response header given the tag passed in
	 * 
	 * @param tag a tag that may appear as the header
	 * @throws IOException if something was unintentionally closed
	 * @throws ParseException if there was no header as the next line
	 */
	public void checkForResponseHeader(final String tag) throws IOException, ParseException {
		if (reader == null || sock.isClosed()) {
			throw new IOException("<ClientServerWriter> Socket is closed - can't check response header");
		}
		final String line = reader.readLine();
		if (ProtocolManager.hasErrorTag(line)) {
			throw new ParseException("<ClientServerWriter> Server returned an error");
		}
		ProtocolManager.checkForOpeningTag(line, tag);
	}
	
	/**
	 * Reads from the server for a response footer
	 * 
	 * @throws IOException if something was unintentionally closed
	 * @throws ParseException if there was no header as the next line
	 */
	public void checkForResponseFooter() throws IOException, ParseException {
		if (reader == null || sock.isClosed()) {
			throw new IOException("<ClientServerWriter> Socket is closed - can't check response footer");
		}
		ProtocolManager.checkForResponseFooter(reader.readLine());
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
		sock.shutdownOutput();
		reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		return reader;
	}
	
	/**
	 * Opens the reader to get the response from the server
	 * 
	 * @return the reader
	 * @throws IOException if the socket is closed or if the input stream get failed
	 */
	public BufferedReader getReaderWithoutShutdownOutput() throws IOException {
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
	 * Writes a string with a newline after
	 * 
	 * @param string the message to write
	 * @throws IOException if writing failed
	 */
	public void writeWithNL(final String string) throws IOException {
		write(string);
		write("\n");
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
		sock.shutdownInput();
		sock.close();
		reader.close();
		writer.close();
	}
}
