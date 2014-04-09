package server.graph;

/**
 * Classes implementing this interface can be used as "content" within GraphEdges in this package's graph implementation
 * to represent information about the edge, particular to that graph
 * 
 * @author aiguha
 * @param <T> the edge type
 */
public interface Weighable<T> {
	
	/**
	 * T must have a non-null, non-empty name
	 * 
	 * @return name
	 */
	public String getName();
	
	/**
	 * T must have a non-null, non-empty, unique ID
	 * 
	 * @return the id
	 */
	public String getID();
	
	/**
	 * Weighable objects can decide how they want to be weighed, This weight is used as the weights of the edges in the
	 * graph implementation
	 * 
	 * @return the weight of T if it was to be contained in an edge
	 */
	public double getWeight();
	
}
