package backend.graph;

import java.io.IOException;
import java.util.List;

/**
 * This interface provides data for the graph, enabling dynamic graph construction This helps generalize the Graph
 * implementation, letting the implementing class determine how to construct relevant parts of the graph as needed
 * 
 * @author aiguha
 * @param <T> type contained in the nodes
 * @param <S> type contained in the edges
 */
public interface DataProvider<T extends Graphable<T>, S extends Weighable<S>> {
	
	/**
	 * Gets Node containing T If a node containing t was previously made, this same node should be returned to have
	 * expected behavior
	 * 
	 * @param t the value of the node
	 * @return the node encapsulating t
	 */
	public GraphNode<T, S> getNode(T t);
	
	/**
	 * Returns the list of GraphEdges connecting cur to all neighboring vertices Returns an empty list if no neighbors
	 * exist
	 * 
	 * @param cur the node being considered
	 * @return the list of GraphEdges
	 * @throws DataProviderException
	 * @throws IOException
	 */
	public List<GraphEdge<T, S>> getNeighborVertices(GraphNode<T, S> cur) throws DataProviderException, IOException;
	
}
