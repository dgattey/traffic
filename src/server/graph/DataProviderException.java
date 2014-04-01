package server.graph;

/**
 * DataProvider Exception
 * Classes implementing DataProvider should throw this exception 
 * in appropriate situations
 * @author aiguha
 *
 */
public class DataProviderException extends Exception {
	/**
	 * Default serial ID
	 */
	private static final long serialVersionUID = 1L;

	public DataProviderException() {
		super();
	}
	
	public DataProviderException(String message) {
		super(message);
	}

}
