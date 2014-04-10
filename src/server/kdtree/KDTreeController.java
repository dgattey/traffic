package server.kdtree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import server.io.IOController;
import data.LatLongPoint;
import data.MapException;
import data.MapNode;

/**
 * This class allows interaction with the KDTree<br>
 * 
 * @author Advik
 */
public class KDTreeController {
	
	private Collection<MapNode>	mapNodeSet; // Is a set by invariant (check parser)
	private KDTree<MapNode>		tree;
	
	/**
	 * Initializes a KDTree Controller
	 * 
	 * @throws IOException thrown internally
	 * @throws MapException thrown internally
	 */
	public KDTreeController() throws IOException, MapException {
		try {
			mapNodeSet = IOController.getAllNodes().values();
			final List<MapNode> mapNodesList = new ArrayList<>(mapNodeSet);
			if (mapNodesList.isEmpty()) {
				throw new MapException("<KDTree Controller> Empty dataset. Cannot construct KDTree");
			}
			tree = new KDTree<>(2, mapNodesList, mapNodesList.get(0).getComparators());
		} catch (final KDTreeException e) {
			throw new MapException(e.getMessage());
		}
	}
	
	/**
	 * Gets the n nearest neighbors to the given coords
	 * 
	 * @param n the number of neighbors to return
	 * @param coords the coordinates array
	 * @return the list of neighbors
	 */
	public List<MapNode> getNearestNeighbors(final int n, final double[] coords) {
		if (coords.length != tree.getK()) {
			return null;
		}
		final LatLongPoint p = new LatLongPoint(coords[0], coords[1]);
		return getNearestNeighbors(n, p);
	}
	
	/**
	 * Gets the nearest neighbors given a LatLongPoint
	 * 
	 * @param n number of neighbors to find
	 * @param p the latlongpoint
	 * @return the list of up to n neighbors
	 */
	public List<MapNode> getNearestNeighbors(final int n, final LatLongPoint p) {
		final String searchID = Double.toString(-Double.MAX_VALUE);
		final MapNode center = MapNode.create(searchID, p, null);
		return tree.nNearestNeighbors(n, center);
		
	}
	
	/**
	 * Gets the nearest neighbor
	 * 
	 * @param p the latlongpoint
	 * @return the nearest neighbor or null
	 */
	public MapNode getNeighbor(final LatLongPoint p) {
		final List<MapNode> neighbors = getNearestNeighbors(1, p);
		if (neighbors == null || neighbors.isEmpty()) {
			return null;
		}
		return neighbors.get(0);
	}
	
	/**
	 * Naive nearest neighbor search
	 * 
	 * @param n the number of neighbors to find
	 * @param p the lat long point to search around
	 * @return the list of nearest neighbors
	 */
	public List<MapNode> getNearestNaive(final int n, final LatLongPoint p) {
		final String searchID = Double.toString(-Double.MAX_VALUE);
		final MapNode center = MapNode.create(searchID, p, null);
		return tree.naiveNN(n, center, false);
	}
	
}
