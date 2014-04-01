package backend.autocorrect.trie;

import hub.Utilities;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import backend.autocorrect.generators.LEDObject;

/**
 * A class representing a node in a trie, to which a user can add a suffix, get the count, or check containment of a
 * suffix (recursive operations)
 * 
 * @author dgattey
 */
public class TrieNode {
	
	private int							count;
	private final Map<String, TrieNode>	suffixes;
	
	/**
	 * Public constructor, sets default values
	 */
	TrieNode() {
		count = 0;
		suffixes = new HashMap<>();
	}
	
	/**
	 * Checks to see if a given string is a word in the tree
	 * 
	 * @param s a string to follow through the nodes
	 * @return returns true if the node contains o
	 */
	boolean contains(final String s) {
		if (s == null) {
			return false;
		} else if (s.isEmpty()) {
			return isWord();
		}
		
		final String key = s.substring(0, 1);
		final String rest = s.substring(1);
		if (getSuffixes().containsKey(key)) {
			return getSuffixes().get(key).contains(rest);
		}
		return false;
	}
	
	/**
	 * Gives the count of the current node
	 * 
	 * @return the number of times this word has been inserted
	 */
	int getCount() {
		return count;
	}
	
	/**
	 * Adds the given suffix to the TrieTree by adding a new TrieNode for each letter, or using an existing node, ending
	 * iff the suffix is empty
	 * 
	 * @param suffix a new String to add to the tree
	 * @return true if the word was inserted
	 */
	boolean insert(final String suffix) {
		if (suffix.isEmpty()) {
			count++;
			return true;
		}
		
		// Otherwise, recur and keep adding suffixes
		final String key = suffix.substring(0, 1);
		final String rest = suffix.substring(1);
		TrieNode n = new TrieNode();
		if (getSuffixes().containsKey(key)) {
			n = getSuffixes().get(key);
		} else {
			getSuffixes().put(key, n);
		}
		
		return n.insert(rest);
	}
	
	/**
	 * Returns true if everything up to this node represents a word
	 * 
	 * @return if the current node is a word that's been inserted
	 */
	boolean isWord() {
		return count > 0;
	}
	
	/**
	 * Takes the suffixes and returns a list of all suffixes of this node
	 * 
	 * @return returns a collection representing all substrings of this
	 */
	public Collection<String> makeStrings() {
		if (getSuffixes().isEmpty()) {
			return new HashSet<>();
		}
		
		/*
		 * Takes all suffixes and gets makeStrings from them and concats the key with all substrings of its
		 * corresponding node
		 */
		final Set<String> allStrings = new HashSet<>();
		for (final Entry<String, TrieNode> e : getSuffixes().entrySet()) {
			final String key = e.getKey();
			final TrieNode n = e.getValue();
			final Collection<String> allSuffixes = n.makeStrings();
			
			// If it's a word, add the key no matter
			if (n.isWord()) {
				allStrings.add(key);
			}
			
			// Concat all suffixes's strings
			if (allSuffixes != null) {
				for (final String s : allSuffixes) {
					allStrings.add(key.concat(s));
				}
			}
		}
		return allStrings;
	}
	
	/**
	 * This recursive helper is used by the search function above. It assumes that the previousRow has been filled in
	 * already.
	 * 
	 * @param led an object that holds values necessary for use later
	 * @param letter the current letter to use for character checking
	 * @param prev the last row of the matrix used in searching
	 * @param word the word up to this point in the trie
	 */
	public void getLEDWords(final LEDObject led, final String letter, final int[] prev, final String word) {
		final int[] curr = new int[led.cols + 1];
		curr[0] = prev[0] + 1;
		
		/*
		 * Make a row, filling each column with the minimum LED for each letter in final word plus empty string at zero
		 * index
		 */
		for (int i = 1; i <= led.cols; i++) {
			final int insertCost = curr[i - 1] + 1;
			final int deleteCost = prev[i] + 1;
			int replaceCost = prev[i - 1];
			
			// Check previous character
			if (led.word.charAt(i - 1) != letter.charAt(0)) {
				replaceCost++;
			}
			curr[i] = Utilities.min(insertCost, deleteCost, replaceCost);
		}
		
		// Cost is within bounds and is word, so add it!
		if (curr[led.cols] <= led.dist && isWord()) {
			led.addWord(word);
		}
		
		// Remaining under min? Continue searching
		if (Utilities.getMinOfArray(curr) <= led.dist) {
			for (final Entry<String, TrieNode> entry : getSuffixes().entrySet()) {
				final String key = entry.getKey();
				entry.getValue().getLEDWords(led, key, curr, word.concat(key));
			}
		}
	}
	
	/**
	 * Gives the subtree (represented by a node and its children here) - Returns null if there doesn't exist a given
	 * subtree of the prefix
	 * 
	 * @param s the prefix of the subtree - look till empty
	 * @return the subtree of which s is a prefix
	 */
	TrieNode subTree(final String s) {
		if (s.isEmpty()) {
			return this;
		}
		
		final String key = s.substring(0, 1);
		final String rest = s.substring(1);
		if (getSuffixes().containsKey(key)) {
			return getSuffixes().get(key).subTree(rest);
		}
		
		// Allows differentiation between key not found and empty node
		return null;
		
	}
	
	@Override
	/**
	 * Prints out the TrieNode in format "<key<suffix , suffix...>>"
	 */
	public String toString() {
		String ret = "";
		
		final Set<Entry<String, TrieNode>> set = getSuffixes().entrySet();
		if (set.isEmpty()) {
			return " ";
		}
		
		for (final Entry<String, TrieNode> e : set) {
			ret = ret.concat(String.format("%s:%d%s, ", e.getKey(), e.getValue().getCount(), e.getValue()));
		}
		return "<" + ret.substring(0, ret.length() - 3) + "> ";
	}
	
	public Map<String, TrieNode> getSuffixes() {
		return suffixes;
	}
}
