package client.hub;

import java.util.List;

import client.view.MapChunk;
import data.ClientMapWay;
import data.LatLongPoint;

/**
 * @author dgattey
 */
public interface Controllable {
	
	/**
	 * Returns a list of ClientMapWay representing the route between points a and b for visual display
	 * 
	 * @param a the beginning
	 * @param b the obvious end
	 * @return a list of lines to draw to screen
	 */
	public List<ClientMapWay> getRoute(LatLongPoint a, LatLongPoint b);
	
	/**
	 * Returns a list of MapLines representing the route between intersections A and B for visual display
	 * 
	 * @param streetA1 street 1 of intersection A
	 * @param streetA2 street 2 of intersection A
	 * @param streetB1 street 1 of intersection B
	 * @param streetB2 street 2 of intersection B
	 * @return a list of lines to draw to screen
	 */
	public List<ClientMapWay> getRoute(String streetA1, String streetA2, String streetB1, String streetB2);
	
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
	public MapChunk getChunk(LatLongPoint min, LatLongPoint max);
}
