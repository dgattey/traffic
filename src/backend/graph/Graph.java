package backend.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Implementation of Graphs, containing functionality to perform shortest path searches
 * 
 * @author aiguha
 * @param <T>
 * @param <S>
 */
public class Graph<T extends Graphable<T>, S extends Weighable<S>> {
	
	// Start and End of the Graph
	private final GraphNode<T, S>		start;
	private GraphNode<T, S>				end;
	
	// Data Provider: allows dynamic graph construction
	private final DataProvider<T, S>	provider;
	
	public Graph(final GraphNode<T, S> start, final GraphNode<T, S> end, final DataProvider<T, S> prov)
			throws IOException, GraphException, DataProviderException {
		if (start == null || end == null) {
			throw new GraphException("Provided arguments " + start + " and " + end + " must both exist.");
		}
		this.start = start;
		this.end = end;
		if (prov == null) {
			throw new GraphException("Graph Initialization Failed. Invalid Arguments.");
		}
		this.provider = prov;
	}
	
	public GraphNode<T, S> getStart() {
		return this.start;
	}
	
	public GraphNode<T, S> getEnd() {
		return this.end;
	}
	
	/**
	 * Uses the DataProvider to construct the next level of the graph, i.e, constructs cur's adjacent vertices and
	 * connecting edges
	 * 
	 * @param cur the node whose adjacent vertices are to be constructed
	 * @throws GraphException
	 * @throws DataProviderException
	 * @throws IOException
	 */
	public void buildLevel(final GraphNode<T, S> cur) throws GraphException, DataProviderException, IOException {
		final List<GraphEdge<T, S>> vertices = provider.getNeighborVertices(cur);
		if (vertices == null) {
			throw new GraphException("Dynamic Construction Failed. Data Provider Returned Invalid Data.");
		}
		cur.getEdges().addAll(vertices);
	}
	
	/**
	 * Performs Dijkstra's Algorithm, until shortest path to end is found Allows dynamic graph construction using
	 * DataProvider
	 * 
	 * @throws GraphException
	 * @throws DataProviderException
	 * @throws IOException
	 */
	private void calculatePaths() throws GraphException, DataProviderException, IOException {
		// Set distance of start of zero
		this.start.setDistance(0);
		final PriorityQueue<GraphNode<T, S>> pq = new PriorityQueue<>(20, new ASTARComparator<T, S>(end));
		// Keep track of visited nodes
		final HashSet<GraphNode<T, S>> visited = new HashSet<>();
		pq.add(start);
		while (!pq.isEmpty() && !Thread.currentThread().isInterrupted()) {
			final GraphNode<T, S> curSource = pq.poll();
			
			// Marks curSource as visited
			if (!visited.add(curSource)) {
				continue;
			}
			if (curSource.equals(end)) {
				// To assign end's parent edge pointer
				this.end = curSource;
				break;
			}
			// Building the neighbor Edges & Nodes of curSource
			buildLevel(curSource);
			for (final GraphEdge<T, S> e : curSource.getEdges()) {
				final GraphNode<T, S> target = e.getTarget();
				// Need not revisit popped nodes
				if (visited.contains(target)) {
					continue;
				}
				final double oldDist = target.getDistance();
				final double newDist = (curSource.getDistance() + e.getWeight());
				if (oldDist > newDist) {
					// Nodes with *infinite* distance are never in the pqueue, hence need not be removed
					if (oldDist != Double.MAX_VALUE) {
						pq.remove(target);
					}
					target.setDistance(newDist);
					// Sets target's parent node to the current edge
					target.setParentEdge(e);
					pq.add(target);
				}
			}
		}
		if (Thread.currentThread().isInterrupted()) {
			throw new GraphException("Thread interrupted");
		}
	}
	
	/**
	 * Runs Dijkstra's Algorithm, then builds a list of edges leading from the start to end representing the shortest
	 * path (if a path exists)
	 * 
	 * @return list of GraphEdges, representing the shortest path
	 * @throws GraphException
	 * @throws DataProviderException
	 * @throws IOException
	 */
	public List<GraphEdge<T, S>> shortestPath() throws GraphException, DataProviderException, IOException {
		calculatePaths();
		if (Thread.currentThread().isInterrupted()) {
			return null;
		}
		final List<GraphEdge<T, S>> path = new ArrayList<>();
		GraphNode<T, S> cur = end;
		
		// Start's parentEdge is necessarily null
		while (cur.getParentEdge() != null && !Thread.currentThread().isInterrupted()) {
			path.add(cur.getParentEdge());
			cur = cur.getParentEdge().getSource();
		}
		// Path was not found in this case
		if (Thread.currentThread().isInterrupted()
			|| (!path.isEmpty() && !path.get(path.size() - 1).getSource().equals(start))) {
			return null;
		}
		Collections.reverse(path);
		return path;
	}
}
