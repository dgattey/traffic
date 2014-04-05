package kdtree;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.io.IOController;
import server.kdtree.KDTreeController;
import data.LatLongPoint;
import data.MapException;

@SuppressWarnings("static-method")
public class KDTreeControllerTest {
	
	@Before
	public void setUp() throws Exception {
		IOController.setup("data/ways.tsv", "data/nodes.tsv", "data/index.tsv");
	}
	
	@After
	public void tearDownAfterClass() {
		IOController.tearDown();
	}
	
	@Test
	public void nearestNeighbors() {
		try {
			final KDTreeController c = new KDTreeController();
			final LatLongPoint p1 = new LatLongPoint(0, 0);
			assertTrue(c.getNearestNeighbors(1000, p1).equals(c.getNearestNaive(1000, p1)));
			
			final LatLongPoint p2 = new LatLongPoint(41.82, -71.40);
			assertTrue(c.getNearestNeighbors(1000, p2).equals(c.getNearestNaive(1000, p2)));
			
		} catch (IOException | MapException e) {
			fail("Threw Exception");
		}
	}
	
}
