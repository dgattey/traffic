package frontend.app;

import hub.HubController;
import hub.MapsException;

import java.io.IOException;

import javax.swing.SwingUtilities;

import frontend.view.ViewController;

/**
 * Creates a GUI interface for the Maps program
 * 
 * @author dgattey
 */
public class GUIApp extends App {
	
	private final ViewController	viewController;
	
	/**
	 * Uses the App constructor plus gui specific stuff
	 * 
	 * @param ways the ways filename
	 * @param nodes the nodes filename
	 * @param index the index filename
	 * @param debug if we should be in debug mode
	 * @throws IOException if there was an error reading a file
	 * @throws MapsException if the hub had an error
	 */
	public GUIApp(final String ways, final String nodes, final String index, final boolean debug) throws IOException,
			MapsException {
		super(ways, nodes, index, debug);
		viewController = new ViewController(this);
		hub = new HubController(ways, nodes, index, this);
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
	
}
