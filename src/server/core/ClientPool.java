package server.core;

import java.io.IOException;
import java.util.LinkedList;

/**
 * A group of ClientHandlers representing the a group of sockets
 */
public class ClientPool {
	
	private final LinkedList<ClientHandler>	_clients;
	
	/**
	 * Initialize a new ClientPool
	 */
	public ClientPool() {
		_clients = new LinkedList<ClientHandler>();
	}
	
	/**
	 * Add a new client to the chat room.
	 * 
	 * @param client to add
	 */
	public synchronized void add(final ClientHandler client) {
		_clients.add(client);
	}
	
	/**
	 * Remove a client from the pool. Only do this if you intend to clean up that client later.
	 * 
	 * @param client to remove
	 * @return true if the client was removed, false if they were not there.
	 */
	public synchronized boolean remove(final ClientHandler client) {
		return _clients.remove(client);
	}
	
	/**
	 * Send a message to clients in the pool
	 * 
	 * @param message to send
	 * @param sender the client _not_ to send the message to (send to everyone if null)
	 */
	public synchronized void broadcast(final String message) {
		for (final ClientHandler client : _clients) {
			try {
				client.send(message);
			} catch (final IOException e) {
				remove(client);
			}
		}
	}
	
	/**
	 * Close all {@link ClientHandler}s and empty the pool
	 */
	public synchronized void killall() {
		// broadcast("The server is quitting now. Goodbye.");
		
		for (final ClientHandler client : _clients) {
			client.kill();
		}
		
		_clients.clear();
	}
}
