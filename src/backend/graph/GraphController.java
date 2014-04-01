package backend.graph;

import hub.MapNode;
import hub.MapWay;
import hub.MapsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls interaction with the Graph
 * 
 * @author Advik
 */
public class GraphController {
	
	/**
	 * Returns the shortest path between s and e Creates a new Graph object, which reuses the class variable
	 * DataProvider to construct the graph and calculate shortest paths using A*
	 * 
	 * @param s
	 * @param e
	 * @return
	 * @throws IOException
	 * @throws GraphException
	 * @throws MapsException
	 */
	private static List<GraphEdge<MapNode, MapWay>> getShortestPathEdges(final MapNode s, final MapNode e)
			throws IOException, GraphException, MapsException {
		final MapsDataProvider provider = new MapsDataProvider();
		final GraphNode<MapNode, MapWay> start = provider.getNode(s);
		final GraphNode<MapNode, MapWay> end = provider.getNode(e);
		Graph<MapNode, MapWay> g;
		try {
			g = new Graph<>(start, end, provider);
			return g.shortestPath();
		} catch (final DataProviderException e1) {
			throw new MapsException("<GraphController> " + e1.getMessage());
		}
	}
	
	public static List<MapWay> getShortestPathWays(final MapNode s, final MapNode e) throws IOException, MapsException {
		List<GraphEdge<MapNode, MapWay>> path;
		try {
			path = getShortestPathEdges(s, e);
		} catch (final GraphException e1) {
			return null;
		}
		if (path == null || Thread.currentThread().isInterrupted()) {
			return null;
		}
		final List<MapWay> toReturn = new ArrayList<>();
		for (final GraphEdge<MapNode, MapWay> curEdge : path) {
			toReturn.add(curEdge.getContent());
		}
		return toReturn;
	}
	
}
