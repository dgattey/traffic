package data;

/**
 * Frontend representation of MapWays
 * 
 * @author aiguha
 */
public class ClientMapWay {
	
	private final String		id;
	private final String		name;
	private final ClientMapNode	start;
	private final ClientMapNode	end;
	
	public ClientMapWay(final String id, final String name, final ClientMapNode start, final ClientMapNode end) {
		this.id = id;
		this.name = name;
		this.start = start;
		this.end = end;
	}
	
	public String getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public ClientMapNode getStart() {
		return start;
	}
	
	public ClientMapNode getEnd() {
		return end;
	}
	
}
