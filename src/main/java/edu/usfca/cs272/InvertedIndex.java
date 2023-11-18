package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	
	/** A toString method prints inverted index contents
	 * 
	 *@returns to string value of the inverted index 
	 */
	@Override
	public String toString() {
		return invertedIndex.toString();
	}


	/**
	 * Writes the indexed words, their occurrences, and positions within files to a file in a specific JSON format.
	 * 
	 * @param indexPath The path where the index should be written in JSON format
	 * @throws IOException If an error occurs during file writing.
	 */
	public void writeIndex(Path indexPath) throws IOException {
		JsonWriter.writeIndexToFile(invertedIndex,indexPath);
	}

	/**
	 * Writes the word counts to a file.
	 * 
	 * @param countsPath The path to write the word counts.
	 * @throws IOException If writing fails.
	 */
	public void writeCounts(Path countsPath) throws IOException {
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
		// Adds to invertedIndex
		invertedIndex.putIfAbsent(word, new TreeMap<>());
		invertedIndex.get(word).putIfAbsent(location, new TreeSet<>());
		boolean modified = invertedIndex.get(word).get(location).add(position);

		// Only updates the word count if something new was added
		if (modified) {
			wordCountMap.put(location, wordCountMap.getOrDefault(location, 0L) + 1);
		}
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

	/** =============================Project 2 Functionality============================= */

	/**A Private helper method for search. 
	 * 
	 * @param location The string location of where the word being searched is found
	 * @param count The integer count of how many times that word has appeared in said location
	 * @param lookupMap The lookup map to keep track of fr objects that has already been made
	 * @param resultList populate the List of FileResult objects that will be returned at the end of the process
	 */
	private void processFileResult(HashMap<String, FileResult> lookupMap, List<FileResult> resultList, Set<Entry<String, TreeSet<Integer>>> set) {
		for (var entry : set) {
			String location = entry.getKey();
			int count = entry.getValue().size();
			FileResult fr = lookupMap.get(location);
			if (fr == null) {
				fr = new FileResult(location);
				lookupMap.put(location, fr);
				resultList.add(fr);
			}
			fr.incrementCount(count);
		}
	}

	/**
	 * Performs an exact search for cleaned and unique queries and returns a sorted
	 * list of FileResult objects.
	 *
	 * @param cleanedUniqueQueries the cleaned and unique queries
	 * @return a sorted list of FileResult objects
	 */
	public List<FileResult> searchExact(TreeSet<String> cleanedUniqueQueries) {
		HashMap<String, FileResult> lookupMap = new HashMap<>();
		List<FileResult> resultList = new ArrayList<>();

		for (String word : cleanedUniqueQueries) {
			var innerMap = invertedIndex.get(word);
			
			if (innerMap != null) {
				processFileResult(lookupMap, resultList, innerMap.entrySet());
			}
		}
		
		Collections.sort(resultList);
		return resultList;
	}

	/**
	 * Performs a partial search for cleaned and unique queries and returns a sorted
	 * list of FileResult objects.
	 *
	 * @param cleanedUniqueQueries the cleaned and unique queries
	 * @return a sorted list of FileResult objects
	 */
	public List<FileResult> searchPartial(TreeSet<String> cleanedUniqueQueries) {
		HashMap<String, FileResult> lookupMap = new HashMap<>();
		List<FileResult> resultList = new ArrayList<>();

		for (String queryWord : cleanedUniqueQueries) {
			for (var entry : invertedIndex.tailMap(queryWord).entrySet()) {
				String word = entry.getKey();

				if (!word.startsWith(queryWord)) {
					break;
				}

				processFileResult(lookupMap, resultList, entry.getValue().entrySet());
			}
		}
		
		Collections.sort(resultList);
		return resultList;
	}


	/**
	 * Represents results of a file search, including the location of the file,
	 * word count, score based on search criteria, and the total number of words
	 * in the file.
	 * 
	 * @author Anton Lim
	 * @author CS 272 Software Development (University of San Francisco)
	 * @version Fall 2023
	 */
	public class FileResult implements Comparable<FileResult> {

		/** A private final string value for location*/
		private final String location;
		
		/** A private final integer for the amount of times a word has been found in a location */
		private int count = 0;
		
		/** A private double score which is total times a word has appeared / total words in location of file*/
		private double score = 0.0;

		/**A constructor for the FileResult inner class
		 * It takes in a location of a word...
		 * 
		 * @param location of a given word that is being searched...
		 */
		public FileResult(String location) {
			this.location = location;
		}

		/**A get count method...
		 * 
		 * @return count. The total amount of times a word appears in a location...
		 */
		public int getCount() {
			return this.count;
		}

		/** Returns the score.
		 *
		 * @return the score value.
		 */
		public double getScore() {
			return this.score;
		}

		/**
		 * Returns the where string.
		 *
		 * @return the where location.
		 */
		public String getWhere() {
			return this.location;
		}

		/**Increment count is used to add onto existing count to calculate score for words...
		 * 
		 * @param value Is the total count of a word in a file
		 */
		private void incrementCount(int value) {
			this.count += value;
			updateScore();
		}

		/**Uses the total words of a file and grabs the count, divides the two to calculate score...
		 * 
		 */
		private void updateScore() {
			long totalWords = wordCountMap.getOrDefault(location, 0L);
			if (totalWords != 0) {
				this.score = (double) count / totalWords;
			}
		}

		/**
		 * Converts the properties of this object to a map.
		 * 
		 * @return A map representation of this object.
		 */
		public Map<String, Object> asMap() {
			Map<String, Object> map = new HashMap<>();
			map.put("where", location);
			map.put("count", count);
			map.put("score", String.format("%.8f", score));  // to ensure 8 decimal places
			return map;
		}


		/** Compares the score + the count
		 * 
		 */
		public int compareTo(FileResult other) {
			int scoreCompare = Double.compare(other.score, this.score);
			if (scoreCompare != 0) {
				return scoreCompare;
			}

			int countCompare = Integer.compare(other.count, this.count);
			if (countCompare != 0) {
				return countCompare;
			}

			return this.location.compareToIgnoreCase(other.location);
		}

		/**To string method to test if variables have the proper values
		 * 
		 */
		@Override
		public String toString() {
			return "\nCount: " + count + ",\nScore: " + score + "\nLocation: " + location + "\n";
		}
	}
}