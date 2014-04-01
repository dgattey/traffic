package client.view;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Class to scale the canvas given a change in the slider
 * 
 * @author dgattey
 */
class ScaleHandler implements MouseWheelListener {
	
	private final MapView	mapView;
	
	public ScaleHandler(final MapView mapView) {
		this.mapView = mapView;
	}
	
	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		double zoom = 1.0;
		final double delta = 0.05;
		if (e.getWheelRotation() > 0) {
			zoom -= delta;
		} else if (e.getWheelRotation() < 0) {
			zoom += delta;
		}
		mapView.scaleBy(zoom);
		mapView.repaint();
	}
}