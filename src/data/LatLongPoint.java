package data;

import java.awt.geom.Point2D;

/**
 * Wrapper for Point2D to represent Latitude and Longitude points
 * 
 * @author dgattey
 */
public class LatLongPoint extends Point2D.Double implements Convertible<LatLongPoint> {
	
	private static final long	serialVersionUID	= 5402819518527207517L;
	
	private static final double	EARTH_RADIUS_M		= 6371000;
	
	/**
	 * Constructor takes lat and long and sets x and y from them
	 * 
	 * @param latitude a latitude as a double
	 * @param longitude a longitude as a double
	 */
	public LatLongPoint(final double latitude, final double longitude) {
		y = latitude;
		x = longitude;
	}
	
	/**
	 * Wrapper for Y
	 * 
	 * @return the latitude
	 */
	public double getLat() {
		return y;
	}
	
	/**
	 * Wrapper for X
	 * 
	 * @return the longitude
	 */
	public double getLong() {
		return x;
	}
	
	/**
	 * Floors a latLong to the nearest .01
	 * 
	 * @return a new LatLongPoint, floored
	 */
	public LatLongPoint floor() {
		final double l = Math.round(getLat() * 100.0);
		final double l2 = Math.round(getLong() * 100.0);
		return new LatLongPoint(l / 100.0, l2 / 100.0);
	}
	
	/**
	 * Ceils a latLong to the nearest .01
	 * 
	 * @return a new LatLongPoint, ceiled
	 */
	public LatLongPoint ceil() {
		final double l = Math.round((getLat() * 100.0) + 1);
		final double l2 = Math.round((getLong() * 100.0) + 1);
		return new LatLongPoint(l / 100.0, l2 / 100.0);
	}
	
	/**
	 * Returns this lat long point with an addition of the specified delta lat and long
	 * 
	 * @param lat amount of lat to add
	 * @param lon amount of long to add
	 * @return a new LatLongPoint
	 */
	public LatLongPoint plus(final double lat, final double lon) {
		return new LatLongPoint(getLat() + lat, getLong() + lon);
	}
	
	/**
	 * Returns this lat long point with an addition of the other point
	 * 
	 * @param p the other LatLongPoint to add
	 * @return a new LatLongPoint representing this plus p
	 */
	public LatLongPoint plus(final LatLongPoint p) {
		return new LatLongPoint(getLat() + p.getLat(), getLong() + p.getLong());
	}
	
	/**
	 * Returns this lat long point with an subtraction of the specified delta lat and long
	 * 
	 * @param lat amount of lat to subtract
	 * @param lon amount of long to subtract
	 * @return a new LatLongPoint
	 */
	public LatLongPoint minus(final double lat, final double lon) {
		return new LatLongPoint(getLat() - lat, getLong() - lon);
	}
	
	/**
	 * Returns this lat long point with a subtraction of the other point
	 * 
	 * @param p the other LatLongPoint to subtract
	 * @return a new LatLongPoint representing this minus p
	 */
	public LatLongPoint minus(final LatLongPoint p) {
		return new LatLongPoint(getLat() - p.getLat(), getLong() - p.getLong());
	}
	
	/**
	 * Multiplies this point by a delta
	 * 
	 * @param delta the new scale
	 * @return a new point representing this * delta
	 */
	public LatLongPoint times(final double delta) {
		return new LatLongPoint(getLat() * delta, getLong() * delta);
	}
	
	/**
	 * Divides this point by a delta
	 * 
	 * @param delta the new scale
	 * @return a new point representing this / delta
	 */
	public LatLongPoint divide(final double delta) {
		return new LatLongPoint(getLat() / delta, getLong() / delta);
	}
	
	/**
	 * Returns if two arbitrary chunks intersect
	 * 
	 * @param min1 the minimum of the first chunk
	 * @param max1 the maximum of the first chunk
	 * @param min2 the minimum of the second chunk
	 * @param max2 the maximum of the second chunk
	 * @return if the two chunks intersected
	 */
	public static boolean intersectChunk(LatLongPoint min1, LatLongPoint max1, LatLongPoint min2, LatLongPoint max2) {
		
		// If min and max are switched, switch em for calculations
		if (min1.x > max1.x || min1.y > max1.y) {
			final LatLongPoint temp = max1;
			max1 = min1;
			min1 = temp;
		}
		if (min2.x > max2.x || min2.y > max2.y) {
			final LatLongPoint temp = max2;
			max2 = min2;
			min2 = temp;
		}
		
		final double w1 = Math.abs(max1.x - min1.x);
		final double w2 = Math.abs(max2.x - min2.x);
		final double h1 = Math.abs(max1.y - min1.y);
		final double h2 = Math.abs(max2.y - min2.y);
		final double halfW1 = w1 / 2.0;
		final double halfW2 = w2 / 2.0;
		final double halfH1 = h1 / 2.0;
		final double halfH2 = h2 / 2.0;
		final double cx1 = min1.x + halfW1;
		final double cx2 = min2.x + halfW2;
		final double cy1 = min1.y + halfH1;
		final double cy2 = min2.y + halfH2;
		
		final boolean ret = (halfW1 + halfW2) >= Math.abs(cx2 - cx1) && (halfH1 + halfH2) >= Math.abs(cy2 - cy1);
		return ret;
	}
	
	/**
	 * Uses the Haversine formula to calculate the distance between two points
	 * 
	 * @param other the other point
	 * @return the distance between this and other
	 */
	public double sphericalDistance(final LatLongPoint other) {
		final double latDiff = Math.toRadians(other.getLat() - getLat());
		final double longDiff = Math.toRadians(other.getLong() - getLong());
		final double lat1Rad = Math.toRadians(getLat());
		final double lat2Rad = Math.toRadians(other.getLat());
		final double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) + Math.sin(longDiff / 2)
				* Math.sin(longDiff / 2) * Math.cos(lat1Rad) * Math.cos(lat2Rad);
		final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		final double d = EARTH_RADIUS_M * c;
		return d;
	}
	
	/**
	 * Uses Euclidean distance to calculate distance between two points
	 * 
	 * @param other the other point
	 * @return the distance between this and other
	 */
	public double flatDistance(final LatLongPoint other) {
		final double dist = Math.pow(getLat() - other.getLat(), 2) + Math.pow(getLong() - other.getLong(), 2);
		return Math.abs(Math.sqrt(dist));
	}
	
	@Override
	public String encodeObject() {
		return ProtocolManager.LLP_TAG + getLat() + ProtocolManager.LLP_DELIM + getLong() + "\n";
	}
	
	@Override
	public LatLongPoint decodeObject(final String rep) {
		return null;
	}
}
