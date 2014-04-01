package server;

import java.io.IOException;

import server.autocorrect.ACController;
import server.io.IOController;
import server.kdtree.KDTreeController;
import data.MapException;

public class ResponseController {
	
	private final ACController		_autocorrect;
	private final KDTreeController	_kdtree;
	private boolean					isReady;
	
	public ResponseController(final String ways, final String nodes, final String index, final String hostName,
			final int trafficPort, final int serverPort) throws MapException, IOException {
		isReady = false;
		IOController.setup(ways, nodes, index);
		_kdtree = new KDTreeController();
		_autocorrect = new ACController();
		isReady = true;
	}
	
	public void autocorrectResponse() {}
	
	public void intersectionResponse() {}
	
	public void routeResponse() {}
	
	public void mapDataResponse() {}
	
}
