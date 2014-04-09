package server;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import server.core.ResponseController;
import data.MapException;
import data.ProtocolManager;

@SuppressWarnings("static-method")
public class ResponseControllerTest {
	
	ResponseController	res;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}
	
	@Before
	public void setUp() throws MapException, IOException {
		res = new ResponseController("info/ways.tsv", "info/nodes.tsv", "info/index.tsv", null);
	}
	
	@Test
	public void autocorrectTest() {
		try {
			final String req = "Thaye\n" + ProtocolManager.FOOTER;
			final BufferedReader r = new BufferedReader(new StringReader(req));
			final StringWriter w = new StringWriter();
			final String ans = "";
			res.respondAC(r, w);
			final String exp = "@r:ac:\n<list:string:7\nThayer Street\nThayer St\nThayer Court\nThayer Drive\nThayer Place\nThayer Road\nThayers Court\n>\n@x\n";
			assertTrue(w.toString().equals(exp));
			System.out.println("Response:\n" + w.toString());
		} catch (final IOException e) {
			fail("Threw Exception");
		}
	}
	
	@Test
	public void routeNamesTest() {
		try {
			final String req = "Thayer Street\nCushing Street\nThayer Street\nWaterman Street\n"
					+ ProtocolManager.FOOTER;
			final BufferedReader r = new BufferedReader(new StringReader(req));
			final StringWriter w = new StringWriter();
			final String ans = "";
			res.respondRS(r, w);
			System.out.println("Response:\n" + w.toString());
		} catch (final IOException e) {
			fail("Threw Exception");
		}
	}
	
}
