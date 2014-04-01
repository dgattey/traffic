package kdtree;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import data.LatLongPoint;
import data.MapNode;
import server.kdtree.KDTree;
import server.kdtree.KDTreeException;

@SuppressWarnings("static-method")
public class KDTreeTest {
	
	@Test
	public void invalidInit1() {
		try {
			new KDTree<MapNode>(3, null, null);
			fail("Should have thrown an Exception");
		} catch (final KDTreeException e) {
			assertTrue(e.getMessage().contains("null"));
		}
	}
	
	@Test
	public void invalidInit2() {
		try {
			new KDTree<MapNode>(3, new ArrayList<MapNode>(), null);
			fail("Should have thrown an Exception");
		} catch (final KDTreeException e) {
			assertTrue(e.getMessage().contains("null"));
		}
	}
	
	@Test
	public void validInitEmptyList() {
		final MapNode dummy = MapNode.create("0", new LatLongPoint(0.0, 0.0), null);
		final List<Comparator<MapNode>> comps = dummy.getComparators();
		KDTree<MapNode> t = null;
		try {
			t = new KDTree<>(2, new ArrayList<MapNode>(), comps);
		} catch (final KDTreeException e) {
			fail("KDTree threw exception");
		}
		assertTrue(t.getRoot() == null);
	}
	
	@Test
	public void oneElementTree() {
		final MapNode dummy = MapNode.create("0", new LatLongPoint(0.0, 0.0), null);
		final List<MapNode> points = new ArrayList<>();
		points.add(dummy);
		final List<Comparator<MapNode>> comps = dummy.getComparators();
		KDTree<MapNode> t = null;
		try {
			t = new KDTree<>(dummy.getValidDimensions(), points, comps);
		} catch (final KDTreeException e) {
			fail("KDTree threw exception");
		}
		assertTrue(t.getRoot().getLocation() == dummy);
	}
	
	@Test
	public void simpleTree() {
		KDTree<MapNode> t = null;
		final MapNode m1 = MapNode.create("0", new LatLongPoint(0.0, 0.0), null);
		final MapNode m2 = MapNode.create("1", new LatLongPoint(1.0, 1.0), null);
		final MapNode m3 = MapNode.create("2", new LatLongPoint(-2.0, -2.0), null);
		final List<MapNode> l = new ArrayList<>();
		l.add(m1);
		l.add(m2);
		l.add(m3);
		try {
			t = new KDTree<>(m1.getValidDimensions(), l, m1.getComparators());
		} catch (final KDTreeException e) {
			fail("KDTree threw exception");
		}
		assertTrue(t.getRoot() != null);
		assertTrue(t.getRoot().getLocation() == m1);
		assertTrue(t.lookup(m1).getLocation() == m1);
		assertTrue(t.lookup(m2).getLocation() == m2);
		assertTrue(t.lookup(m3).getLocation() == m3);
	}
	
	@Test
	public void addDuplicatesToTree() {
		KDTree<MapNode> t = null;
		final MapNode m1 = MapNode.create("0", new LatLongPoint(0.0, 0.0), null);
		final MapNode m2 = MapNode.create("1", new LatLongPoint(1.0, 1.0), null);
		final MapNode m3 = MapNode.create("2", new LatLongPoint(-2.0, -2.0), null);
		final MapNode m4 = MapNode.create("3", new LatLongPoint(0.0, 0.0), null);// Different IDs, same coords
		final List<MapNode> l = new ArrayList<>();
		l.add(m1);
		l.add(m2);
		l.add(m3);
		l.add(m4);
		try {
			t = new KDTree<>(m1.getValidDimensions(), l, m1.getComparators());
		} catch (final KDTreeException e) {
			fail("KDTree threw exception");
		}
		assertTrue(t.getRoot() != null);
		assertTrue(t.getRoot().getLocation() == m1);
		assertTrue(t.lookup(m1).getLocation() == m1);
		assertTrue(t.lookup(m2).getLocation() == m2);
		assertTrue(t.lookup(m3).getLocation() == m3);
		assertTrue(t.lookup(m4).getLocation() == m1);
	}
	
	@Test
	public void simpleNearestNeighbor() {
		try {
			final MapNode m1 = MapNode.create("0", new LatLongPoint(0.0, 0.0), null);
			final MapNode m2 = MapNode.create("1", new LatLongPoint(3.0, 3.0), null);
			final MapNode m3 = MapNode.create("2", new LatLongPoint(-5.0, 5.0), null);
			final ArrayList<MapNode> list = new ArrayList<MapNode>();
			list.add(m1);
			list.add(m2);
			list.add(m3);
			final KDTree<MapNode> t = new KDTree<MapNode>(m1.getValidDimensions(), list, list.get(0).getComparators());
			final MapNode searchVal = MapNode.create("-99", new LatLongPoint(1.0, 1.0), null);
			assertTrue(t.nNearestNeighbors(1, searchVal).get(0) == m1);
			assertTrue(t.nNearestNeighbors(2, searchVal).get(1) == m2);
			assertTrue(t.nNearestNeighbors(3, searchVal).get(2) == m3);
			assertTrue(t.nNearestNeighbors(4, searchVal).size() == 3);
		} catch (final KDTreeException e) {
			fail("Threw exception");
		}
	}
	
}
