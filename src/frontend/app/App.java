package frontend.app;

import hub.HubController;
import hub.MapsException;

import java.io.IOException;

/**
 * Abstract class to implement an App object, used for running the GUI or REPL (command line interface)
 * 
 * @author dgattey
 */
public abstract class App {
	
	protected HubController	hub;
	protected final boolean	debug;
	
	/**
	 * Creates HubController
	 * 
	 * @param ways the ways filename
	 * @param nodes the nodes filename
	 * @param index the index filename
	 * @param debug if we should be in debug mode
	 * @throws IOException if there was an error reading a file
	 * @throws MapsException if the hub had an error
	 */
	public App(final String ways, final String nodes, final String index, final boolean debug) throws IOException,
			MapsException {
		this.debug = debug;
	}
	
	/**
	 * Getter for the hub of the app
	 * 
	 * @return the hub
	 */
	public HubController getHub() {
		return hub;
	}
	
	/**
	 * Starts the user interface or evaluation of input, based off the class
	 */
	public abstract void start();
	
}
