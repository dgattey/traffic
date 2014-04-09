package server.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import main.Utils;
import data.ProtocolManager;

public class TrafficController {
	
	ConcurrentHashMap<String, Double>	trafficMap;
	private Socket						trafficSock;
	private final ClientPool			clients;
	
	/**
	 * Creates a new traffic controller with the given host name and port
	 * 
	 * @param hostname the name of the traffic host
	 * @param port the port of the traffic host
	 */
	public TrafficController(final String hostname, final int port) {
		if (port < 1025) {
			throw new IllegalArgumentException("<TrafficController> Ports under 1025 are reserved");
		}
		if (Utils.isNullOrEmpty(hostname)) {
			throw new IllegalArgumentException("<TrafficController> Non-null, non-empty hostname required");
		}
		trafficMap = new ConcurrentHashMap<>();
		clients = new ClientPool();
		
		connectToServer(hostname, port);
	}
	
	/**
	 * @param hostName
	 * @param trafficPort
	 */
	private void connectToServer(final String hostName, final int trafficPort) {
		new Thread() {
			
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(4000);
						trafficSock = new Socket(hostName, trafficPort);
						System.out.println("Connected to the traffic server\n");
						startGettingTraffic();
					} catch (final IOException | InterruptedException e) {}
				}
			}
		}.start();
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
	
	private void startGettingTraffic() throws IOException {
		String line;
		final BufferedReader input = new BufferedReader(new InputStreamReader(trafficSock.getInputStream()));
		while ((line = input.readLine()) != null) {
			final Entry<String, Double> traffic = ProtocolManager.parseTrafficData(line);
			if (traffic != null) {
				trafficMap.put(traffic.getKey(), traffic.getValue());
				clients.broadcast(line);
			}
		}
		System.out.println("Disconnected from the traffic server\n");
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
