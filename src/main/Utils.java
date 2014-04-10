package main;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import data.ProtocolManager;

/**
 * Constants class to package up strings used in multiple classes
 * 
 * @author dgattey
 */
public abstract class Utils {
	
	public static final String	APP_NAME		= "Traffic";
	public static final String	APP_ABOUT		= "aiguha and dgattey";
	public static final String	USAGE_CLIENT	= "Usage: trafficClient hostname serverport [--debug]";
	public static final String	USAGE_SERVER	= "Usage: trafficServer ways nodes index hostname trafficport serverport";
	public static final String	DEBUG			= "debug";
	private static Socket		testSocket;
	
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
	 * Prints a message to sysout
	 * 
	 * @param msg the details!
	 */
	public static void printMessage(final String msg) {
		System.out.println(msg);
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
	
	/**
	 * Checks if a string is null or empty
	 * 
	 * @param s a string that exists
	 * @return true if either empty or null
	 */
	public static boolean isNullOrEmpty(final String s) {
		return s == null || s.isEmpty();
	}
	
	/**
	 * Checks if any of the passed in strings is null or empty
	 * 
	 * @param args a list of strings
	 * @return true if anything is empty or null
	 */
	public static boolean anyNullOrEmpty(final String... args) {
		for (final String s : args) {
			if (isNullOrEmpty(s)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Normalizes a percent of a range
	 * 
	 * @param min the min value
	 * @param max the max value
	 * @param percent the percent you want between min and max
	 * @return the double representing percent between min and max
	 */
	public static double normalize(final double min, final double max, final double percent) {
		return ((max - min) * percent) + min;
	}
	
	/**
	 * Tries connecting to a host on a port to see whether there's a connection
	 * 
	 * @param host the host name to connect on
	 * @param port the port to connect on
	 * @return if there was a connection using the given host and port
	 */
	public static boolean checkConnection(final String host, final int port) {
		try {
			if (testSocket == null) {
				testSocket = new Socket(host, port);
			}
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(testSocket.getOutputStream()));
			writer.write(ProtocolManager.Q_HB);
			writer.flush();
			return true;
		} catch (final IOException e) {
			testSocket = null;
			return false;
		}
	}
	
}
