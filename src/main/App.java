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
	 * @param hostName
	 * @param serverPort
	 */
	public App(final String hostName, final int serverPort) {
		this.serverPort = serverPort;
		this.hostName = hostName;
	}
	
	/**
	 * Starts the user interface or evaluation of input, based off the class
	 */
	public abstract void start();
	
}
