package server.kdtree;

import java.util.Comparator;
import java.util.List;

/**
 * Classes implementing this interface will be comparable on the requisite dimensions
 * 
 * @author aiguha
 * @param <T> a comparable type for this
 */
public interface KComparer<T> {
	
	/**
	 * Used to establish a contract with the implementing class that the object is comparable on a specific number of
	 * dimensions The number returned here must match the length of the list of comparators as well as the dimension of
	 * the KDTree to be created
	 * 
	 * @return the number of dimensions on which this object can be compared
	 */
	public int getValidDimensions();
	
	/**
	 * Returns a list of comparators equal in length to dimensions of the KDTree created, as well as the declared number
	 * of valid dimensions
	 * 
	 * @return list of comparators to compare these objects on different dimensions
	 */
	public List<Comparator<T>> getComparators();
	
	/**
	 * Returns the raw coordinates array, to allow distance calculation The length of the coordinate array should be
	 * consistent with the comparator list
	 * 
	 * @return the coordinates array
	 */
	public double[] getCoords();
	
	/**
	 * Compares two KComparer objects on a particular dimension Recommended that implementing objects implement this
	 * method to enable comparator creation KDTree uses its own version of this method for robustness
	 * 
	 * @param other other object to compare
	 * @param dim the dim on which they should be compared
	 * @return the difference between them
	 */
	public double compareOnDim(T other, int dim);
	
	/**
	 * Calculates distance to other
	 * 
	 * @param other another T to calculate distance to
	 * @return the distance from this to other
	 */
	public double distanceFrom(T other);
	
}
