package data;

/**
 * Frontend representation of MapNodes
 * 
 * @author aiguha
 */
public class ClientMapNode {
	
	private final String		id;
	
	private final LatLongPoint	p;
	
	public ClientMapNode(final String id, final LatLongPoint p) {
		this.id = id;
		this.p = p;
	}
	
	public String getID() {
		return id;
	}
	
	public LatLongPoint getPoint() {
		return p;
	}
	
}
