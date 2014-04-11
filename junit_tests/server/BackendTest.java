package server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import graph.GraphTest;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import server.autocorrect.ACController;
import server.io.IOController;
import server.kdtree.KDTreeController;
import data.MapException;

@SuppressWarnings("static-method")
public class BackendTest {
	
	private static final String	WAYS	= GraphTest.DATA_FOLDER + "/ways.tsv";
	private static final String	NODES	= GraphTest.DATA_FOLDER + "/nodes.tsv";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}
	
	@Test
	public void emptyIndexFileTest() {
		IOController.tearDown();
		try {
			IOController.setup(WAYS, NODES, "info/empty/index.tsv");
			assertTrue(IOController.getAllWayNames().isEmpty());
			final ACController ac = new ACController();
			assertTrue(ac.suggest("Test").isEmpty());
			final KDTreeController kd = new KDTreeController();
			IOController.findIntersection("Thayer Street", "Cushing Street");
			assertFalse(kd != null);
			fail("Should have thrown MapException");
		} catch (MapException | IOException e) {
			assertTrue(true);
		}
		
	}
	
}
