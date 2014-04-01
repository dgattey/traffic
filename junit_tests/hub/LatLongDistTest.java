package hub;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

@SuppressWarnings("static-method")
public class LatLongDistTest {
	
	@Test
	public void sphericalDistPropertiesTest() {
		final LatLongPoint p1 = new LatLongPoint(0.0, 0.0);
		final LatLongPoint p2 = new LatLongPoint(1.0, 1.0);
		final LatLongPoint p3 = new LatLongPoint(3.0, 3.0);
		final LatLongPoint p4 = new LatLongPoint(-5.0, 5.0);
		
		// Reflexive
		assertTrue(p1.sphericalDistance(p1) == 0);
		assertTrue(p2.sphericalDistance(p2) == 0);
		
		// Symmetric
		assertTrue(p2.sphericalDistance(p1) == p1.sphericalDistance(p2));
		assertTrue(p4.sphericalDistance(p3) == p3.sphericalDistance(p4));
		
		// Transitive
		assertTrue(p1.sphericalDistance(p1) < p1.sphericalDistance(p2));
		assertTrue(p1.sphericalDistance(p2) < p1.sphericalDistance(p3));
		assertTrue(p1.sphericalDistance(p3) < p1.sphericalDistance(p4));
		assertTrue(p1.sphericalDistance(p2) < p1.sphericalDistance(p4));
	}
	
	@Test
	public void sphericalDistTest() {
		final LatLongPoint p1 = new LatLongPoint(87.10102, -130.420);
		final LatLongPoint p2 = new LatLongPoint(-12.13, 49.802);
		final double dist = p1.sphericalDistance(p2);
		assertTrue(dist > 11677000 && dist < 11682000);
	}
	
	@Test
	public void flatDistTest() {
		final LatLongPoint p1 = new LatLongPoint(87.10102, -130.420);
		final LatLongPoint p2 = new LatLongPoint(-12.13, 49.802);
		final double dist = p1.flatDistance(p2);
		assertTrue(dist == 205.73469472658323);// Checked against Wolfram Alfa
		assertTrue(dist == p1.distance(p2));
		
		final LatLongPoint p3 = new LatLongPoint(-87.10102, -130.420);
		final LatLongPoint p4 = new LatLongPoint(-12.13, -49.802);
		assertTrue(p3.flatDistance(p4) == p3.distance(p4));
		
		final LatLongPoint p5 = new LatLongPoint(82.12, 113.420);
		final LatLongPoint p6 = new LatLongPoint(3.13, 9.802);
		assertTrue(p5.flatDistance(p6) == p5.distance(p6));
	}
}
