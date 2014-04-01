package autocorrect;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import server.autocorrect.trie.TrieTree;

public class TrieTest {
	
	TrieTree	t;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {}
	
	@Before
	public void setUp() throws Exception {}
	
	@After
	public void tearDown() throws Exception {}
	
	@Test
	public void findRoot() {
		t = new TrieTree();
		assertTrue(t.getSubTree("").getRoot() == t.getRoot());
	}
	
	@Test
	public void checkOneAdd() {
		t = new TrieTree();
		assertTrue(t.add("first"));
		assertTrue(t.isWord("first"));
	}
	
	@Test
	public void addNull() {
		t = new TrieTree();
		assertTrue(t.add(null) == false);
		assertTrue(t.add("") == false);
	}
	
	@Test
	public void basicInsert() {
		t = new TrieTree();
		assertTrue(t.add("first") == true);
		assertTrue(t.getRoot().getSuffixes().containsKey("f"));
		t.add("second");
		assertTrue(t.getRoot().getSuffixes().containsKey("s"));
	}
	
	@Test
	public void lookupBasic() {
		t = new TrieTree();
		assertTrue(t.query(null) == false);
		assertTrue(t.add("first"));
		assertTrue(t.query("first") == true);
		assertTrue(t.query("firs") == false);
	}
	
	@Test
	public void loadBasic() {
		t = new TrieTree();
		final ArrayList<String> dict = new ArrayList<>(Arrays.asList("a", "to", "tea", "ted", "ten", "i", "in", "inn"));
		t.addAll(dict);
		assertTrue(t.query("inn"));
		assertTrue(t.query("a"));
		assertTrue(t.query("tea"));
		assertTrue(t.query("ted"));
		assertTrue(t.query("ten"));
		assertTrue(t.query("i"));
		assertTrue(t.query("in"));
		assertTrue(t.query("dictionary") == false);
		assertTrue(t.query("innn") == false);
	}
	
}
