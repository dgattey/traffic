package io;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import hub.LatLongPoint;
import hub.MapWay;
import hub.MapsException;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import backend.io.BinaryFileSearcher;
import backend.io.DataSetException;
import backend.io.IOController;

@SuppressWarnings("static-method")
public class IOTest {
	
	@Before
	public void setUpBeforeClass() throws Exception {
		IOController.setup("data/ways.tsv", "data/nodes.tsv", "data/index.tsv");
		IOController.getAllNodes();
	}
	
	@After
	public void tearDownAfterClass() {
		IOController.tearDown();
	}
	
	@Test
	public void blockBinarySearchTest() {
		try {
			final List<String> records1 = BinaryFileSearcher.findMatchingRecords("data/index.tsv", "Thayer St",
					IOController.getIndexHeaderMap().get("name"), "\\t");
			assertTrue(records1.size() == 2);
			final List<String> records2 = BinaryFileSearcher.findMatchingRecords("data/index.tsv", "Thayer Street",
					IOController.getIndexHeaderMap().get("name"), "\\t");
			assertTrue(records2.size() == 3);
			final List<String> records3 = BinaryFileSearcher.findMatchingRecords("data/index.tsv", "Cushing Street",
					IOController.getIndexHeaderMap().get("name"), "\\t");
			assertTrue(records3.size() == 6);
		} catch (DataSetException | IOException e) {
			// TODO Auto-generated catch block
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
		} catch (MapsException | IOException e) {
			fail("Threw Exception");
		}
	}
	
	@Test
	public void latLongStringConverterTest() {
		assertTrue(IOController.firstFourDigits(4183.0).equals("4183"));
	}
	
	@Test
	public void testChunking() {
		final LatLongPoint p1 = new LatLongPoint(41.8, 71.34);
		final LatLongPoint p2 = new LatLongPoint(41.81, 71.35);
		try {
			final List<MapWay> chunk = IOController.getChunkOfWays(p1, p2);
			
		} catch (DataSetException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void idConversionTests() {
		assertTrue(IOController.firstFourDigits(41.8).equals("4180"));
		assertTrue(IOController.firstFourDigits(41.81).equals("4181"));
		assertTrue(IOController.firstFourDigits(71.34).equals("7134"));
		assertTrue(IOController.firstFourDigits(71.35).equals("7135"));
		
	}
	
}
