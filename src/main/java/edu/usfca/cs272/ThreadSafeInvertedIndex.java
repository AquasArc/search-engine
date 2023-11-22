package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;



/**ThreadSafeInvertedIndex class extends the InvertedIndex class with threadsafe methods 
 * 
 * 
 * @author Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {

	/** Creating a MultiReaderLock object */
	private final MultiReaderLock lock;

	/**
	 * Initializes a thread-safe indexed set.
	 *
	 */
	public ThreadSafeInvertedIndex() {
		super();
		this.lock = new MultiReaderLock();
	}
	
	/**
	 * Adds a word, its location and position to the indexMap and wordCountMap.
	 * 
	 * @param word The word to add.
	 * @param location The file location.
	 * @param position The position of the word.
	 */
	@Override
	public void add(String word, String location, int position) {
		lock.writeLock().lock();
		try {
			super.add(word, location, position);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Adds multiple words, their location and positions
	 * 
	 * @param words List of words to add.
	 * @param location The file location.
	 * @param position The starting position of the words.
	 */
	@Override
	public void addAll(List<String> words, String location, int position) {
		lock.writeLock().lock();
		try {
			super.addAll(words, location, position);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Adds all the entries from another inverted index into the original inverted index
	 * Same for wordCountMap
	 * 
	 * @param otherIndex The other InvertedIndex to merge with this one.
	 */
	@Override
	public void addAll(InvertedIndex index) {
		lock.writeLock().lock();
		try {
			super.addAll(index);
		} finally {
			lock.writeLock().unlock();
		}
	}

	
	/**
	 * Checks if the index contains a word.
	 * 
	 * @param word The word to check.
	 * @return True if the word exists, else False.
	 */
	@Override
	public boolean hasWord(String word) {
		lock.readLock().lock();
		try {
			return super.hasWord(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Checks if the index contains a location for a given word.
	 * 
	 * @param word The word to check.
	 * @param location The file location.
	 * @return True if the location exists, else False.
	 */
	@Override
	public boolean hasLocation(String word, String location) {
		lock.readLock().lock();
		try {
			return super.hasLocation(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Checks if the index contains a specific position for a given word and location.
	 * 
	 * @param word The word to check.
	 * @param location The file location.
	 * @param position The position of the word.
	 * @return True if the position exists, else False.
	 */
	@Override
	public boolean hasPosition(String word, String location, int position) {
		lock.readLock().lock();
		try {
			return super.hasPosition(word, location, position);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Retrieves a set of all the words in the inverted index.
	 * 
	 * @return An unmodifiable set of all words in the index.
	 */
	@Override
	public Set<String> getWords() {
		lock.readLock().lock();
		try {
			return super.getWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Retrieves all the locations and their positions for a given word.
	 * 
	 * @param word The word for which to retrieve locations and positions.
	 * @return An unmodifiable map containing the locations and positions of the given word.
	 */
	@Override
	public Set<String> getLocations(String word) {
		lock.readLock().lock();
		try {
			return super.getLocations(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Retrieves all the positions for a given word and location.
	 * 
	 * @param word The word for which to retrieve positions.
	 * @param location The location for which to retrieve positions.
	 * @return An unmodifiable sorted set containing all the positions of the given word in the given location.
	 */
	@Override
	public Set<Integer> getPositions(String word, String location) {
		lock.readLock().lock();
		try {
			return super.getPositions(word, location);
		} finally {
			lock.readLock().unlock(); 
		}
	}

	/**
	 * Gets the number of unique words in the index.
	 * 
	 * @return The number of unique words in the index.
	 */
	@Override
	public int numWords() {
		lock.readLock().lock();
		try {
			return super.numWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Gets the number of locations for a specific word in the index.
	 * 
	 * @param word The word to query.
	 * @return The number of locations for the given word. Returns 0 if the word does not exist.
	 */
	@Override
	public int numLocations(String word) {
		lock.readLock().lock();
		try {
			return super.numLocations(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	/** To string method... 
	 * 
	 * @return toString method
	 */
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**Calls super and writes the inverted index in a pretty json format using 
	 * json writer...
	 * 
	 * 
	 * @param indexPath the path that is being written to 
	 */
	public void writeIndex(Path indexPath) throws IOException {
		lock.readLock().lock();
		try {
			super.writeIndex(indexPath);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**Calls super and writes the word count map in a pretty json format using 
	 * json writer...
	 * 
	 * 
	 * @param countsPath the path that is being written to 
	 */
	@Override
	public void writeCounts(Path countsPath) throws IOException {
		lock.readLock().lock();
		try {
			super.writeCounts(countsPath);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Performs an exact search for cleaned and unique queries and returns a sorted
	 * list of FileResult objects.
	 *
	 * @param cleanedUniqueQueries the cleaned and unique queries
	 * @return a sorted list of FileResult objects
	 */
	@Override
	public List<FileResult> searchExact(TreeSet<String> cleanedUniqueQueries) {
		lock.readLock().lock();
		try {
			return super.searchExact(cleanedUniqueQueries);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Performs a partial search for cleaned and unique queries and returns a sorted
	 * list of FileResult objects.
	 *
	 * @param cleanedUniqueQueries the cleaned and unique queries
	 * @return a sorted list of FileResult objects
	 */
	@Override
	public List<FileResult> searchPartial(TreeSet<String> cleanedUniqueQueries) {
		lock.readLock().lock();
		try {
			return super.searchPartial(cleanedUniqueQueries);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Gets the number of positions for a specific word at a specific location in the index.
	 * 
	 * @param word The word to query.
	 * @param location The location to query.
	 * @return The number of positions for the word at the location. Returns 0 if the word or location does not exist.
	 */
	@Override
	public int numPositions(String word, String location) {
		lock.readLock().lock();
		try {
			return super.numPositions(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Gets the total number of words in a specific location.
	 * 
	 * @param location The location to query.
	 * @return The total number of words at the location. Returns 0 if the location does not exist.
	 */
	@Override
	public long numWordsInLocation(String location) {
		lock.readLock().lock();
		try {
			return super.numWordsInLocation(location);
		} finally {
			lock.readLock().unlock();
		}
	}
}