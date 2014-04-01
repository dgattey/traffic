package frontend.eventhandlers;

import hub.LatLongPoint;
import hub.MapNode;
import hub.MapWay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.List;

import frontend.app.GUIApp;
import frontend.view.ViewController;

/**
 * Deals with finding a route given four fields representing intersections or two points representing clicks
 * 
 * @author dgattey
 */
public class RouteHandler implements ActionListener, MouseListener {
	
	private final GUIApp	app;
	private Thread			thread;
	
	public RouteHandler(final GUIApp app) {
		this.app = app;
	}
	
	/**
	 * Does route finding by taking the fields, parsing the text into nodes, and getting the route between those nodes.
	 * Has invariants that the four fields exist and have text
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		final ViewController controller = app.getViewController();
		controller.setLabel(ViewController.DEFAULT_LABEL_TEXT);
		if (thread != null) {
			thread.interrupt();
		}
		controller.clearPoints();
		thread = new Thread() {
			
			@Override
			public void run() {
				final List<String> streets = controller.getFields();
				for (final String s : streets) {
					if (s.isEmpty()) {
						controller.setLabel("You've got to enter a street, silly!");
						controller.repaintMap();
						return;
					}
				}
				controller.clearRoute();
				controller.setLabel("Finding a route...");
				final MapNode a = app.getHub().getNearestIntersection(streets.get(0), streets.get(1));
				final MapNode b = app.getHub().getNearestIntersection(streets.get(2), streets.get(3));
				if (!Thread.currentThread().isInterrupted()) {
					makeRoute(a, b, true);
				}
				
			}
		};
		thread.start();
	}
	
	/**
	 * Takes the current route and reverses it
	 */
	public void reverseRoute() {
		final ViewController controller = app.getViewController();
		controller.setLabel(ViewController.DEFAULT_LABEL_TEXT);
		if (thread != null) {
			thread.interrupt();
		}
		final List<MapWay> oldRoute = controller.getRoute();
		controller.clearRoute();
		controller.clearPoints();
		controller.repaintMap();
		thread = new Thread() {
			
			@Override
			public void run() {
				boolean empty = false;
				for (final String street : controller.getFields()) {
					if (street.isEmpty()) {
						empty = true;
						break;
					}
				}
				if (oldRoute != null && !oldRoute.isEmpty()) {
					if (empty) {
						controller.clearFields();
					}
					controller.setLabel("Finding a route...");
					final MapNode b = oldRoute.get(0).getStart();
					final MapNode a = oldRoute.get(oldRoute.size() - 1).getEnd();
					if (!Thread.currentThread().isInterrupted()) {
						makeRoute(a, b, true);
					}
				}
				
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
		controller.setLabel("Click another point to route between the intersections");
		if (thread != null) {
			thread.interrupt();
		}
		controller.clearRoute();
		if (controller.updateUserPoints(new Point2D.Double(e.getX(), e.getY()))) {
			final List<LatLongPoint> pts = controller.getUserPoints();
			thread = new Thread() {
				
				@Override
				public void run() {
					controller.setLabel("Finding a route... Click anywhere to cancel.");
					if (pts != null && !Thread.currentThread().isInterrupted()) {
						final MapNode a = app.getHub().getNearestIntersection(pts.get(0));
						final MapNode b = app.getHub().getNearestIntersection(pts.get(1));
						if (!Thread.currentThread().isInterrupted()) {
							makeRoute(a, b, false);
							controller.clearFields();
							return;
						}
					}
					controller.setLabel(ViewController.DEFAULT_LABEL_TEXT);
				}
			};
			thread.start();
		}
	}
	
	/**
	 * Internal helper to make a route
	 * 
	 * @param a starting point
	 * @param b ending point
	 * @param streets if streets were used to create this or not
	 */
	private void makeRoute(final MapNode a, final MapNode b, final boolean streets) {
		final ViewController controller = app.getViewController();
		if (a != null && b != null) {
			final List<MapWay> route = app.getHub().getRoute(a, b);
			if (!Thread.currentThread().isInterrupted()) {
				controller.setLabel((route != null && !route.isEmpty()) ? "Route found!"
						: "No route found between those points");
				controller.setRoute(route);
			} else {
				controller.setLabel(ViewController.DEFAULT_LABEL_TEXT);
			}
		} else {
			controller.setLabel("No intersections found for those " + (streets ? "streets" : "points"));
			controller.clearRoute();
		}
		controller.clearPoints();
		controller.repaintMap();
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
