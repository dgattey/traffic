package hub;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests intersection of lat long points
 * 
 * @author dgattey
 */
public class LatLongIntersectionTest {
	
	/**
	 * Tests two different small boxes starting at the same point
	 */
	@Test
	public void basicIntersection() {
		final LatLongPoint a = new LatLongPoint(0, 0);
		final LatLongPoint b = new LatLongPoint(10, 10);
		final LatLongPoint c = new LatLongPoint(0, 0);
		final LatLongPoint d = new LatLongPoint(5, 5);
		assertTrue(LatLongPoint.intersectChunk(a, b, c, d));
		assertTrue(LatLongPoint.intersectChunk(c, d, a, b));
		assertTrue(LatLongPoint.intersectChunk(d, c, b, a));
		assertTrue(LatLongPoint.intersectChunk(c, d, b, a));
		assertTrue(LatLongPoint.intersectChunk(d, c, a, b));
	}
	
	/**
	 * Tests the same box against itself both ways
	 */
	@Test
	public void sameIntersection() {
		final LatLongPoint a = new LatLongPoint(-5, 0);
		final LatLongPoint b = new LatLongPoint(3, 40);
		assertTrue(LatLongPoint.intersectChunk(a, b, a, b));
		assertTrue(LatLongPoint.intersectChunk(b, a, b, a));
	}
	
	/**
	 * Tests non-intersections
	 */
	@Test
	public void nonIntersection() {
		final LatLongPoint a = new LatLongPoint(-5, 0);
		final LatLongPoint b = new LatLongPoint(5, 10);
		final LatLongPoint c = new LatLongPoint(-200, 900);
		final LatLongPoint d = new LatLongPoint(-300, 600);
		assertFalse(LatLongPoint.intersectChunk(a, b, c, d));
		assertFalse(LatLongPoint.intersectChunk(a, b, d, c));
		assertFalse(LatLongPoint.intersectChunk(b, a, c, d));
		assertFalse(LatLongPoint.intersectChunk(b, a, d, c));
	}
	
	/**
	 * Tests non-intersections
	 */
	@Test
	public void realisticNonIntersection() {
		final LatLongPoint a = new LatLongPoint(38.236, 50.329);
		final LatLongPoint b = new LatLongPoint(57.734, 51.712);
		final LatLongPoint c = new LatLongPoint(58.0, 61.343);
		final LatLongPoint d = new LatLongPoint(65.8, 67.232);
		assertFalse(LatLongPoint.intersectChunk(a, b, c, d));
		assertFalse(LatLongPoint.intersectChunk(a, b, d, c));
		assertFalse(LatLongPoint.intersectChunk(b, a, c, d));
		assertFalse(LatLongPoint.intersectChunk(b, a, d, c));
	}
	
	/**
	 * Tests non-intersections
	 */
	@Test
	public void realisticXIntersection() {
		final LatLongPoint a = new LatLongPoint(38.236, 50.329);
		final LatLongPoint b = new LatLongPoint(57.734, 51.712);
		final LatLongPoint c = new LatLongPoint(56.999, 61.343);
		final LatLongPoint d = new LatLongPoint(65.8, 67.232);
		assertFalse(LatLongPoint.intersectChunk(a, b, c, d));
		assertFalse(LatLongPoint.intersectChunk(a, b, d, c));
		assertFalse(LatLongPoint.intersectChunk(b, a, c, d));
		assertFalse(LatLongPoint.intersectChunk(b, a, d, c));
	}
	
	/**
	 * Tests non-intersections
	 */
	@Test
	public void realisticYIntersection() {
		final LatLongPoint a = new LatLongPoint(38.236, 50.329);
		final LatLongPoint b = new LatLongPoint(57.734, 51.712);
		final LatLongPoint c = new LatLongPoint(58.0, 50.363);
		final LatLongPoint d = new LatLongPoint(65.8, 67.232);
		assertFalse(LatLongPoint.intersectChunk(a, b, c, d));
		assertFalse(LatLongPoint.intersectChunk(a, b, d, c));
		assertFalse(LatLongPoint.intersectChunk(b, a, c, d));
		assertFalse(LatLongPoint.intersectChunk(b, a, d, c));
	}
	
	/**
	 * Tests non-intersections
	 */
	@Test
	public void realisticIntersection() {
		final LatLongPoint a = new LatLongPoint(38.236, 50.329);
		final LatLongPoint b = new LatLongPoint(57.734, 51.712);
		final LatLongPoint c = new LatLongPoint(54.230, 50.343);
		final LatLongPoint d = new LatLongPoint(65.8, 67.232);
		assertTrue(LatLongPoint.intersectChunk(a, b, c, d));
		assertTrue(LatLongPoint.intersectChunk(a, b, d, c));
		assertTrue(LatLongPoint.intersectChunk(b, a, c, d));
		assertTrue(LatLongPoint.intersectChunk(b, a, d, c));
	}
	
}
