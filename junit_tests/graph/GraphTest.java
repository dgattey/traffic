package graph;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.graph.DataProviderException;
import server.graph.Graph;
import server.graph.GraphEdge;
import server.graph.GraphException;
import server.graph.GraphNode;
import server.graph.MapsDataProvider;
import server.io.IOController;
import data.MapException;
import data.MapNode;
import data.MapWay;

@SuppressWarnings("static-method")
public class GraphTest extends IOController {
	
	@Before
	public void setUp() throws Exception {
		IOController.setup("data/ways.tsv", "data/nodes.tsv", "data/index.tsv");
	}
	
	@After
	public void tearDownAfterClass() {
		IOController.tearDown();
	}
	
	@Test
	public void oneHopPath() {
		try {
			final MapsDataProvider prov = new MapsDataProvider();
			// Thayer St
			final MapNode s = IOController.getMapNode("/n/4182.7140.1955940297");
			final MapNode e = IOController.getMapNode("/n/4182.7140.1957915158");
			final GraphNode<MapNode, MapWay> start = prov.getNode(s);
			final GraphNode<MapNode, MapWay> end = prov.getNode(e);
			final Graph<MapNode, MapWay> g = new Graph<>(start, end, prov);
			
			final List<GraphEdge<MapNode, MapWay>> path = g.shortestPath();
			assertTrue(path.size() == 1);
			assertTrue(path.get(0).getSource().getValue().getID().equals("/n/4182.7140.1955940297"));
			assertTrue(path.get(0).getTarget().getValue().getID().equals("/n/4182.7140.1957915158"));
			
		} catch (MapException | IOException | GraphException | DataProviderException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void twoHopPath() {
		try {
			final MapsDataProvider prov = new MapsDataProvider();
			// Thayer St
			final MapNode s = IOController.getMapNode("/n/4182.7140.1955940297");
			final MapNode e = IOController.getMapNode("/n/4182.7140.1957915190");
			final GraphNode<MapNode, MapWay> start = prov.getNode(s);
			final GraphNode<MapNode, MapWay> end = prov.getNode(e);
			final Graph<MapNode, MapWay> g = new Graph<>(start, end, prov);
			
			final List<GraphEdge<MapNode, MapWay>> path = g.shortestPath();
			assertTrue(path.size() == 2);
			assertTrue(path.get(0).getSource().getValue().getID().equals("/n/4182.7140.1955940297"));
			assertTrue(path.get(1).getTarget().getValue().getID().equals("/n/4182.7140.1957915190"));
			
		} catch (IOException | GraphException | DataProviderException | MapException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void zeroHopPath() {
		try {
			final MapsDataProvider prov = new MapsDataProvider();
			// Thayer St
			final MapNode s = IOController.getMapNode("/n/4182.7140.1955940297");
			final MapNode e = IOController.getMapNode("/n/4182.7140.1955940297");
			final GraphNode<MapNode, MapWay> start = prov.getNode(s);
			final GraphNode<MapNode, MapWay> end = prov.getNode(e);
			final Graph<MapNode, MapWay> g = new Graph<>(start, end, prov);
			
			final List<GraphEdge<MapNode, MapWay>> path = g.shortestPath();
			assertTrue(path.size() == 0);
			
		} catch (IOException | GraphException | DataProviderException | MapException e) {
			e.printStackTrace();
		}
		
	}
}
