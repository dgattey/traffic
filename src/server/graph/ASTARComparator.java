package server.graph;

import java.util.Comparator;

/**
 * A comparator class for use in doing A* for GraphNodes
 * 
 * @author dgattey
 * @param <T>
 * @param <S>
 */
public class ASTARComparator<T extends Graphable<T>, S extends Weighable<S>> implements Comparator<GraphNode<T, S>> {
	
	private final GraphNode<T, S>	target;
	
	/**
	 * Creates a comparator with target as the target graph node
	 * 
	 * @param target the graph node we're moving toward
	 */
	public ASTARComparator(final GraphNode<T, S> target) {
		this.target = target;
	}
	
	@Override
	public int compare(final GraphNode<T, S> o1, final GraphNode<T, S> o2) {
		return (int) Math.signum(o1.getDistance() - o2.getDistance() + heuristic(o1) - heuristic(o2));
	}
	
	/**
	 * Calculates a heuristic for use in Dijkstra's - Euclidean distance here
	 * 
	 * @param node
	 * @return
	 */
	private int heuristic(final GraphNode<T, S> node) {
		return calcDistance(node.getValue().getCoords(), target.getValue().getCoords());
	}
	
	private static int calcDistance(final double[] c1, final double[] c2) {
		double dist = 0;
		for (int i = 0; i < c1.length; i++) {
			dist += Math.pow((c1[i] - c2[i]), 2);
		}
		return (int) Math.sqrt(dist);
		
	}
	
}
