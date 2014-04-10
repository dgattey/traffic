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
			res.autocorrectResponse(r, w);
			final String exp = "@r:ac:\n<list:string:7\nThayer Street\nThayer St\nThayer Court\nThayer Drive\nThayer Place\nThayer Road\nThayers Court\n>\n@x\n";
			assertTrue(w.toString().equals(exp));
			assertTrue(w.toString().equals(
					"@r:ac:" + "\n" + "<list:string:7" + "\n" + "Thayer Street" + "\n" + "Thayer St" + "\n"
							+ "Thayer Court" + "\n" + "Thayer Drive" + "\n" + "Thayer Place" + "\n" + "Thayer Road"
							+ "\n" + "Thayers Court" + "\n" + ">" + "\n" + "@x" + "\n"));
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
			res.routeFromNamesResponse(r, w);
			assertTrue(w
					.toString()
					.equals("@r:rs:\n<list:way:9\n<way:\n/w/4183.7140.19383477.7.1\nThayer Street\n/n/4183.7140.201160091\n#llp:41.830032 -71.400902\n/n/4182.7140.201312088\n#llp:41.829166 -71.400787\n>\n<way:\n/w/4182.7140.19383477.8.1\nThayer Street\n/n/4182.7140.201312088\n#llp:41.829166 -71.400787\n/n/4182.7140.201284113\n#llp:41.828636 -71.400719\n>\n<way:\n/w/4182.7140.19383477.9.1\nThayer Street\n/n/4182.7140.201284113\n#llp:41.828636 -71.400719\n/n/4182.7140.201402708\n#llp:41.828533 -71.400703\n>\n<way:\n/w/4182.7140.19383477.10.1\nThayer Street\n/n/4182.7140.201402708\n#llp:41.828533 -71.400703\n/n/4182.7140.201554456\n#llp:41.828255 -71.400673\n>\n<way:\n/w/4182.7140.19383477.11.1\nThayer Street\n/n/4182.7140.201554456\n#llp:41.828255 -71.400673\n/n/4182.7140.201280063\n#llp:41.828098 -71.40065\n>\n<way:\n/w/4182.7140.19383477.12.1\nThayer Street\n/n/4182.7140.201280063\n#llp:41.828098 -71.40065\n/n/4182.7140.201554453\n#llp:41.8276851 -71.4005932\n>\n<way:\n/w/4182.7140.19383477.13.1\nThayer Street\n/n/4182.7140.201554453\n#llp:41.8276851 -71.4005932\n/n/4182.7140.446866082\n#llp:41.8276529 -71.400586\n>\n<way:\n/w/4182.7140.19383477.14.1\nThayer Street\n/n/4182.7140.446866082\n#llp:41.8276529 -71.400586\n/n/4182.7140.201554451\n#llp:41.8276083 -71.400576\n>\n<way:\n/w/4182.7140.19383477.15.1\nThayer Street\n/n/4182.7140.201554451\n#llp:41.8276083 -71.400576\n/n/4182.7140.1955940297\n#llp:41.827282 -71.400536\n>\n>\n@x\n"));
		} catch (final IOException e) {
			fail("Threw Exception");
		}
	}
	
}
