package server.graph;

/**
 * Thrown by Graph implementation in exception cases
 * @author aiguha
 *
 */
public class GraphException extends Exception {

	/**
	 * Default Serial ID
	 */
	private static final long serialVersionUID = 1L;

	
	public GraphException() {
		super();
	}
	
	public GraphException(String message) {
		super(message);
	}
}
