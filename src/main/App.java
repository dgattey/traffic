package main;

/**
 * Abstract class to implement an App object, used for running the Client or Server
 * 
 * @author dgattey
 */
public abstract class App {
	
	public final String	hostName;
	public final int	serverPort;
	
	/**
	 * Constructor for app that saves host name and server port
	 * 
	 * @param hostName the host name of the server
	 * @param serverPort the server port
	 */
	public App(final String hostName, final int serverPort) {
		checkPort(serverPort, "server");
		this.serverPort = serverPort;
		this.hostName = hostName;
	}
	
	/**
	 * Checks a port and throws an illegalarg exception if ports outside of range
	 * 
	 * @param port the port number to check
	 * @param name the name of the port to describe it to user in case of error
	 */
	protected static void checkPort(final int port, final String name) {
		if (port < 1025 || port > 49150) {
			throw new IllegalArgumentException(name + " port was outside allowable range");
		}
	}
	
	/**
	 * Starts the user interface or evaluation of input, based off the class
	 */
	protected abstract void start();
	
}
