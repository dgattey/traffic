package server.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import data.MapException;
import data.MapNode;
import data.MapWay;

/**
 * Controls interaction with the Graph
 * 
 * @author Advik
 */
public class GraphController {
	
	private static ConcurrentHashMap<String, Double>	trafficMap;
	
	/**
	 * Sets the traffic concurrent hash map
	 * 
	 * @param t the map
	 */
	public static void setTrafficMap(final ConcurrentHashMap<String, Double> t) {
		trafficMap = t;
	}
	
	/**
	 * Returns the shortest path between s and e Creates a new Graph object, which reuses the class variable
	 * DataProvider to construct the graph and calculate shortest paths using A*
	 * 
	 * @param s the starting node
	 * @param e the end node
	 * @return returns the shortest path in GraphEdges
	 * @throws IOException thrown internally
	 * @throws GraphException thrown internally
	 * @throws MapException if a DataProviderException was encountered
	 */
	private static List<GraphEdge<MapNode, MapWay>> getShortestPathEdges(final MapNode s, final MapNode e)
			throws IOException, GraphException, MapException {
		final MapsDataProvider provider = new MapsDataProvider(trafficMap);
		final GraphNode<MapNode, MapWay> start = provider.getNode(s);
		final GraphNode<MapNode, MapWay> end = provider.getNode(e);
		Graph<MapNode, MapWay> g;
		try {
			g = new Graph<>(start, end, provider);
			return g.shortestPath();
		} catch (final DataProviderException e1) {
			throw new MapException("<GraphController> " + e1.getMessage());
		}
	}
	
	/**
	 * Returns the shortest path between s and e Creates a new Graph object, which reuses the class variable
	 * DataProvider to construct the graph and calculate shortest paths using A*
	 * 
	 * @param s the starting node
	 * @param e the end node
	 * @return returns the shortest path in MapWays
	 * @throws IOException thrown internally
	 * @throws MapException if a DataProviderException was encountered
	 */
	public static List<MapWay> getShortestPathWays(final MapNode s, final MapNode e) throws IOException, MapException {
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
