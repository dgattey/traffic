package server;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import server.core.Server;

import data.MapException;

public class ServerTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}
	
	@Test
	public void invalidInit() {
		try {
			final Server s = new Server("info/ways.tsv", "info/nodes.tsv", "info/index.tsv", null, 0, 0);
			fail("Should have thrown exception");
		} catch (final IllegalArgumentException e) {
			assertTrue(true);
		} catch (IOException | MapException e) {
			fail("Should not have thrown these exception");
		}
	}
	
	@Test
	public void validInit() {
		try {
			final Server s = new Server("info/ways.tsv", "info/nodes.tsv", "info/index.tsv", "traffic-server", 3011,
					8080);
			assertTrue(s != null);
		} catch (IOException | MapException e) {
			fail("Should not have thrown these exception");
		}
	}
}
