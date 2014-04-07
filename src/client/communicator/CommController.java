package client.communicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import data.Convertible;
import data.ParseException;

/**
 * Represents class to read/write to server from the client
 * 
 * @author dgattey
 */
public class CommController {
	
	private Socket			sock;
	private PrintWriter		writer;
	private BufferedReader	reader;
	private final String	hostName;
	private final int		serverPort;
	
	/**
	 * Executes a callable and returns a V representing what the server returned
	 * 
	 * @param callable a ServerCallable that will interface with server
	 * @return a V to return
	 * @throws IOException if there was an error executing the callable
	 * @throws ParseException if the thread parsed wrong
	 */
	public static <V> V getFromServer(final ServerCallable<V> callable) throws IOException, ParseException {
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			return executor.submit(callable).get();
		} catch (InterruptedException | ExecutionException e) {
			final Throwable e2 = e.initCause(e);
			if (e2 instanceof ParseException) {
				throw (ParseException) e2;
			}
			throw new IOException(e2);
		}
		finally {
			executor.shutdown();
		}
	}
	
	/**
	 * Sets relevant details to allow connection later
	 * 
	 * @param hostName the string name of the host
	 * @param serverPort the port where the socket should connect
	 */
	CommController(final String hostName, final int serverPort) {
		this.hostName = hostName;
		this.serverPort = serverPort;
	}
	
	/**
	 * Connects to a server and opens the writer
	 * 
	 * @throws IOException if the server or writer failed to open
	 */
	void connect() throws IOException {
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
		writer.write("\n");
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
	void disconnect() throws IOException {
		sock.close();
		reader.close();
		writer.close();
	}
}
