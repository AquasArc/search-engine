package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * Represents an inverted index where words map to their occurrences within files.
 * Words are stemmed and indexed with their positions.
 * 
 * @author Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class InvertedIndex {
	/**
	 * The core data structure of the inverted index.
	 * It maps words to file paths and the positions of the words within those files.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> indexMap;
	// Word : {Address : [1,2,3]}

	/**
	 * Data structure for total words in a file
	 * Map contains address : total words
	 */
	private Map<String, Long> wordCountMap;



	/**
	 * Initializes the inverted index data structure.
	 */

	public InvertedIndex() {
		this.indexMap = new TreeMap<>();
		this.wordCountMap = new TreeMap<>();
	}


	/**
	 * Adds a word, its location and position to the indexMap and wordCountMap.
	 * 
	 * @param word The word to add.
	 * @param location The file location.
	 * @param position The position of the word.
	 */
	public void add(String word, String location, int position) {
		//Adds to indexMap
		indexMap.putIfAbsent(word, new TreeMap<>());
		indexMap.get(word).putIfAbsent(location, new TreeSet<>());
		indexMap.get(word).get(location).add(position);

		//Adds to wordCountMap...
		wordCountMap.put(location, wordCountMap.getOrDefault(location, 0L) + 1);
	}

	/**
	 * Adds multiple words, their location and positions
	 * 
	 * @param words List of words to add.
	 * @param location The file location.
	 * @param position The starting position of the words.
	 */
	public void addAll(List<String> words, String location, int position) {
		for (String word : words) {
			add(word, location, position++);
		}
	}

	/**
	 * Checks if the index contains a word.
	 * 
	 * @param word The word to check.
	 * @return True if the word exists, else False.
	 */
	public boolean hasWord(String word) {
		return indexMap.containsKey(word);
	}

	/**
	 * Checks if the index contains a location for a given word.
	 * 
	 * @param word The word to check.
	 * @param location The file location.
	 * @return True if the location exists, else False.
	 */
	public boolean hasLocation(String word, String location) {
		return hasWord(word) && indexMap.get(word).containsKey(location);
	}

	/**
	 * Checks if the index contains a specific position for a given word and location.
	 * 
	 * @param word The word to check.
	 * @param location The file location.
	 * @param position The position of the word.
	 * @return True if the position exists, else False.
	 */
	public boolean hasPosition(String word, String location, int position) {
		return hasLocation(word, location) && indexMap.get(word).get(location).contains(position);
	}

	/**
	 * Writes the word counts to a file.
	 * 
	 * @param countsPath The path to write the word counts.
	 * @throws IOException If writing fails.
	 */
	public void writeCountsMap(Path countsPath) throws IOException {
		JsonWriter.writeWordCountsToFile(wordCountMap, countsPath);
	}

	public Set<String> viewWords() {
		return Collections.unmodifiableSet(indexMap.keySet());
	}

	public Map<String, TreeSet<Integer>> viewLocations(String word) {
		if (indexMap.containsKey(word)) {
			return Collections.unmodifiableMap(indexMap.get(word));
		}

		return Collections.emptyMap();
	}

	public TreeSet<Integer> viewPositions(String word, String location) {
		if (hasLocation(word, location)) {
			return (TreeSet<Integer>) Collections.unmodifiableSortedSet(indexMap.get(word).get(location));
		}

		return new TreeSet<>();
	}

	public Map<String, TreeMap<String, TreeSet<Integer>>> viewIndexMap() {
		return Collections.unmodifiableMap(indexMap);
	}
}
