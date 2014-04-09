package io;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import graph.GraphTest;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.io.BinaryFileSearcher;
import server.io.DataSetException;
import server.io.IOController;
import data.LatLongPoint;
import data.MapException;
import data.MapWay;

@SuppressWarnings("static-method")
public class IOTest {
	
	private static final String	WAYS	= GraphTest.DATA_FOLDER + "/ways.tsv";
	private static final String	NODES	= GraphTest.DATA_FOLDER + "/nodes.tsv";
	private static final String	INDEX	= GraphTest.DATA_FOLDER + "/index.tsv";
	
	@Before
	public void setUpBeforeClass() throws Exception {
		IOController.setup(WAYS, NODES, INDEX);
		IOController.getAllNodes();
	}
	
	@After
	public void tearDownAfterClass() {
		IOController.tearDown();
	}
	
	@Test
	public void blockBinarySearchTest() {
		try {
			final List<String> records1 = BinaryFileSearcher.findMatchingRecords(INDEX, "Thayer St", IOController
					.getIndexHeaderMap().get("name"), "\\t");
			assertTrue(records1.size() == 2);
			final List<String> records2 = BinaryFileSearcher.findMatchingRecords(INDEX, "Thayer Street", IOController
					.getIndexHeaderMap().get("name"), "\\t");
			assertTrue(records2.size() == 3);
			final List<String> records3 = BinaryFileSearcher.findMatchingRecords(INDEX, "Cushing Street", IOController
					.getIndexHeaderMap().get("name"), "\\t");
			assertTrue(records3.size() == 6);
		} catch (DataSetException | IOException e) {
			fail("Threw Exception");
		}
	}
	
	@Test
	public void intersectionTest() {
		try {
			
			assertTrue(IOController.findIntersection("Thayer Street", "Cushing Street").getID()
					.equals("/n/4183.7140.201160091"));
			assertTrue(IOController.findIntersection("Thayer Street", "Waterman Street").getID()
					.equals("/n/4182.7140.1955940297"));
		} catch (MapException | IOException e) {
			fail("Threw Exception");
		}
	}
	
	@Test
	public void testChunking() {
		final LatLongPoint p1 = new LatLongPoint(41.8, 71.34);
		final LatLongPoint p2 = new LatLongPoint(41.81, 71.35);
		try {
			final List<MapWay> chunk = IOController.getChunkOfWays(p1, p2);
			assertTrue(chunk != null);
			assertTrue(chunk.size() == 1555);
		} catch (DataSetException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void idConversionTests() {
		assertTrue(IOController.firstFourDigits(4183.0).equals("4183"));
		assertTrue(IOController.firstFourDigits(4185.6).equals("4185"));
		assertTrue(IOController.firstFourDigits(4381.1234123423).equals("4381"));
		assertTrue(IOController.firstFourDigits(41.8).equals("4180"));
		assertTrue(IOController.firstFourDigits(41.81).equals("4181"));
		assertTrue(IOController.firstFourDigits(71.34).equals("7134"));
		assertTrue(IOController.firstFourDigits(71.35).equals("7135"));
		
	}
	
}
