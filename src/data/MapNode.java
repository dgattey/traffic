package data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import server.graph.Graphable;
import server.kdtree.KComparer;

/**
 * MapNode represents nodes in the data-set provided and because it implements KComparer and Graphable, it will be used
 * in the construction of KDtreeNodes as well as GraphNodes for nearest neighbor and path-finding purposes
 * 
 * @author Advik
 */
public class MapNode implements KComparer<MapNode>, Graphable<MapNode>, Comparable<MapNode> {
	
	private final String		id;
	private final List<String>	ways;
	private final LatLongPoint	p;
	private final double[]		coords;
	
	/**
	 * Makes a new MapNode to return statically
	 * 
	 * @param id the id of it
	 * @param p the point representing this thing in space
	 * @param ways the list of ways
	 * @return a new MapNode from given data
	 */
	public static MapNode create(final String id, final LatLongPoint p, final List<String> ways) {
		if (id == null || id.isEmpty() || p == null) {
			return null;
		}
		return new MapNode(id, p, ways);
	}
	
	/**
	 * Constructor for map node
	 * 
	 * @param id the id from file
	 * @param p the location in 2D space
	 * @param ways the list of ways
	 */
	private MapNode(final String id, final LatLongPoint p, final List<String> ways) {
		this.id = id;
		this.ways = (ways != null) ? ways : new ArrayList<String>();
		this.p = p;
		coords = new double[2];
		coords[0] = p.getLat();
		coords[1] = p.getLong();
	}
	
	public List<String> getWays() {
		return ways;
	}
	
	public double getLatitude() {
		return coords[0];
	}
	
	public double getLongitude() {
		return coords[1];
	}
	
	public LatLongPoint getPoint() {
		return p;
	}
	
	/**
	 * Graphable Methods
	 */
	
	@Override
	public String getName() {
		return getID();
	}
	
	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public boolean canConnectTo(final MapNode other) {
		return true;
	}
	
	/**
	 * KComparer Methods
	 */
	
	@Override
	public int getValidDimensions() {
		return coords.length;
	}
	
	@Override
	public double distanceFrom(final MapNode other) {
		return p.flatDistance(other.getPoint());
	}
	
	@Override
	public List<Comparator<MapNode>> getComparators() {
		final List<Comparator<MapNode>> comps = new ArrayList<>();
		for (int i = 0; i < coords.length; i++) {
			final int dim = i;
			comps.add(new Comparator<MapNode>() {
				
				@Override
				public int compare(final MapNode o1, final MapNode o2) {
					if (o1.compareOnDim(o2, dim) > 0) {
						return 1;
					} else if (o1.compareOnDim(o2, dim) < 0) {
						return -1;
					} else {
						return 0;
					}
				}
				
			});
		}
		return comps;
	}
	
	@Override
	public double[] getCoords() {
		return coords;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	@Override
	public double compareOnDim(final MapNode other, final int dim) {
		return (getCoords()[dim] - other.getCoords()[dim]);
	}
	
	@Override
	public int compareTo(final MapNode that) {
		// MapNodes with equal ids should always be considered equal
		if (getID().equals(that.getID())) {
			return 0;
		}
		// Arbitrarily chose comparison order
		final int latComp = Double.compare(getLatitude(), that.getLatitude());
		final int longComp = Double.compare(getLongitude(), that.getLongitude());
		if (latComp != 0) {
			return latComp;
		}
		return longComp;
		
	}
	
	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof MapNode)) {
			return false;
		}
		final MapNode o = (MapNode) other;
		final int comp = compareTo(o);
		return (comp == 0);
		
	}
	
	@Override
	public String toString() {
		final String holder = "(%s, %s)";
		return String.format(holder, getLatitude(), getLongitude());
	}
	
}
