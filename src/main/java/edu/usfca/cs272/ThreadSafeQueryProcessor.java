package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

// TODO Remove this, make your multithreaded version also thread-safe
/**
 * Represents a thread safe version of the query processor
 * 
 * @author Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class ThreadSafeQueryProcessor extends QueryProcessor {

	/** Creating a MultiReaderLock object */
	private final MultiReaderLock lock;


	/** The constructor... follows the same structure as original QueryProcessor
	 * 
	 * @param index to utilize the inverted index methods...
	 * @param isPartial determines partial/exact search...
	 */
	public ThreadSafeQueryProcessor(InvertedIndex index, boolean isPartial) {
		super(index, isPartial);
		this.lock = new MultiReaderLock();
	}

	@Override
	/** To string method... 
	 * 
	 * @return toString method..
	 */
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**Returns true or false depending on if the query exists in the results map
	 * 
	 * @param query string input of a query
	 * @return true or false using containsKey...
	 */
	@Override
	public boolean hasQuery(String query) {
		lock.readLock().lock();
		try {
			return super.hasQuery(query);
		} finally {
			lock.readLock().unlock();
		}
	}


	/**
	 * Checks if a specific query has any associated FileResult objects.
	 *
	 * @param query The query to check.
	 * @return True if the query has one or more FileResult objects, false otherwise.
	 */
	@Override
	public boolean hasFileResults(String query) {
		lock.readLock().lock();
		try {
			return super.hasFileResults(query);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**Calculates the number of FileResults for a given query
	 * 
	 * @param query a string containing a line of queries
	 * @return number of file results for a query, otherwise, 0 if none..
	 */
	@Override
	public int numResultsForQuery(String query) {
		lock.readLock().lock();
		try {
			return super.numResultsForQuery(query);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**Returns an integer number of total queries that was processed
	 * 
	 * @return the size of the resultsMap
	 */
	@Override
	public int numQueriesProcessed() {
		lock.readLock().lock();
		try {
			return super.numQueriesProcessed();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**Retrieves an unmodifiable set of all the queries processed.
	 *
	 * @return An unmodifiable set of query strings.
	 */
	@Override
	public Set<String> getQueries() {
		lock.readLock().lock();
		try {
			return super.getQueries();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**Retrieves the List of meta data associated to a query that has been processed
	 * 
	 * @param query input to search from the results map
	 * @return either a empty list if the query does not exist, or a unmodifiableList 
	 *  of the metadata associated to the processed query
	 */
	@Override
	public List<InvertedIndex.FileResult> getResultsForQuery(String query) {
		lock.readLock().lock();
		try {
			return super.getResultsForQuery(query);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * ProcessQuery is the start of the search exact/partial functionality. It first
	 * creates a list of strings that will hold all the unique queries Then using an
	 * enhanced for loop, checks if each query is not empty, then Checks whether or
	 * not partial boolean flag is given Depending on whether or not partial is
	 * given, we split to either search exact or partial
	 * 
	 * @param line a singular query input...
	 */
	@Override
	public void processQuery(String line) {
		lock.writeLock().lock();
		try {
			super.processQuery(line);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Writes the results map to the specified output file in JSON format.
	 *
	 * @param outputPath the path to the output file
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public void writeResults(Path outputPath) throws IOException {
		lock.readLock().lock();
		try {
			super.writeResults(outputPath);
		} finally {
			lock.readLock().unlock();
		}
	}

}
