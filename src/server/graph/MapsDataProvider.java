package server.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import server.io.DataSetException;
import server.io.IOController;
import data.MapNode;
import data.MapWay;

/**
 * Class that supplies MapData for Graphs
 * 
 * @author aiguha
 */
public class MapsDataProvider implements DataProvider<MapNode, MapWay> {
	
	// Stored objects to avoid re-reading files
	private final HashMap<String, GraphNode<MapNode, MapWay>>	graphNodeStore;
	private final ConcurrentHashMap<String, Double>				trafficMap;
	
	/**
	 * Constructs a MapsDataProvider that can be used to access the large datasets and dynamically construct the Maps
	 * graph
	 * 
	 * @param trafficMap the map of traffic data
	 */
	public MapsDataProvider(final ConcurrentHashMap<String, Double> trafficMap) {
		graphNodeStore = new HashMap<>();
		this.trafficMap = trafficMap;
		
	}
	
	@Override
	public GraphNode<MapNode, MapWay> getNode(final MapNode t) {
		if (t == null) {
			return null;
		}
		// Returns same GraphNode if it exists
		GraphNode<MapNode, MapWay> toReturn = graphNodeStore.get(t.getID());
		if (toReturn == null) {
			toReturn = new GraphNode<>(t);
			graphNodeStore.put(t.getID(), toReturn);
		}
		return toReturn;
	}
	
	/**
	 * Updates traffic if trafficMap is non-null
	 * 
	 * @param way the way to update
	 */
	public void updateWayTraffic(final MapWay way) {
		if (trafficMap != null) {
			way.updateTraffic(trafficMap.get(way.getName()));
		}
	}
	
	@Override
	public List<GraphEdge<MapNode, MapWay>> getNeighborVertices(final GraphNode<MapNode, MapWay> cur)
			throws DataProviderException, IOException {
		final List<GraphEdge<MapNode, MapWay>> edges = new ArrayList<>();
		final List<String> curWays = cur.getValue().getWays();
		// Traverses list of films, constructing edges and nodes for each target actor in each film
		try {
			for (final String wayID : curWays) {
				final MapWay way = IOController.getMapWay(wayID);
				// Invalid records would fail to create MapWay objects
				if (way == null) {
					continue;
				}
				
				updateWayTraffic(way);
				final MapNode curDest = way.getEnd();
				// Should not connect to itself
				if (curDest == cur.getValue()) {
					continue;
				}
				final GraphNode<MapNode, MapWay> destination = getNode(curDest);
				final GraphEdge<MapNode, MapWay> curNewEdge = new GraphEdge<>(way, cur, destination);
				edges.add(curNewEdge);
			}
		} catch (final DataSetException e) {
			throw new DataProviderException("File Parser failed with: " + e.getMessage());
		}
		return edges;
	}
	
}
