package io;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import backend.io.BinaryFileSearcher;
import backend.io.DataSetException;
import backend.io.IOController;

@SuppressWarnings("static-method")
public class BinaryFileSearcherTest {
	
	@Before
	public void setUp() throws Exception {
		IOController.setup("data/ways.tsv", "data/nodes.tsv", "data/index.tsv");
	}
	
	@After
	public void tearDownAfterClass() {
		IOController.tearDown();
	}
	
	@Test
	public void partialSearchingTest() {
		
		try {
			final RandomAccessFile r = new RandomAccessFile("data/ways.tsv", "r");
			final String record = BinaryFileSearcher.binarySearchHelper(r, "/w/4170.7125", IOController
					.getWaysHeaderMap().get("id"), "\\t", true);
			assertTrue(record != null);
			assertTrue(r.readLine().startsWith("/w/4170.7125"));
			r.close();
		} catch (DataSetException | IOException e) {
			// TODO Auto-generated catch block
			fail("Threw Exception");
		}
	}
	
	@Test
	public void pagingTest() {
		try {
			
			final List<String> result = (BinaryFileSearcher.getPage("data/ways.tsv", "/w/4170.7125", "/w/4170.7126",
					IOController.getWaysHeaderMap().get("id"), "\\t"));
			assertTrue(result != null);
			assertTrue(result.get(0).contains("/w/4170.7125"));
			assertTrue(result.get(result.size() / 2).contains("/w/4170.7125")
					|| result.get(result.size() / 2).contains("/w/4170.7126"));
			assertTrue(result.get(result.size() - 1).contains("/w/4170.7126"));
			
			final List<String> result2 = (BinaryFileSearcher.getPage("data/ways.tsv", "/w/4170.7125", "/w/4170.7127",
					IOController.getWaysHeaderMap().get("id"), "\\t"));
			assertTrue(result2 != null);
			assertTrue(result2.get(0).contains("/w/4170.7125"));
			assertTrue(result2.get(result2.size() / 2).contains("/w/4170.7125")
					|| result2.get(result2.size() / 2).contains("/w/4170.7126")
					|| result2.get(result2.size() / 2).contains("/w/4170.7127"));
			assertTrue(result2.get(result2.size() - 1).contains("/w/4170.7127"));
			
			// Top of the file
			final List<String> result3 = (BinaryFileSearcher.getPage("data/ways.tsv", "/w/4015.7374", "/w/4015.7375",
					IOController.getWaysHeaderMap().get("id"), "\\t"));
			assertTrue(result3 != null);
			assertTrue(result3.get(0).contains("/w/4015.7374"));
			assertTrue(result3.size() == 1);
			
			// Just above bottom of the file
			final List<String> result4 = (BinaryFileSearcher.getPage("data/ways.tsv", "/w/4208.7180", "/w/4208.7181",
					IOController.getWaysHeaderMap().get("id"), "\\t"));
			assertTrue(result4 != null);
			assertTrue(result4.get(0).contains("/w/4208.7180"));
			assertTrue(result4.size() == 4);
			
			// Bottom of file
			final List<String> result5 = (BinaryFileSearcher.getPage("data/ways.tsv", "/w/4209.7169", "/w/4209.7169",
					IOController.getWaysHeaderMap().get("id"), "\\t"));
			assertTrue(result5 != null);
			assertTrue(result5.get(0).contains("/w/4209.7169"));
			assertTrue(result5.size() == 1);
			
			// Problematic Chunks
			final List<String> result6 = (BinaryFileSearcher.getPage("data/ways.tsv", "/w/4176.7135", "/w/4176.7136",
					IOController.getWaysHeaderMap().get("id"), "\\t"));
			assertTrue(result6 != null);
			assertTrue(result6.get(0).contains("/w/4176.7135"));
			System.out.println("size of result was: " + result6.size());
			
			final List<String> result7 = (BinaryFileSearcher.getPage("data/ways.tsv", "/w/4175", "/w/4176",
					IOController.getWaysHeaderMap().get("id"), "\\t"));
			assertTrue(result7 != null);
			// assertTrue(result7.get(0).contains("/w/4175"));
			System.out.println("size of result was: " + result7.size());
			
			final List<String> result8 = (BinaryFileSearcher.getPage("data/ways.tsv", "/w/4180.7147", "/w/4180.7146",
					IOController.getWaysHeaderMap().get("id"), "\\t"));
			assertTrue(result8 != null);
			// assertTrue(result8.get(0).contains("/w/4175"));
			System.out.println("size of result was: " + result8.size());
			
		} catch (DataSetException | IOException e) {
			fail("Threw Exception");
		}
	}
	
}
