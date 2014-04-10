package server.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents nodes in this package's Graph implementation
 * 
 * @author aiguha
 * @param <T> type contained in GraphNodes for the particular Graph
 * @param <S> type contained in GraphEdges for the particular Graph
 */
public class GraphNode<T extends Graphable<T>, S extends Weighable<S>> implements Comparable<GraphNode<T, S>> {
	
	private GraphEdge<T, S>				parentEdge;
	private final T						value;
	private final List<GraphEdge<T, S>>	edges;
	// Represents the distance from the start of the Graph
	private double						distance;
	
	/**
	 * Initializes GraphNode
	 * 
	 * @param value the internal T value
	 */
	public GraphNode(final T value) {
		this.parentEdge = null;
		this.value = value;
		this.edges = new ArrayList<>();
		this.distance = Double.MAX_VALUE;
	}
	
	/**
	 * Initializes GraphNode
	 * 
	 * @param value the internal value
	 * @param edges the list of edges
	 */
	public GraphNode(final T value, final List<GraphEdge<T, S>> edges) {
		// Parent edges are null initially
		this.parentEdge = null;
		this.value = value;
		this.edges = edges;
		this.distance = Double.MAX_VALUE;
	}
	
	public String getName() {
		return this.getValue().getName();
	}
	
	public GraphEdge<T, S> getParentEdge() {
		return parentEdge;
	}
	
	public void setParentEdge(final GraphEdge<T, S> parentEdge) {
		this.parentEdge = parentEdge;
	}
	
	public T getValue() {
		return value;
	}
	
	public List<GraphEdge<T, S>> getEdges() {
		return edges;
	}
	
	public void addEdge(final GraphEdge<T, S> edge) {
		edges.add(edge);
	}
	
	public void setDistance(final double distance) {
		this.distance = distance;
	}
	
	public double getDistance() {
		return this.distance;
	}
	
	@Override
	public String toString() {
		final String holder = "(Value: %s; V:%s; P:%s; D: %s)";
		return String.format(holder, value, edges, parentEdge, distance);
	}
	
	@Override
	public int compareTo(final GraphNode<T, S> o) {
		final int distComp = Double.compare(this.getDistance(), o.getDistance());
		// Two Nodes should be equal only if their ids (in addition to distance) are equal
		final int idComp = this.getValue().getID().compareTo(o.getValue().getID());
		if (distComp != 0) {
			return distComp;
		}
		return idComp;
	}
	
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof GraphNode<?, ?>)) {
			return false;
		} else {
			final GraphNode<?, ?> that = (GraphNode<?, ?>) o;
			// Ensures Consistency between Equals and CompareTo
			return (this.distance == that.distance) && (this.getValue().equals(that.getValue()));
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(value, distance);
	}
}
