package hub;

import java.util.List;

/**
 * @author dgattey
 */
public interface Controllable {
	
	/**
	 * Does KDTree search for LatLongPoint p and returns the nearest intersection as a node to use in the frontend
	 * 
	 * @param p a LatLongPoint to search for in the backend
	 * @return a MapNode representing the intersection
	 */
	public MapNode getNearestIntersection(LatLongPoint p);
	
	/**
	 * Does file search for two strings representing an intersection
	 * 
	 * @param street1 a street name to cross with another
	 * @param street2 a second street
	 * @return the MapNode representing the intersection or null if streets don't intersection
	 */
	public MapNode getNearestIntersection(String street1, String street2);
	
	/**
	 * Returns a list of MapLines representing the route between start and end for visual display
	 * 
	 * @param start the beginning
	 * @param end the obvious end
	 * @return a list of lines to draw to screen
	 */
	public List<MapWay> getRoute(MapNode start, MapNode end);
	
	/**
	 * Searches the trie for the given input and returns a list of suggestions for input
	 * 
	 * @param input the user's input
	 * @return a list of the suggestions from input
	 */
	public List<String> getSuggestions(String input);
	
	/**
	 * Reads in a page of map data from file. Assumes valid input, namely that min < max and that min and max are
	 * separated by exactly 0.01 degrees in each direction
	 * 
	 * @param min the minimum LatLongPoint for this data
	 * @param max the maximum LatLongPoint for this data
	 * @return a list of MapWays in this page of data
	 */
	public List<MapWay> pageInMapData(LatLongPoint min, LatLongPoint max);
}
