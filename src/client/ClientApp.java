package client;

import java.io.IOException;

import javax.swing.SwingUtilities;

import main.App;
import client.hub.HubController;
import client.view.ViewController;
import data.MapException;

/**
 * Creates a GUI interface for the Maps program
 * 
 * @author dgattey
 */
public class ClientApp extends App {
	
	final ViewController		viewController;
	private final HubController	hub;
	public final boolean		debug;
	
	/**
	 * Uses the App constructor plus gui specific stuff
	 * 
	 * @param hostName the host name of the server to connect to
	 * @param serverPort the host port of the server
	 * @param debug if we should be in debug mode
	 * @throws IOException if there was an error reading a file
	 * @throws MapException if the hub had an error
	 */
	public ClientApp(final String hostName, final int serverPort, final boolean debug) throws IOException, MapException {
		super(hostName, serverPort);
		viewController = new ViewController(this);
		hub = new HubController(hostName, serverPort, this);
		this.debug = debug;
	}
	
	/**
	 * Method called from App - creates window, adds objects, links up the listeners for the field, and shows the window
	 */
	@Override
	public void start() {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				viewController.create();
			}
		});
	}
	
	/**
	 * Public getter for the view controller
	 * 
	 * @return the current view controller
	 */
	public ViewController getViewController() {
		return viewController;
	}
	
	/**
	 * Public getter for the hub controller
	 * 
	 * @return the current hub controller
	 */
	public HubController getHub() {
		return hub;
	}
	
}
