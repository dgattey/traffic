package backend.kdtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;

/**
 * This class represents the KDTree and contains logic to enable nearest neighbors<br>
 * TODO Change treeset to hashset (the reason treeset was being used was that hashset wasn't catching duplicates might
 * be fixed if T overrides hashCode()?
 * 
 * @author aiguha
 * @param <T> a comparable type
 */
public class KDTree<T extends KComparer<T>> {
	
	private TreeNode<T>					root;
	private final int					k;
	private final List<Comparator<T>>	comparators;
	private final TreeSet<T>			pointSet;
	
	/**
	 * Constructs a KDTree
	 * 
	 * @param k an int for the number of dimensions
	 * @param points this is a list because we need to check getValidDimensions(). Could be avoided if the method was
	 *            static but interfaces (KComparer) do not allow static methods
	 * @param comps list of comparaotrs for this tree
	 * @throws KDTreeException if something was null or otherwise unusable
	 */
	public KDTree(final int k, final List<T> points, final List<Comparator<T>> comps) throws KDTreeException {
		if (points == null) {
			throw new KDTreeException("KDTree: cannot initialize with null points list");
		}
		if (comps == null) {
			throw new KDTreeException("KDTree: cannot initialize with null comparator list");
		}
		if (k < 1) {
			throw new KDTreeException("KDTree: must have positive dimensions");
		}
		if (k != comps.size()) {
			throw new KDTreeException("KDTree: must provide comparators for each dimension");
		}
		
		if (!points.isEmpty() && k != points.get(0).getValidDimensions()) {
			throw new KDTreeException("KDTree: tree dimension must match that of contained type");
		}
		this.k = k;
		this.comparators = comps;
		this.pointSet = new TreeSet<>(points);// Removes Duplicates
		constructTree(new ArrayList<>(pointSet));
	}
	
	public int getK() {
		return k;
	}
	
	public TreeNode<T> getRoot() {
		return root;
	}
	
	public void setRoot(final TreeNode<T> root) {
		this.root = root;
	}
	
	/**
	 * Calculates the distance between two points
	 * 
	 * @param first a distance
	 * @param second another distance
	 * @return the absolute distance between first and second
	 */
	double distanceBetween(final T first, final T second) {
		final double[] firstc = first.getCoords();
		final double[] secondc = second.getCoords();
		// Uses distance formula to calculate distance between first and second
		double dist = 0;
		for (int i = 0; i < k; i++) {
			dist += Math.pow((firstc[i] - secondc[i]), 2);
		}
		return Math.abs(Math.sqrt(dist));
	}
	
	/**
	 * Compares first and second on a particular axis
	 * 
	 * @param first a distance
	 * @param second another distance
	 * @param axis the axis to be compared on
	 * @return the difference between first and second's coordinates on that axis
	 */
	private double compareOnAxis(final T first, final T second, final int axis) {
		return (first.getCoords()[axis] - second.getCoords()[axis]);
	}
	
	/**
	 * Calculates the median index of the sortedList
	 * 
	 * @param sortedList the sorted list
	 * @param axis the current axis of comparison
	 * @return the index of the median
	 * @throws KDTreeException if something was wrong internally
	 */
	private int calcMedianIndex(final List<T> sortedList, final int axis) throws KDTreeException {
		if (axis >= comparators.size() || sortedList == null || sortedList.size() == 0) {
			throw new KDTreeException("KDTree Internal Error in KDTree. Exiting.");
		}
		boolean done = false;
		int med = sortedList.size() / 2;
		// Divide into less than and greater than and equal to
		while (!done && med > 0) {
			if (compareOnAxis(sortedList.get(med - 1), sortedList.get(med), axis) < 0) {
				done = true;
				break;
			}
			med--;
		}
		return med;
	}
	
	/**
	 * Performs the recursive tree construction
	 * 
	 * @param points the list of points to be added to the tree
	 * @param curDepth the current depth of the tree
	 * @return the root of the tree
	 * @throws KDTreeException if something was wrong internally
	 */
	private TreeNode<T> constructTreeHelper(final List<T> points, final int curDepth) throws KDTreeException {
		if (points.size() == 0) {
			return null;
		}
		final int curAxis = curDepth % k;
		Collections.sort(points, comparators.get(curAxis));
		final int medIndex = calcMedianIndex(points, curAxis);
		final T median = points.get(medIndex);
		
		final TreeNode<T> curNode = new TreeNode<>(curAxis, curDepth);
		curNode.setLocation(median);
		// Recursively build left and right subtrees
		curNode.left = constructTreeHelper(points.subList(0, medIndex), curDepth + 1);
		curNode.right = constructTreeHelper(points.subList(medIndex + 1, points.size()), curDepth + 1);
		return curNode;
	}
	
	/**
	 * Constructs Tree from given list
	 * 
	 * @param points a list of T to make into a tree
	 * @throws KDTreeException if something was wrong internally
	 */
	public void constructTree(final List<T> points) throws KDTreeException {
		if (points == null) {
			setRoot(null);
			return;
		}
		setRoot(constructTreeHelper(points, 0));
	}
	
	/**
	 * Performs recursive lookup
	 * 
	 * @param point the point to be looked up
	 * @param start the starting node
	 * @param depth the current depth
	 * @return the node containing the point or null
	 */
	private TreeNode<T> lookupHelper(final T point, final TreeNode<T> start, final int depth) {
		if (start == null) {
			return null;
		}
		if (start.getLocation().equals(point)) {
			return start;
		}
		final int curAxis = depth % k;
		if (compareOnAxis(start.getLocation(), point, curAxis) > 0) {
			return lookupHelper(point, start.left, depth + 1);
		}
		return lookupHelper(point, start.right, depth + 1);
	}
	
	/**
	 * Lookups point in the KDTree
	 * 
	 * @param point a point to look up
	 * @return The node if found containing point
	 */
	public TreeNode<T> lookup(final T point) {
		if (point == null) {
			return null;
		}
		final TreeNode<T> toReturn = lookupHelper(point, getRoot(), 0);
		return toReturn;
	}
	
	/**
	 * Creates a distance-from-center comparator
	 * 
	 * @param center the center point
	 * @return the comparator
	 */
	private Comparator<T> getDistComparator(final T center) {
		return new Comparator<T>() {
			
			@Override
			public int compare(final T o1, final T o2) {
				return Double.compare(distanceBetween(center, o1), distanceBetween(center, o2));
			}
		};
	}
	
	/**
	 * Updates the threshold depending on the current worst in the priority queue
	 * 
	 * @param npq the priority queue
	 * @param center the center point
	 * @param n the size of the PQ
	 * @return a new threshold for the worst
	 */
	private double updateThreshold(final PriorityQueue<T> npq, final T center, final int n) {
		// Bounds the priority queue at n
		while (npq.size() > n) {
			npq.poll();
		}
		return distanceBetween(center, npq.peek());
	}
	
	/**
	 * Adds the neighbor to the queue if its distance from the center is within the threshold
	 * 
	 * @param toggle true if n-nearest search, false if radius search
	 * @param curNode current node
	 * @param center the coordinates around which the neighbors are being searched for
	 * @param npq the neighbors priority queue
	 * @param n the number of neighbors to return
	 * @param threshold the distance threshold for neighbors
	 * @return a new threshold
	 */
	private double addNeighborToQueue(final boolean toggle, final TreeNode<T> curNode, final T center,
			final PriorityQueue<T> npq, final int n, double threshold) {
		if (center == curNode.getLocation()) {
			return threshold;
		}
		final double curDist = distanceBetween(curNode.getLocation(), center);
		// N-Nearest Neighbor
		if (toggle) {
			if (npq.size() < n || curDist < threshold) {
				npq.add(curNode.getLocation());
			}
			threshold = updateThreshold(npq, center, n);
		}
		// Radius Search
		else {
			if (curDist <= threshold) {
				npq.add(curNode.getLocation());
			}
		}
		
		return threshold;
	}
	
	/**
	 * A recursive helper to find the nearest neighbors
	 * 
	 * @param toggle true if n-nearest search, false if radius search
	 * @param curNode the current node of the recursive search
	 * @param center the point around which neighbors are to be found
	 * @param neighbors the growing list of neighbors
	 * @param n the number of neighbors to be found (-1 if radius search)
	 * @param threshold the max distance of neighbors to be added
	 */
	private void findNeighbors(final boolean toggle, final TreeNode<T> curNode, final T center,
			final PriorityQueue<T> neighbors, final int n, double threshold) {
		if (curNode == null || Thread.currentThread().isInterrupted()) {
			return;
		}
		// Updates the threshold if needed (n-nearest search)
		threshold = addNeighborToQueue(toggle, curNode, center, neighbors, n, threshold);
		final int curAxis = curNode.getAxis();
		final double distToPlane = compareOnAxis(curNode.getLocation(), center, curAxis);
		final boolean leftFirst = distToPlane >= 0;
		// Decides whether to check the other branch of the tree
		final boolean crossPlane = (toggle && neighbors.size() < n) || (Math.abs(distToPlane) <= Math.abs(threshold));
		
		final TreeNode<T> first = (leftFirst) ? curNode.left : curNode.right;
		final TreeNode<T> second = (leftFirst) ? curNode.right : curNode.left;
		
		findNeighbors(toggle, first, center, neighbors, n, threshold);
		if (crossPlane) {
			findNeighbors(toggle, second, center, neighbors, n, threshold);
		}
	}
	
	/**
	 * Returns n nearest neighbors of point center
	 * 
	 * @param n the number of nearest neighbors
	 * @param center the center point
	 * @return list of nearest neighbor points
	 */
	public List<T> nNearestNeighbors(final int n, final T center) {
		final ArrayList<T> neighbors = new ArrayList<>();
		if (root == null || n < 0) {
			return null;
		}
		if (n == 0) {
			return neighbors;
		}
		// Cannot construct a priority queue of arbitrarily large initial capacity
		final int maxCapacity = (pointSet.size() > n) ? n : pointSet.size();
		final PriorityQueue<T> npq = new PriorityQueue<>(maxCapacity,
				Collections.reverseOrder(getDistComparator(center)));
		findNeighbors(true, root, center, npq, n, Double.MAX_VALUE);
		neighbors.addAll(npq);
		Collections.sort(neighbors, getDistComparator(center));
		final int toIndex = (neighbors.size() < n) ? neighbors.size() : n;
		return neighbors.subList(0, toIndex);
	}
	
	/**
	 * Returns all neighbors of point center within radius
	 * 
	 * @param radius the radius to find
	 * @param center the center point
	 * @return a list of neighbors within radius
	 */
	public List<T> radiusNeighbors(final double radius, final T center) {
		if (root == null || radius < 0) {
			return null;
		}
		final ArrayList<T> neighbors = new ArrayList<>();
		final Comparator<T> distComp = getDistComparator(center);
		final PriorityQueue<T> npq = new PriorityQueue<>(16, distComp);
		findNeighbors(false, root, center, npq, 0, radius);
		neighbors.addAll(npq);
		Collections.sort(neighbors, distComp);
		return neighbors;
		
	}
	
	/**
	 * Naively gets all neighbors in the tree Does not remove the point itself
	 * 
	 * @param center the center point
	 * @return a list of all neighbor points sorted in increasing distance from the center
	 */
	private List<T> getNeighborsNaive(final T center) {
		final Comparator<T> distComp = getDistComparator(center);
		final List<T> allNeighbors = new ArrayList<>(pointSet);
		Collections.sort(allNeighbors, distComp);
		return allNeighbors;
		
	}
	
	/**
	 * Performs a Naive Neighbors Search (for testing purposes)
	 * 
	 * @param n the number of neighbors to be found
	 * @param center the point around which to find neighbors
	 * @param removeCenter boolean to specify whether to not consider the star itself as a neighbor
	 * @return the list of n neighbors
	 */
	public List<T> naiveNN(final int n, final T center, final boolean removeCenter) {
		if (root == null || n < 0 || center == null) {
			return null;
		}
		if (n == 0) {
			return new ArrayList<>();
		}
		
		final List<T> neighbors = getNeighborsNaive(center);
		// Simulates the constraint of not considering the star its own neighbor
		if (removeCenter) {
			neighbors.remove(center);
		}
		final int toIndex = (neighbors.size() < n) ? neighbors.size() : n;
		return neighbors.subList(0, toIndex);
	}
	
	/**
	 * Performs a Naive Range Neighbor Search (for testing purposes)
	 * 
	 * @param radius the radius within which neighbors should be returned
	 * @param center the point around which to search for neighbors
	 * @param removeCenter boolean to specify whether to not consider the star itself as a neighbor
	 * @return the list of neighbors within the radius from the center
	 */
	public List<T> naiveRange(final double radius, final T center, final boolean removeCenter) {
		if (root == null || radius < 0 || center == null) {
			return null;
		}
		if (radius == 0) {
			return new ArrayList<>();
		}
		final List<T> allNeighbors = getNeighborsNaive(center);
		final List<T> validNeighbors = new ArrayList<>();
		// Simulates the constraint of not considering the star its own neighbor
		if (removeCenter) {
			allNeighbors.remove(center);
		}
		for (final T n : allNeighbors) {
			if (distanceBetween(n, center) > radius) {
				break;
			}
			validNeighbors.add(n);
		}
		return validNeighbors;
	}
	
}
