package frontend.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

/**
 * Class to deal with panning of a canvas
 * 
 * @author dgattey
 */
class PanHandler implements MouseListener, MouseMotionListener {
	
	private final MapView	mapView;
	private double			referenceX;
	private double			referenceY;
	
	/**
	 * Constructor that sets up a canvas
	 * 
	 * @param mapView the canvas this PanHandler applies to
	 */
	public PanHandler(final MapView mapView) {
		this.mapView = mapView;
	}
	
	/**
	 * Save transformed mouse point for use in mouseDragged
	 */
	@Override
	public void mousePressed(final MouseEvent e) {
		referenceX = e.getX();
		referenceY = e.getY();
	}
	
	/**
	 * Actually move the view, given the new and old mouse points (calculate deltas and move by that amount)
	 */
	@Override
	public void mouseDragged(final MouseEvent e) {
		final double deltaX = referenceX - e.getX();
		final double deltaY = referenceY - e.getY();
		mapView.translateBy(new Point2D.Double(deltaX, deltaY));
	}
	
	@Override
	public void mouseClicked(final MouseEvent e) {}
	
	@Override
	public void mouseEntered(final MouseEvent e) {}
	
	@Override
	public void mouseExited(final MouseEvent e) {}
	
	@Override
	public void mouseMoved(final MouseEvent e) {}
	
	@Override
	public void mouseReleased(final MouseEvent e) {}
}