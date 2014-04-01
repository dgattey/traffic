package main;


/**
 * Constants class to package up strings used in multiple classes
 * 
 * @author dgattey
 */
public abstract class Utils {
	
	public static final String	APPNAME			= "Traffic";
	public static final String	USAGE_CLIENT	= "Usage: trafficClient hostname serverport";
	public static final String	USAGE_SERVER	= "Usage: trafficServer ways nodes index hostname trafficport serverport";
	
	/**
	 * Calculates minimum of many ints
	 * 
	 * @param ints a list of ints to find min of
	 * @return the smallest of a variable number of ints
	 */
	public static int min(final int... ints) {
		int curr = Integer.MAX_VALUE;
		for (final int i : ints) {
			curr = Math.min(i, curr);
		}
		return curr;
	}
	
	/**
	 * Calculates maximum of many ints
	 * 
	 * @param ints a list of ints to find min of
	 * @return the smallest of a variable number of ints
	 */
	public static int max(final int... ints) {
		int curr = Integer.MIN_VALUE;
		for (final int i : ints) {
			curr = Math.max(i, curr);
		}
		return curr;
	}
	
	/**
	 * Prints an error to syserr
	 * 
	 * @param msg the details!
	 */
	public static void printError(final String msg) {
		System.err.println("ERROR: " + msg);
	}
	
	/**
	 * Gets the minimum element of an int array
	 * 
	 * @param arr the array to search
	 * @return the minimum element of arr
	 */
	public static int getMinOfArray(final int[] arr) {
		int min = Integer.MAX_VALUE;
		for (final int i : arr) {
			if (i < min) {
				min = i;
			}
		}
		return min;
	}
}
