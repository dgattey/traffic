package hub;

import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("static-method")
public class HubTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}
	
	@Test
	public void thousandFuckingHops() {
		final String data1 = "data/ways.tsv";
		final String data2 = "data/nodes.tsv";
		final String data3 = "data/index.tsv";
		
		/*
		 * final HubController hc = new HubController(data1, data2, data3, new REPLApp(data1, data2, data3, false));
		 * final MapNode start = hc.getNearestIntersection(new LatLongPoint(1234, -1234)); final MapNode end =
		 * hc.getNearestIntersection(new LatLongPoint(-1234, 1234));
		 */
		// assertTrue(hc.getRoute(start, end).size() > 1000);
		
	}
}
