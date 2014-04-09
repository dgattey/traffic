package server.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserTools {
	
	/**
	 * Returns the list of headers found at the top of a file
	 * 
	 * @param filename the file to be searched
	 * @param delimiter the delimiter used to split the columns
	 * @return A list of the headers
	 * @throws IOException file io
	 * @throws DataSetException bad or missing data
	 */
	public static List<String> getHeadersList(final String filename, final String delimiter) throws IOException,
			DataSetException {
		final BufferedReader b = new BufferedReader(new FileReader(filename));
		final String headers = b.readLine();
		b.close();
		if (headers == null) {
			throw new DataSetException("Dataset must contain headers.");
		}
		final ArrayList<String> headersList = new ArrayList<>(Arrays.asList(headers.split(delimiter)));
		return headersList;
		
	}
	
	/**
	 * Headers in files can be of different order than expected. This method finds the order of headers in this
	 * particular file Finds indices of all required headers in the given file Expects to find all headers in reqHeaders
	 * in the header list of the file Does not matter if there are duplicates in reqHeaders
	 * 
	 * @param filename the file to be searched
	 * @param reqHeaders the list of headers that are required and relevant
	 * @param delimiter the String used to delimit fields
	 * @return Map of headers to the indices they can be accessed at
	 * @throws DataSetException data contract violation
	 * @throws IOException file io or standard io
	 */
	public static Map<String, Integer> findHeaders(final String filename, final List<String> reqHeaders,
			final String delimiter) throws DataSetException, IOException {
		if (reqHeaders == null || reqHeaders.isEmpty()) {
			throw new DataSetException("Cannot find zero headers.");
		}
		final HashMap<String, Integer> toReturn = new HashMap<>();
		final List<String> allHeaders = getHeadersList(filename, delimiter);
		for (final String s : reqHeaders) {
			final int ret = allHeaders.indexOf(s);
			if (ret == -1) {
				throw new DataSetException("Missing header " + s + " in file " + filename);
			}
			toReturn.put(s, ret);
		}
		return toReturn;
	}
	
	/**
	 * Converts a string to a list, using the delimiter
	 * 
	 * @param list string to be split
	 * @param delimiter the delimiter to use
	 * @return list of strings
	 */
	public static List<String> convertToList(final String list, final String delimiter) {
		if (list == null || list.isEmpty()) {
			return new ArrayList<String>();
		}
		final String[] arr = list.split(delimiter);
		return new ArrayList<String>(Arrays.asList(arr));
	}
	
	/**
	 * Gets arr[index] if exists, or return ret Returns null if arr was null
	 * 
	 * @param arr the array
	 * @param index the index to be accessed
	 * @param ret the fallback return value
	 * @return arr[index] or ret
	 */
	public static String getOrElse(final String[] arr, final int index, final String ret) {
		return (arr.length > index) ? arr[index] : ret;
	}
	
	/**
	 * Gets the highest index in the map
	 * 
	 * @param headerIndices the map
	 * @return the highest index
	 */
	public static int getHighestIndex(final Map<String, Integer> headerIndices) {
		return Collections.max(headerIndices.values());
	}
	
	/**
	 * Simple method that splits a line on a given delimiter, and ensures that it contains at least minLength fields
	 * 
	 * @param line the line to be parsed
	 * @param minLength the minimum number of columns in the line to be valid
	 * @param delimiter the record delimiter
	 * @return array of the line columns
	 * @throws DataSetException bad or missing data
	 */
	public static String[] parseLine(final String line, final int minLength, final String delimiter)
			throws DataSetException {
		if (line == null || minLength < 0 || delimiter == null) {
			throw new DataSetException("Internal: Invalid arguments to parseLine");
		}
		final String[] lineArr = line.split(delimiter);
		if (lineArr.length < minLength) {
			throw new DataSetException("Parser encountered invalid line:" + line);
		}
		return lineArr;
	}
	
	/**
	 * Parses a line
	 * 
	 * @param line string of the line
	 * @param headerMap map of important headers
	 * @param delimiter file delimiter
	 * @return the map of headers to values
	 * @throws DataSetException bad or missing data
	 */
	public static Map<String, String> parseLine(final String line, final Map<String, Integer> headerMap,
			final String delimiter) throws DataSetException {
		if (line == null || headerMap == null || delimiter == null) {
			throw new DataSetException("Internal: Invalid arguments to parseLine");
		}
		final Map<String, String> toReturn = new HashMap<>();
		final String[] lineArr = line.split(delimiter);
		for (final String header : headerMap.keySet()) {
			final int index = headerMap.get(header);
			toReturn.put(header, getOrElse(lineArr, index, null));
		}
		return toReturn;
	}
	
	/**
	 * Returns a particular column of a line record
	 * 
	 * @param lineArr the line record (already split to ensure user can specify validity)
	 * @param headerMap the map of header indices
	 * @param header the important header
	 * @return the column value for this record
	 * @throws DataSetException bad or missing data
	 */
	public static String getColumn(final String[] lineArr, final Map<String, Integer> headerMap, final String header)
			throws DataSetException {
		if (!headerMap.containsKey(header)) {
			throw new DataSetException("Did not find header " + header + " in line" + Arrays.toString(lineArr));
		}
		return ParserTools.getOrElse(lineArr, headerMap.get(header), null);
	}
	
	public static int searchPartialInRecord(final String record, final String search, final int searchIndex,
			final String delimiter) throws DataSetException {
		if (record == null) {
			throw new DataSetException("Found null record");
		}
		final String[] recordArr = record.split(delimiter);
		if (recordArr.length <= searchIndex) {
			throw new DataSetException("Malformed Record in File");
		}
		final int cutTo = Math.min(search.length(), recordArr[searchIndex].length());
		final String key = recordArr[searchIndex].substring(0, cutTo);
		return key.compareTo(search);
	}
	
	public static int searchFullyInRecord(final String record, final String search, final int searchIndex,
			final String delimiter) throws DataSetException {
		if (record == null) {
			throw new DataSetException("Found null record");
		}
		final String[] recordArr = record.split(delimiter);
		if (recordArr.length <= searchIndex) {
			throw new DataSetException("Malformed Record in File");
		}
		return recordArr[searchIndex].compareTo(search);
	}
	
}
