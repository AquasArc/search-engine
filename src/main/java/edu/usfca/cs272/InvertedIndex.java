package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
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
	
	public void add(String word, String location, int position) {
		//Adds to indexMap
		indexMap.putIfAbsent(word, new TreeMap<>());
		indexMap.get(word).putIfAbsent(location, new TreeSet<>());
		indexMap.get(word).get(location).add(position);
		
		//Adds to wordCountMap...
		wordCountMap.put(location, wordCountMap.getOrDefault(location, 0L) + 1);
	}
	
	public void addAll(List<String> words, String location, int position) {
		for (String word : words) {
			add(word, location, position++);
		}
	}
	
	public boolean hasWord(String word) {
		return indexMap.containsKey(word);
	}
	
	public boolean hasLocation(String word, String location) {
		return hasWord(word) && indexMap.get(word).containsKey(location);
	}
	
	public boolean hasPosition(String word, String location, int position) {
		return hasLocation(word, location) && indexMap.get(word).get(location).contains(position);
	}
	
	
	public void writeIndexMap(Path indexPath) throws IOException {
		JsonWriter.writeIndexToFile(indexMap, indexPath);
	}
	
	public void writeCountsMap(Path countsPath) throws IOException {
		JsonWriter.writeWordCountsToFile(wordCountMap, countsPath);
	}

	/**
	 * Retrieves the current state of the inverted index data structure.
	 * Then creates an unmodifiable copy and returns that
	 * 
	 * 
	 * @return The inverted index as a nested map.
	 */
	public Map<String, TreeMap<String, SortedSet<Integer>>> getIndexMap() {
	    Map<String, TreeMap<String, SortedSet<Integer>>> unmodifiableCopy = new TreeMap<>();

	    for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> outerEntry : indexMap.entrySet()) {
	        TreeMap<String, SortedSet<Integer>> innerUnmodifiable = new TreeMap<>();
	        
	        for (Map.Entry<String, TreeSet<Integer>> innerEntry : outerEntry.getValue().entrySet()) {
	            innerUnmodifiable.put(innerEntry.getKey(), Collections.unmodifiableSortedSet(new TreeSet<>(innerEntry.getValue())));
	        }
	        
	        unmodifiableCopy.put(outerEntry.getKey(), innerUnmodifiable);
	    }

	    return Collections.unmodifiableMap(unmodifiableCopy);
	}
}
