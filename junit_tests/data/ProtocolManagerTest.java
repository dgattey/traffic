package data;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.BeforeClass;
import org.junit.Test;

public class ProtocolManagerTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}
	
	@Test
	public void parseLLPTest() {
		try {
			final String req = "#llp:41.72 -51.1234";
			final BufferedReader r = new BufferedReader(new StringReader(req));
			final LatLongPoint p = ProtocolManager.parseLatLongPoint(r);
			assertTrue(p != null);
			assertTrue(p.getLat() == 41.72);
			assertTrue(p.getLong() == -51.1234);
		} catch (final IOException | ParseException e) {
			fail("Threw Exception: " + e.getMessage());
		}
		try {
			final String req = "#llp:41.72 longitude";
			final BufferedReader r = new BufferedReader(new StringReader(req));
			final LatLongPoint p = ProtocolManager.parseLatLongPoint(r);
			fail("Should have thrown exception");
		} catch (final IOException | ParseException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void parseCMWTest() {
		try {
			final String req = "<way:\n" + "/w/00001\n" + "The Street\n" + "/n/0002\n" + "#llp:41.72 -51.12\n"
					+ "/n/0003\n" + "#llp:-1.1 -2.2\n" + ">\n";
			final BufferedReader r = new BufferedReader(new StringReader(req));
			final ClientMapWay m = ProtocolManager.parseClientMapWay(r);
			assertTrue(m != null);
			assertTrue(m.getID().equals("/w/00001"));
			assertTrue(m.getName().equals("The Street"));
			assertTrue(m.getStart().getPoint().getLat() == 41.72);
			assertTrue(m.getEnd().getPoint().getLong() == -2.2);
		} catch (final IOException | ParseException e) {
			fail("Threw Exception: " + e.getMessage());
		}
	}
}
