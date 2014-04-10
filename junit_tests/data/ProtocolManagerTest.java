package data;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map.Entry;

import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("static-method")
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
		LatLongPoint p = null;
		try {
			final String req = "#llp:41.72 longitude";
			final BufferedReader r = new BufferedReader(new StringReader(req));
			p = ProtocolManager.parseLatLongPoint(r);
			fail("Should have thrown exception");
		} catch (final IOException | ParseException e) {
			assertTrue(p == null);
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
	
	@Test
	public void parseReqHeaderTest() {
		try {
			final String req = ProtocolManager.Q_TR + "00001";
			final BufferedReader r = new BufferedReader(new StringReader(req));
			final Pair<String, String> p = ProtocolManager.parseRequestHeader(r);
			assertTrue(p.getLeft().equals(ProtocolManager.Q_TR));
			assertTrue(p.getRight().equals("00001"));
			
		} catch (final IOException e) {
			fail("Threw Exception: " + e.getMessage());
		}
		try {
			final String req = ProtocolManager.Q_RS;
			final BufferedReader r = new BufferedReader(new StringReader(req));
			final Pair<String, String> p = ProtocolManager.parseRequestHeader(r);
			assertTrue(p.getLeft().equals(ProtocolManager.Q_RS));
			assertTrue(p.getRight().isEmpty());
			
		} catch (final IOException e) {
			fail("Threw Exception: " + e.getMessage());
		}
	}
	
	@Test
	public void parseTrafficTest() {
		final String t = "Thayer Street\t0.05";
		final Entry<String, Double> e = ProtocolManager.parseTrafficData(t);
		assertTrue(e.getKey().equals("Thayer Street"));
		assertTrue(e.getValue() == 0.05);
	}
	
	@Test
	public void parseStreetListTest() {
		final String list = "<list:string:2\nThayer Street\nCushing Street\n>\n";
		try {
			final List<String> l = ProtocolManager.parseStreetList(new BufferedReader(new StringReader(list)));
			assertTrue(l.size() == 2);
			assertTrue(l.get(0).equals("Thayer Street"));
			assertTrue(l.get(1).equals("Cushing Street"));
		} catch (ParseException | IOException e) {
			fail("Threw Exception");
		}
	}
}
