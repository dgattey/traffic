package client.hub;

import java.io.IOException;
import java.util.List;

import data.ClientMapWay;
import data.LatLongPoint;
import data.ParseException;

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
	 * @throws ParseException if parsing the network item failed
	 * @throws IOException if the network couldn't be read
	 */
	public List<ClientMapWay> getRoute(LatLongPoint a, LatLongPoint b) throws IOException, ParseException;
	
	/**
	 * Returns a list of MapLines representing the route between intersections A and B for visual display
	 * 
	 * @param streetA1 street 1 of intersection A
	 * @param streetA2 street 2 of intersection A
	 * @param streetB1 street 1 of intersection B
	 * @param streetB2 street 2 of intersection B
	 * @return a list of lines to draw to screen
	 * @throws ParseException if parsing the network item failed
	 * @throws IOException if the network couldn't be read
	 */
	public List<ClientMapWay> getRoute(String streetA1, String streetA2, String streetB1, String streetB2)
			throws IOException, ParseException;
	
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
	public List<ClientMapWay> getChunk(LatLongPoint min, LatLongPoint max);
}
