package edu.usfca.cs272;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
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
	private final Map<String, Long> wordCountMap;

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
	 * @param indexPath The path where the index should be written in JSON format
	 * @throws IOException If an error occurs during file writing.
	 */
	public void processIndex(Path indexPath) throws IOException {
		try(BufferedWriter writer = Files.newBufferedWriter(indexPath)) {
			JsonWriter.writeIndexToFile(invertedIndex, writer, 1, indexPath);
		}
	}

	/** A toString method prints inverted index contents
	 * 
	 *@returns to string value of the inverted index 
	 */
	@Override
	public String toString() {
		return invertedIndex.toString();
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
	public Set<String> getWords() {
		return Collections.unmodifiableSet(invertedIndex.keySet());
	}

	/**
	 * Retrieves all the locations and their positions for a given word.
	 * 
	 * @param word The word for which to retrieve locations and positions.
	 * @return An unmodifiable map containing the locations and positions of the given word.
	 */
	public Set<String> getLocations(String word) {
		if (hasWord(word)) {
			return Collections.unmodifiableSet(invertedIndex.get(word).keySet());
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
	public Set<Integer> getPositions(String word, String location) {
		if (hasLocation(word, location)) {
			return Collections.unmodifiableSet(invertedIndex.get(word).get(location));
		}

		return Collections.emptySet();
	}

	/**
	 * Gets the number of unique words in the index.
	 * 
	 * @return The number of unique words in the index.
	 */
	public int numWords() {
		return invertedIndex.size();
	}

	/**
	 * Gets the number of locations for a specific word in the index.
	 * 
	 * @param word The word to query.
	 * @return The number of locations for the given word. Returns 0 if the word does not exist.
	 */
	public int numLocations(String word) {
		return hasWord(word) ? invertedIndex.get(word).size() : 0;
	}

	/**
	 * Gets the number of positions for a specific word at a specific location in the index.
	 * 
	 * @param word The word to query.
	 * @param location The location to query.
	 * @return The number of positions for the word at the location. Returns 0 if the word or location does not exist.
	 */
	public int numPositions(String word, String location) {
		return hasLocation(word, location) ? invertedIndex.get(word).get(location).size() : 0;
	}

	/**
	 * Gets the total number of words in a specific location.
	 * 
	 * @param location The location to query.
	 * @return The total number of words at the location. Returns 0 if the location does not exist.
	 */
	public long numWordsInLocation(String location) {
		return wordCountMap.getOrDefault(location, 0L);
	}
}
