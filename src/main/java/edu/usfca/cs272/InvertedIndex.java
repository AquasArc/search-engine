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
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;
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
		this.invertedIndex = new TreeMap<>();
		this.wordCountMap = new TreeMap<>();
	}

	
	/**
	 * Writes the indexed words, their occurrences, and positions within files to a file in a specific JSON format.
	 * 
	 * @param indexPath The path where the index should be written in JSON format.
	 * @param index The InvertedIndex instance containing the indexed data.
	 * @throws IOException If an error occurs during file writing.
	 */
	public void processIndex(Path indexPath) throws IOException {
		JsonWriter.writeIndexToFile(invertedIndex, indexPath);
	}
	
	/** A toString method that returns builder which contains values
	 * from wordcount map and inverted index
	 * 
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Inverted Index: \n");
		for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> entry : invertedIndex.entrySet()) {
			builder.append("\t").append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
		}
		
		builder.append("Word Count Map: \n");
		for (Map.Entry<String, Long> entry : wordCountMap.entrySet()) {
			builder.append("\t").append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
		}
		
		return builder.toString();
	}
	
	/**
	 * Writes the word counts to a file.
	 * 
	 * @param countsPath The path to write the word counts.
	 * @throws IOException If writing fails.
	 */
	public void writeCountsMap(Path countsPath) throws IOException {
		JsonWriter.writeObject(wordCountMap,countsPath);
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
		invertedIndex.putIfAbsent(word, new TreeMap<>());
		invertedIndex.get(word).putIfAbsent(location, new TreeSet<>());
		invertedIndex.get(word).get(location).add(position);

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
		return invertedIndex.containsKey(word);
	}

	/**
	 * Checks if the index contains a location for a given word.
	 * 
	 * @param word The word to check.
	 * @param location The file location.
	 * @return True if the location exists, else False.
	 */
	public boolean hasLocation(String word, String location) {
		return hasWord(word) && invertedIndex.get(word).containsKey(location);
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
		return hasLocation(word, location) && invertedIndex.get(word).get(location).contains(position);
	}

	/**
	 * Retrieves a set of all the words in the inverted index.
	 * 
	 * @return An unmodifiable set of all words in the index.
	 */
	public Set<String> viewWords() {
		return Collections.unmodifiableSet(invertedIndex.keySet());
	}

	/**
	 * Retrieves all the locations and their positions for a given word.
	 * 
	 * @param word The word for which to retrieve locations and positions.
	 * @return An unmodifiable map containing the locations and positions of the given word.
	 */
	public Set<String> viewLocations(String word) {
		// fix
		if (invertedIndex.containsKey(word)) {
			if (invertedIndex.get(word) != null) {
				return Collections.unmodifiableSet(invertedIndex.get(word).keySet());
			}
		}
		return Collections.emptySet();
	}

	/**
	 * Retrieves all the positions for a given word and location.
	 * 
	 * @param word The word for which to retrieve positions.
	 * @param location The location for which to retrieve positions.
	 * @return An unmodifiable sorted set containing all the positions of the given word in the given location.
	 */
	public Set<Integer> viewPositions(String word, String location) {
		if (hasLocation(word, location)) {
			return Collections.unmodifiableSet(invertedIndex.get(word).get(location));
		}

		return Collections.emptySet();
	}

	//Num methods:
	//For Counts -> total size of indices
	//Given a certain word, how many files are there
	//Given a certain word & file, how many positions are there? (similar to view positions)
	    // Use positions method viewPositions.size();
}
