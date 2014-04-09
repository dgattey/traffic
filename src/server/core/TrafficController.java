package server.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import main.Utils;
import data.ProtocolManager;

public class TrafficController {
	
	ConcurrentHashMap<String, Double>	trafficMap;
	private final Socket				trafficSock;
	private final BufferedReader		input;
	private final ClientPool			clients;
	
	/**
	 * Creates a new traffic controller with the given host name and port
	 * 
	 * @param hostname the name of the traffic host
	 * @param port the port of the traffic host
	 * @throws UnknownHostException if the host didn't exist
	 * @throws IOException if the socket didn't open correctly
	 */
	public TrafficController(final String hostname, final int port) throws UnknownHostException, IOException {
		if (port < 1025) {
			throw new IllegalArgumentException("<TrafficController> Ports under 1025 are reserved");
		}
		if (Utils.isNullOrEmpty(hostname)) {
			throw new IllegalArgumentException("<TrafficController> Non-null, non-empty hostname required");
		}
		trafficMap = new ConcurrentHashMap<>();
		trafficSock = new Socket(hostname, port);
		input = new BufferedReader(new InputStreamReader(trafficSock.getInputStream()));
		clients = new ClientPool();
	}
	
	/**
	 * Returns the client pool
	 * 
	 * @return the clientpool
	 */
	public ClientPool getPool() {
		return clients;
	}
	
	/**
	 * Returns the traffic map
	 * 
	 * @return the traffic map
	 */
	public synchronized ConcurrentHashMap<String, Double> getMap() {
		return trafficMap;
	}
	
	public void startGettingTraffic() throws IOException {
		String line;
		while ((line = input.readLine()) != null) {
			// System.out.println("Received from traffic server: " + line);
			final Entry<String, Double> traffic = ProtocolManager.parseTrafficData(line);
			if (traffic != null) {
				trafficMap.put(traffic.getKey(), traffic.getValue());
				clients.broadcast(line);
			}
			
		}
	}
	
	/**
	 * Writes all current traffic data to the client handler
	 * 
	 * @param c the client handler to send to
	 * @throws IOException if sending failed
	 */
	public void sendAll(final ClientHandler c) throws IOException {
		for (final String street : trafficMap.keySet()) {
			final Double val = trafficMap.get(street);
			final String fullString = street + "\t" + val + "\n";
			c.sendWithoutClosing(fullString);
		}
	}
}
