package server.kdtree;

/**
 * TreeNode This class is used as nodes for the K-D Tree
 * 
 * @author aiguha
 * @param <T> a comparable type
 */
public class TreeNode<T extends KComparer<T>> {
	
	TreeNode<T>			left;
	TreeNode<T>			right;
	private int			axis;
	private final int	depth;
	private T			location;
	boolean				visited;
	
	/**
	 * Constructor for a TreeNode
	 * 
	 * @param axis the axis this level is using
	 * @param depth the current depth of the tree
	 */
	public TreeNode(final int axis, final int depth) {
		this.setAxis(axis);
		this.depth = depth;
		this.visited = false;
	}
	
	// Getters and setters
	
	public int getAxis() {
		return axis;
	}
	
	public void setAxis(final int axis) {
		this.axis = axis;
	}
	
	public void setLocation(final T location) {
		this.location = location;
	}
	
	public T getLocation() {
		return this.location;
	}
	
	/**
	 * Information printer
	 * 
	 * @return a string representing this node
	 */
	public String printInfo() {
		return ("axis: " + getAxis() + "; depth: " + depth + "; location: " + location);
	}
	
	@Override
	public String toString() {
		return ("location: " + location + "(" + left + "," + right + ")");
	}
}
