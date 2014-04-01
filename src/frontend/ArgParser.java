package frontend;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses command line args into an objects map and a list of filenames
 * 
 * @author dgattey
 */
public class ArgParser {
	
	private final List<String>			fileList;
	private final Integer				expectedFileNum;
	private final Map<String, Object>	objectsMap;
	private final Map<String, Class<?>>	flagMap;
	
	/**
	 * Sets up defaults and takes given map of string -> class and saves it for use in parsing
	 * 
	 * @param map a map representing command line flags and type of argument each flag expects - if null class, there's
	 *            no related argument
	 * @param expectedFileNum the number of files expected in the list
	 */
	public ArgParser(final Map<String, Class<?>> map, final Integer expectedFileNum) {
		flagMap = map;
		objectsMap = new HashMap<>();
		fileList = new ArrayList<>();
		this.expectedFileNum = expectedFileNum;
	}
	
	/**
	 * Sets up defaults, not caring about the length of the filename list
	 * 
	 * @param map a map representing command line flags and type of argument each flag expects - if null class, there's
	 *            no related argument
	 */
	public ArgParser(final Map<String, Class<?>> map) {
		this(map, null);
	}
	
	/**
	 * Checks if the given flag was an argument
	 * 
	 * @param s a possible key to check for
	 * @return if the key s was contained in the arguments map
	 */
	public boolean existsFlag(final String s) {
		return objectsMap.containsKey(s);
	}
	
	/**
	 * Returns an object for the given command line argument - if it doesn't exist in the map, null will be returned, as
	 * i t will if it explicitly mapped to null
	 * 
	 * @param s a possible key to check for
	 * @return an object matching the given key or null
	 */
	public Object getObjectForFlag(final String s) {
		return objectsMap.get(s);
	}
	
	/**
	 * Returns the default list containing non-flag arguments to the command line (for this program, filenames)
	 * 
	 * @return a list of non-flag arguments encountered while parsing
	 */
	public List<String> getFileNames() {
		return fileList;
	}
	
	/**
	 * Takes string array of arguments from the command line and makes them usable objects. It puts each flag into the
	 * arguments map, associating the flag with the corresponding object as necessary.
	 * <p>
	 * For example, if an integer is associated with the flag, it takes the next argument and tries to make it an
	 * integer. If a flag does not appear in the flagMap, an IllegalArgumentException will be thrown. If the same flag
	 * is used more than once, latest value is the one saved.
	 * 
	 * @param args a string array contain command line args
	 * @throws IllegalArgumentException if a flag wasn't valid or an object didn't instantiate correctly
	 */
	public void parse(final String[] args) {
		clear(); // Deletes all old information
		
		// Parse all args for flag or filename
		for (Integer i = 0; i < args.length; i++) {
			final String s = args[i];
			if (s.startsWith("--")) {
				
				// Error checking on flag length
				if (s.length() < 3) {
					cleanupAndThrow("Invalid flag - length");
				}
				final String flagName = s.substring(2);
				
				// If the flag is expected, add the associated object
				if (flagMap.containsKey(flagName)) {
					addObjectForFlag(flagName, args, i);
				} else {
					cleanupAndThrow("Invalid flag - unexpected value");
				}
			} else {
				// Not a flag, so make the string a filename
				fileList.add(s);
			}
		}
		
		// Check expected length of stuff other than flags
		if (expectedFileNum != null && fileList.size() != expectedFileNum) {
			cleanupAndThrow("Number of flags was wrong");
		}
	}
	
	/**
	 * Tries to create an object for flagName based on the next item in the string array
	 * 
	 * @param flagName the flag to look at
	 * @param args the string args to parse
	 * @param i the integer to increment for the loop above
	 */
	private void addObjectForFlag(final String flagName, final String[] args, Integer i) {
		/*
		 * Flag existed in mapping, so construct an object if necessary using the next string as an argument to the
		 * constructor - on failure, throw a new exception
		 */
		final Class<?> clazz = flagMap.get(flagName);
		Object obj = null;
		if (clazz != null && i + 1 < args.length) {
			try {
				final Constructor<?> construct = clazz.getConstructor(String.class);
				obj = construct.newInstance(new Object[] { args[i + 1] });
				i++;
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				cleanupAndThrow("Object couldn't be created");
			}
		}
		objectsMap.put(flagName, obj);
	}
	
	/**
	 * Simply clears the associated objects and throws an error
	 * 
	 * @param msg the message to pass to IllegalArgumentException
	 */
	private void cleanupAndThrow(final String msg) {
		clear();
		throw new IllegalArgumentException(msg);
	}
	
	/**
	 * Clears the maps for use again without reinstantiation
	 */
	private void clear() {
		objectsMap.clear();
		fileList.clear();
	}
}
