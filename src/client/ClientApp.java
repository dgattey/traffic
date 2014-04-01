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
	
	private final ViewController	viewController;
	private final HubController		hub;
	
	/**
	 * Uses the App constructor plus gui specific stuff
	 * 
	 * @throws IOException if there was an error reading a file
	 * @throws MapException if the hub had an error
	 */
	public ClientApp(final String hostName, final int serverPort) throws IOException, MapException {
		super(hostName, serverPort);
		viewController = new ViewController(this);
		hub = new HubController(hostName, serverPort, this);
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
	 * @return
	 */
	public HubController getHub() {
		return hub;
	}
	
}
