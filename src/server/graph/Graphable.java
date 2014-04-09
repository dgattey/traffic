package server.graph;

/**
 * Classes implementing this interface can be used as "values" within GraphNodes in this package's graph implementation
 * 
 * @author aiguha
 * @param <T> the node type
 */
public interface Graphable<T> {
	
	/**
	 * T must have a non-null, non-empty name
	 * 
	 * @return the name
	 */
	public String getName();
	
	/**
	 * T must have a non-null, unique, non-empty UID
	 * 
	 * @return the id
	 */
	public String getID();
	
	/**
	 * Specifies conditions for having edges connecting GraphNodes containing T
	 * 
	 * @param other the other T
	 * @return true, if this object and other are valid neighbors in the graph
	 */
	public boolean canConnectTo(T other);
	
	/**
	 * Returns spatial representation of Graphable objects Should return null if objects have no spatial significance
	 * Used by ASTAR
	 * 
	 * @return double array of coordinates
	 */
	public double[] getCoords();
	
}
