package server.autocorrect.trie;

import java.util.Collection;

/**
 * Implements a Trie for Strings
 * 
 * @author dgattey
 */
public class TrieTree {
	
	private final TrieNode	root;
	
	/**
	 * Public constructor, makes an empty tree
	 */
	public TrieTree() {
		root = new TrieNode();
	}
	
	/**
	 * Private constructor for subtree creation
	 * 
	 * @param n a node to set as root
	 */
	private TrieTree(final TrieNode n) {
		root = n;
	}
	
	/**
	 * Adds a single string to the trie
	 * 
	 * @param s the string to add
	 * @return true if the thing inserted fine
	 */
	public boolean add(final String s) {
		if (s == null || s.isEmpty()) {
			return false;
		}
		return getRoot().insert(s);
	}
	
	/**
	 * Adds all strings in a collection to the trie
	 * 
	 * @param c a collection of strings
	 * @return for consistency, if it was added fine or not
	 */
	public boolean addAll(final Collection<String> c) {
		for (final String s : c) {
			add(s);
		}
		return true;
	}
	
	/**
	 * Returns a subtree of the current tree, represented as a new Tree that has as root the subtree that results from
	 * following prefix through the tree. If the subtree is null (prefix not in tree), it returns an empty tree with
	 * root node as a new TrieNode.
	 * 
	 * @param s the prefix to follow through the tree to get the subtree
	 * @return a new TrieTree representing the suffixes of prefix that exist in the tree
	 */
	public TrieTree getSubTree(final String s) {
		final TrieNode n = getRoot().subTree(s);
		return new TrieTree((n == null) ? new TrieNode() : n);
	}
	
	/**
	 * Checks if the prefix is a word in the trie yet
	 * 
	 * @param s the prefix to check for
	 * @return if prefix is a word in the trie already
	 */
	public boolean isWord(final String s) {
		return getSubTree(s).getRoot().isWord();
	}
	
	/**
	 * Checks to see if the string is a word
	 * 
	 * @param s the string to check for
	 * @return if the trie contains the given string o as a full word
	 */
	public boolean query(final String s) {
		return getRoot().contains(s);
	}
	
	/**
	 * Prints out the full tree
	 */
	@Override
	public String toString() {
		return "Trie:[ " + getRoot().toString() + "]";
	}
	
	/**
	 * Returns the count for this string
	 * 
	 * @param s the string to search for
	 * @return the saved count of s in the trie
	 */
	public int getCount(final String s) {
		return getSubTree(s).getRoot().getCount();
	}
	
	public TrieNode getRoot() {
		return root;
	}
	
}
