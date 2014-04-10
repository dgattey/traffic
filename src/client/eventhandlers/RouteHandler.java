package client.eventhandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

import main.Utils;
import client.ClientApp;
import client.hub.HubController;
import client.view.ViewController;
import data.ClientMapWay;
import data.LatLongPoint;
import data.ParseException;

/**
 * Deals with finding a route given four fields representing intersections or two points representing clicks
 * 
 * @author dgattey
 */
public class RouteHandler implements ActionListener, MouseListener {
	
	final ClientApp	app;
	private Thread	thread;
	
	public RouteHandler(final ClientApp app) {
		this.app = app;
	}
	
	/**
	 * Removes all current requests by cancelling
	 */
	public void cancelRequests() {
		if (thread != null) {
			thread.interrupt();
		}
	}
	
	/**
	 * Does route finding by taking the fields, parsing the text into nodes, and getting the route between those nodes.
	 * Has invariants that the four fields exist and have text
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		final ViewController controller = app.getViewController();
		final HubController hub = app.getHub();
		controller.setLabel(ViewController.DEFAULT_STATUS);
		cancelRequests();
		controller.clearRoute();
		controller.clearPoints();
		
		// Make a new thread and try finding a route
		thread = new Thread() {
			
			@Override
			public void run() {
				// Update status and get data
				controller.setLabel("Finding a route...");
				final List<String> streets = controller.getFields();
				List<ClientMapWay> route = null;
				try {
					route = hub.getRoute(streets.get(0), streets.get(1), streets.get(2), streets.get(3));
				} catch (final IllegalArgumentException e) {
					if (e.getMessage() != null) {
						controller.setLabel(e.getMessage());
					} else {
						controller.setLabel(ViewController.DEFAULT_STATUS);
					}
				} catch (final IOException e) {
					if (e.getMessage() != null) {
						controller.setLabel(e.getMessage());
						Utils.printError("No connection for getting route");
					} else {
						controller.setLabel(ViewController.DEFAULT_STATUS);
					}
				} catch (final ParseException e) {
					if (e.getMessage() != null) {
						controller.setLabel(e.getMessage());
						Utils.printError("Parsing error in getting route");
					} else {
						controller.setLabel(ViewController.DEFAULT_STATUS);
					}
				}
				
				// Someone else wanted a route, so just return
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				
				// Refresh frontend
				controller.clearPoints();
				if (route == null) {
					controller.setLabel(ViewController.DEFAULT_STATUS);
				} else {
					controller.setLabel((!route.isEmpty()) ? "Route found!"
							: "No route found between those intersections");
				}
				controller.setRoute(route);
				controller.repaintMap();
			}
		};
		thread.start();
	}
	
	/**
	 * Sends the clicks to the canvas to draw and check if a route needs to be drawn. If so, use those points to get a
	 * route that can be passed back to the canvas to draw
	 */
	@Override
	public void mouseClicked(final MouseEvent e) {
		final ViewController controller = app.getViewController();
		final HubController hub = app.getHub();
		cancelRequests();
		final boolean routeExisted = controller.getRoute() != null && !controller.getRoute().isEmpty();
		controller.clearRoute();
		
		// If there's only one point in the user points or a route existed, just return
		if (routeExisted) {
			controller.setLabel(ViewController.DEFAULT_STATUS);
			controller.repaintMap();
			return;
		} else if (!controller.updateUserPoints(new Point2D.Double(e.getX(), e.getY()))) {
			controller.setLabel("Click another point to route between the intersections");
			controller.repaintMap();
			return;
		}
		controller.repaintMap();
		
		// Make a new thread and try finding a route
		thread = new Thread() {
			
			@Override
			public void run() {
				// Update status and get data
				controller.setLabel("Finding a route...");
				final List<LatLongPoint> pts = controller.getUserPoints();
				List<ClientMapWay> route = null;
				try {
					route = hub.getRoute(pts.get(0), pts.get(1));
				} catch (final IOException e) {
					if (e.getMessage() != null) {
						controller.setLabel(e.getMessage());
						if (app.debug) {
							Utils.printError("No connection for getting route");
						}
					}
				} catch (final ParseException e) {
					if (e.getMessage() != null) {
						controller.setLabel(e.getMessage());
						if (app.debug) {
							Utils.printError("Parse error in getting route");
						}
					}
				}
				
				// Someone else wanted a route, so just return
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				
				// Refresh frontend
				controller.clearFields();
				controller.clearPoints();
				if (route == null) {
					controller.setLabel(ViewController.DEFAULT_STATUS);
				} else {
					controller.setLabel((!route.isEmpty()) ? "Route found!" : "No route found between those points");
				}
				controller.setRoute(route);
				controller.repaintMap();
				
			}
		};
		thread.start();
		
	}
	
	@Override
	public void mousePressed(final MouseEvent e) {}
	
	@Override
	public void mouseReleased(final MouseEvent e) {}
	
	@Override
	public void mouseEntered(final MouseEvent e) {}
	
	@Override
	public void mouseExited(final MouseEvent e) {}
	
}
