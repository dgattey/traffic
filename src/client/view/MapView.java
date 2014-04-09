package client.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import main.Utils;
import client.ClientApp;
import data.ClientMapWay;
import data.LatLongPoint;

/**
 * Canvas class to draw shapes to screen with ability to zoom and pan
 * 
 * @author dgattey
 */
public class MapView extends JComponent {
	
	private static final long		serialVersionUID	= 3037328084114829389L;
	private final ClientApp			app;
	
	// Constants
	private static final Color		COLOR_WAY			= new Color(240, 240, 240);
	private static final Color		COLOR_POINT			= new Color(30, 30, 170);
	private static final Color		COLOR_ROUTE			= new Color(80, 150, 80);
	private static final Color		COLOR_TRAFFIC_MAX	= Color.red.darker();
	private static final Color		COLOR_TRAFFIC_MIN	= Color.yellow;
	private static final double		MIN_SCALE			= 10000.0;
	private static final double		MAX_SCALE			= 180000.0;
	private static final double		SIZE_POINT			= 15;
	
	// Information for translation and scale
	private final Point2D.Double	currTranslation		= new Point2D.Double(0, 0);
	private double					currScale			= 60000.0;
	private int						screenWidth;
	private int						screenHeight;
	private final LatLongPoint		centerPoint;
	private double					centerScale			= 1.0;
	
	/**
	 * Constructor for a Canvas
	 * 
	 * @param app the app representing this run
	 */
	public MapView(final ClientApp app) {
		centerPoint = app.getHub().getAppLoadPoint();
		this.app = app;
	}
	
	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		
		// Create screen content and blank it out
		final Graphics2D content = (Graphics2D) g;
		screenWidth = getWidth();
		screenHeight = getHeight();
		content.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		content.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		content.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		content.setColor(ViewController.COLOR_WINDOW);
		content.fillRect(0, 0, screenWidth, screenHeight);
		
		chunk();
		
		// Drawing!
		drawMap(content);
	}
	
	/**
	 * Take the viewport size and chunk in the chunks that exist within the current window
	 */
	public void chunk() {
		final LatLongPoint nw = screenToLatLong(new Point2D.Double(0, screenHeight));
		final LatLongPoint se = screenToLatLong(new Point2D.Double(screenWidth, screenHeight * 2));
		final LatLongPoint sw = new LatLongPoint(se.getLat(), nw.getLong());
		final LatLongPoint ne = new LatLongPoint(nw.getLat(), se.getLong());
		app.getViewController().chunkInVisible(nw, se, sw, ne);
	}
	
	/**
	 * Returns a color for a street that deals with traffic data
	 * 
	 * @param street a street name that may exist
	 * @return a Color representing this street
	 */
	private Color getStreetColor(final String street) {
		final Double value = app.getHub().getTrafficValue(street);
		final Double maxVal = app.getHub().getMaxTrafficValue();
		final Double minVal = app.getHub().getMinTrafficValue();
		if (value == null || maxVal == null || minVal == null) {
			return COLOR_WAY;
		}
		final double percentVal = (value - minVal) / (maxVal - minVal);
		return interpolateColors(COLOR_TRAFFIC_MIN, COLOR_TRAFFIC_MAX, percentVal);
	}
	
	/**
	 * Gives a color between two others, given a high/low color and percent of between
	 * 
	 * @param low the lower color
	 * @param high the higher color
	 * @param percent the percent between the two
	 * @return a new color representing an interpolation between the two
	 */
	public static Color interpolateColors(final Color low, final Color high, final double percent) {
		final Double[] colors = new Double[3];
		colors[0] = Utils.normalize(low.getRed(), high.getRed(), percent);
		colors[1] = Utils.normalize(low.getGreen(), high.getGreen(), percent);
		colors[2] = Utils.normalize(low.getBlue(), high.getBlue(), percent);
		for (int i = 0; i < colors.length; i++) {
			if (colors[i] < 0) {
				colors[i] = 0.0;
			} else if (colors[i] > 255) {
				colors[i] = 255.0;
			}
		}
		return new Color(colors[0].intValue(), colors[1].intValue(), colors[2].intValue());
	}
	
	/**
	 * Draws our representation of the map to screen by drawing the roads, route, and points user has clicked
	 * 
	 * @param content the Graphics2D object representing the map
	 */
	private void drawMap(final Graphics2D content) {
		content.clipRect(0, 0, screenWidth, screenHeight);
		final double delta = doubleToScreen(0.0);
		final LatLongPoint screenMin = screenToLatLong(new Point2D.Double(0 - delta, screenHeight - delta));
		final LatLongPoint screenMax = screenToLatLong(new Point2D.Double(screenWidth + delta, screenHeight * 2 + delta));
		
		// Draw all chunks that are currently within view (by checking intersections)
		final Set<String> drawnWays = new HashSet<>();
		for (final LatLongPoint p : app.getViewController().getChunks().keySet()) {
			if (LatLongPoint.intersectChunk(p, p.plus(MapChunk.CHUNKSIZE, MapChunk.CHUNKSIZE), screenMax, screenMin)) {
				final List<ClientMapWay> allWays = app.getViewController().getChunks().get(p).getWays();
				for (final ClientMapWay w : allWays) {
					
					// Make sure ways that span two chunks don't get drawn twice
					if (drawnWays.contains(w.getID())) {
						continue;
					}
					
					// Only draws lines that are big enough to make a difference for the user
					final Line2D line = makeLine2D(w);
					if (line.getP2().distanceSq(line.getP1()) > 10) {
						content.setColor(getStreetColor(w.getName()));
						content.draw(line);
						drawnWays.add(w.getID());
					}
				}
			}
		}
		
		// Draw mouse clicks
		for (final LatLongPoint p : app.getViewController().getUserPoints()) {
			if (p == null) {
				continue;
			}
			drawPoint(content, p, COLOR_POINT, Color.WHITE);
		}
		
		// Route
		final List<ClientMapWay> route = app.getViewController().getRoute();
		if (route != null && !route.isEmpty()) {
			
			// Draw all ways of the route
			content.setColor(COLOR_ROUTE);
			content.setStroke(new BasicStroke(6.0f));
			for (final ClientMapWay l : route) {
				content.draw(makeLine2D(l));
			}
			
			// Start point
			if (route.get(0) != null) {
				final LatLongPoint startPoint = route.get(0).getStart().getPoint();
				drawPoint(content, startPoint, COLOR_ROUTE, Color.WHITE);
				drawText(content, "Start", startPoint);
			}
			
			// End point
			if (route.get(route.size() - 1) != null) {
				final LatLongPoint endPoint = route.get(route.size() - 1).getEnd().getPoint();
				drawPoint(content, endPoint, COLOR_ROUTE, Color.WHITE);
				drawText(content, "End", endPoint);
			}
		}
	}
	
	/**
	 * Convenience method to draw a point to screen given a latLong point and outer and inner colors
	 * 
	 * @param content the graphics object to use to draw
	 * @param point the LatLongPoint to draw
	 * @param inner the inner color
	 * @param outer the outer color
	 */
	private void drawPoint(final Graphics2D content, final LatLongPoint point, final Color inner, final Color outer) {
		final Ellipse2D.Double circle = makeEllipse2D(point);
		content.setColor(inner);
		content.fill(circle);
		content.setStroke(new BasicStroke(1.6f));
		content.setColor(outer);
		content.draw(circle);
	}
	
	/**
	 * Draws text to screen with a shadow
	 * 
	 * @param content the graphics object to use to draw
	 * @param message the message for the text
	 * @param p the point at which to draw it (offset though)
	 */
	private void drawText(final Graphics2D content, final String message, final LatLongPoint p) {
		final Point2D.Double converted = latLongToScreen(p);
		content.setFont(new Font(ViewController.FONT, Font.BOLD, 15));
		
		final int w = content.getFontMetrics(content.getFont()).getStringBounds(message, content).getBounds().width;
		
		content.setColor(new Color(0, 0, 0, 190));
		content.fillRoundRect((int) converted.x + 4, (int) converted.y - 5, w + 14, 25, 15, 15);
		content.setColor(Color.WHITE);
		content.drawString(message, (float) converted.x + 10, (float) converted.y + 10);
	}
	
	/**
	 * Converts a map way to a line to be able to be used to draw to screen
	 * 
	 * @param way the way to convert
	 * @return a Line2D representation of the way
	 */
	public Line2D.Double makeLine2D(final ClientMapWay way) {
		final Point2D.Double start = latLongToScreen(way.getStart().getPoint());
		final Point2D.Double end = latLongToScreen(way.getEnd().getPoint());
		return new Line2D.Double(start, end);
	}
	
	/**
	 * Creates a rectangle from a point and a width and height
	 * 
	 * @param p a lat long point to draw
	 * @param deltaW width of rectangle
	 * @param deltaH height of rectangle
	 * @return a new Rectangle object representing this area on the map
	 */
	public Rectangle2D.Double makeRect2D(final LatLongPoint p, final double deltaW, final double deltaH) {
		final Point2D.Double temp = latLongToScreen(p);
		return new Rectangle2D.Double(temp.getX(), temp.getY(), doubleToScreen(deltaW), doubleToScreen(deltaH));
	}
	
	/**
	 * Creates a circle from a converted LatLongPoint
	 * 
	 * @param p a converted point to make a circle from
	 * @return an ellipse representing this point
	 */
	public Ellipse2D.Double makeEllipse2D(final LatLongPoint p) {
		final double delta = SIZE_POINT;
		final Point2D.Double temp = latLongToScreen(p);
		return new Ellipse2D.Double(temp.getX() - delta / 2, temp.getY() - delta / 2, delta, delta);
	}
	
	/**
	 * Converts LatLongPoint to ScreenPoint
	 * 
	 * @param p the point to convert
	 * @return a Point2D representing the converted point
	 */
	public Point2D.Double latLongToScreen(final LatLongPoint p) {
		final double finalX = ((p.getX() - centerPoint.getX()) * currScale) + (screenWidth / 2.0)
			- (currTranslation.getX() * centerScale);
		final double finalY = ((-p.getY() + centerPoint.getY()) * currScale) + (screenHeight / 2.0)
			- (currTranslation.getY() * centerScale);
		return new Point2D.Double(finalX, finalY);
	}
	
	/**
	 * Converts ScreenPoint to LatLongPoint
	 * 
	 * @param p the point to convert
	 * @return a LatLongPoint representing the converted point
	 */
	public LatLongPoint screenToLatLong(final Point2D.Double p) {
		final double finalLong = (p.getX() - screenWidth / 2.0 + (currTranslation.getX() * centerScale)) / currScale
			+ centerPoint.getX();
		final double finalLat = (-p.getY() + screenHeight / 2.0 - (currTranslation.getY() * centerScale)) / currScale
			+ centerPoint.getY();
		return new LatLongPoint(finalLat, finalLong);
	}
	
	/**
	 * Converts a lat long double to screen coordinates
	 * 
	 * @param d a given double
	 * @return the converted double in screen coordinates
	 */
	public double doubleToScreen(final double d) {
		return d * currScale;
	}
	
	/**
	 * Converts a screen double to lat long coordinates
	 * 
	 * @param d a given double
	 * @return the converted double in lat long coordinates
	 */
	public double doubleToLatLong(final double d) {
		return d / currScale;
	}
	
	/**
	 * Translates the canvas by the given amount (as a Point2D)
	 * 
	 * @param p the new point
	 */
	public void translateBy(final Point2D.Double p) {
		final double scalar = currScale / MAX_SCALE * 50.0;
		currTranslation.setLocation(currTranslation.getX() + p.getX() / scalar, currTranslation.getY() + p.getY()
			/ scalar);
		repaint();
	}
	
	/**
	 * Scales the canvas by a given amount as a double
	 * 
	 * @param s the direction to scale
	 */
	public void scaleBy(final double s) {
		final double newScale = currScale * s;
		if (newScale <= MAX_SCALE && newScale >= MIN_SCALE) {
			currScale = newScale;
			centerScale *= s;
		}
	}
	
	/**
	 * Centers the viewport on a given a point
	 * 
	 * @param point the new center
	 */
	public void centerViewOn(final LatLongPoint point) {
		app.getViewController().clearPoints();
		final double finalX = ((point.getX() - centerPoint.getX()) * currScale / centerScale);
		final double finalY = ((-point.getY() + centerPoint.getY()) * currScale / centerScale);
		final double deltaX = (finalX - currTranslation.getX()) / 200;
		final double deltaY = (finalY - currTranslation.getY()) / 200;
		double tempX = currTranslation.getX();
		double tempY = currTranslation.getY();
		while (Math.abs(tempX - finalX) > 0.00001 && Math.abs(tempY - finalY) > 0.00001) {
			currTranslation.setLocation(new Point2D.Double(tempX, tempY));
			repaint();
			tempX += deltaX;
			tempY += deltaY;
			final long time = System.currentTimeMillis();
			while (System.currentTimeMillis() - time < 0.005) {}
		}
		currTranslation.setLocation(new Point2D.Double(finalX, finalY));
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(900, 560);
	}
	
	/**
	 * FOR TESTING - sets width of screen manually
	 * 
	 * @param i the new width
	 */
	public void setScreenWidth(final int i) {
		screenWidth = i;
	}
	
	/**
	 * FOR TESTING - sets height of screen manually
	 * 
	 * @param i the new height
	 */
	public void setScreenHeight(final int i) {
		screenHeight = i;
	}
	
}
