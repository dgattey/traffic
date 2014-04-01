package server.graph;

import java.util.Objects;

/**
 * This class represents the edges of this package's Graph implementation
 * 
 * @author aiguha
 * @param <T> type contained in GraphNodes for the particular Graph
 * @param <S> type contained in GraphEdges for the particular Graph
 */
public class GraphEdge<T extends Graphable<T>, S extends Weighable<S>> {
	
	private final S					content;
	// Node from which edges arises
	private final GraphNode<T, S>	source;
	// Node to which edge leads
	private final GraphNode<T, S>	target;
	// Weight of Edge
	private final double			weight;
	
	public GraphEdge(final S content, final GraphNode<T, S> source, final GraphNode<T, S> target) {
		this.content = content;
		this.source = source;
		this.target = target;
		this.weight = content.getWeight();
	}
	
	public String getName() {
		return this.content.getName();
	}
	
	public S getContent() {
		return this.content;
	}
	
	public GraphNode<T, S> getSource() {
		return source;
	}
	
	public GraphNode<T, S> getTarget() {
		return target;
	}
	
	public double getWeight() {
		return weight;
	}
	
	@Override
	public String toString() {
		final String holder = "(Edge: %s; Source: %s; Target: %s)";
		return String.format(holder, content.getName(), source.getValue().getName(), target.getValue().getName());
	}
	
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof GraphEdge<?, ?>)) {
			return false;
		} else {
			final GraphEdge<?, ?> that = (GraphEdge<?, ?>) o;
			return (this.content.equals(that.content) && this.source.equals(that.source) && this.target
					.equals(that.target));
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(source, target, content);
	}
}
