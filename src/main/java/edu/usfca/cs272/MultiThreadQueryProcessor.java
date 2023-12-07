package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**The MultiThreadQueryProcessor
 * Is the Multi-threaded version of the original QueryProcessor
 * It utilizes a task class that focuses on making the logic within processQuery(line) 
 * its main task to be processed by workqueue... 
 * 
 * @author Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class MultiThreadQueryProcessor implements QueryInterface {

	/** The InvertedIndex class... */
	private final InvertedIndex index; // TODO Has to be thread-safe

	/** To determine partial/exact search */
	private final boolean isPartial;

	/** The data structure for results from query searches */
	private final TreeMap<String, List<InvertedIndex.FileResult>> resultsMap;

	/** Creating a workQueue */
	private final WorkQueue workQueue;


	/**Constructor to establish the values for index, isPartial, and resultsMap
	 * 
	 * @param index2 is the threadSafeInvertedIndex with threadsafe methods with locks
	 * @param isPartial is a boolean value to determine exact or partial search...
	 * @param workQueue is for thread usages...
	 */
	public MultiThreadQueryProcessor(InvertedIndex index2, boolean isPartial, WorkQueue workQueue) { // TODO Has to be thread-safe, fix name
		this.index = index2;
		this.isPartial = isPartial;
		this.resultsMap = new TreeMap<String, List<InvertedIndex.FileResult>>();
		this.workQueue = workQueue;
	}


	/**Returns true or false depending on if the query exists in the results map
	 * 
	 * @param query string input of a query
	 * @return true or false using containsKey...
	 */
	@Override
	public boolean hasQuery(String query) {
		TreeSet<String> stemmedQueries = FileStemmer.uniqueStems(query);
		String processedQuery = String.join(" ", stemmedQueries);

		synchronized (resultsMap) {
			return resultsMap.containsKey(processedQuery);
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
		TreeSet<String> stemmedQueries = FileStemmer.uniqueStems(query);
		String processedQuery = String.join(" ", stemmedQueries);

		synchronized (resultsMap) {
			return resultsMap.containsKey(processedQuery) && !resultsMap.get(processedQuery).isEmpty();
		}
	}


	/**Calculates the number of FileResults for a given query
	 * 
	 * @param query a string containing a line of queries
	 * @return number of file results for a query, otherwise, 0 if none..
	 */
	@Override
	public int numResultsForQuery(String query) {
		TreeSet<String> stemmedQueries = FileStemmer.uniqueStems(query);
		String processedQuery = String.join(" ", stemmedQueries);

		synchronized (resultsMap) {
			return resultsMap.containsKey(processedQuery) ? resultsMap.get(processedQuery).size() : 0;
		}
	}


	/**Returns an integer number of total queries that was processed
	 * 
	 * @return the size of the resultsMap
	 */
	@Override
	public int numQueriesProcessed() {
		synchronized (resultsMap) {
			return resultsMap.size();
		}
	}


	/**Retrieves an unmodifiable set of all the queries processed.
	 *
	 * @return An unmodifiable set of query strings.
	 */
	@Override
	public Set<String> getQueries() {
		synchronized (resultsMap) {
			return Collections.unmodifiableSet(resultsMap.keySet());
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
		TreeSet<String> stemmedQueries = FileStemmer.uniqueStems(query);
		String processedQuery = String.join(" ", stemmedQueries);

		synchronized(resultsMap) {
			if (resultsMap.containsKey(processedQuery)) {
				return Collections.unmodifiableList(resultsMap.get(processedQuery));
			}
		}
		return Collections.emptyList();
	}

	// TODO @Override
	/**
	 * ProcessQuery is the start of the search exact/partial functionality. It first
	 * creates a list of strings that will hold all the unique queries Then using an
	 * enhanced for loop, checks if each query is not empty, then Checks whether or
	 * not partial boolean flag is given Depending on whether or not partial is
	 * given, we split to either search exact or partial
	 * 
	 * @param queryPath The given path that holds the address to file
	 * @throws IOException throws io exception if issues hit
	 */
	public void processQuery(Path queryPath) throws IOException {
		QueryInterface.super.processQuery(queryPath);
		workQueue.finish();
	}


	/**The query processing logic. This processes one query. Essentially one line.
	 * 
	 * @param line takes in one line of query and adds the result of searching said line into the results map
	 */
	@Override
	public void processQuery(String line) {
		workQueue.execute(new Task((line)));
	}


	/**
	 * Writes the results map to the specified output file in JSON format.
	 *
	 * @param outputPath the path to the output file
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public void writeResults(Path outputPath) throws IOException {
		synchronized (resultsMap) {
			JsonWriter.writeResultsToFile(resultsMap, outputPath);
		}
	}


	/**Task class for processing a query of search requests
	 *
	 */
	public class Task implements Runnable { // TODO private

		/**
		 * the string line to parse
		 */
		private final String line;


		/**
		 * constructor declaration
		 * 
		 * @param line  the line to parse from
		 *
		 */
		public Task(String line) {
			this.line = line;

		}


		/**
		 * the run method to execute with queuer
		 */
		@Override
		public void run() {
			TreeSet<String> cleanedUniqueQueries = FileStemmer.uniqueStems(line);

			String query = String.join(" ", cleanedUniqueQueries);

			synchronized (resultsMap) {
				if (cleanedUniqueQueries.isEmpty() || resultsMap.containsKey(query)) {
					return;
				}
			}

			var local = index.search(cleanedUniqueQueries, isPartial);

			synchronized (resultsMap) {
				resultsMap.put(query, local);
			}
		}
	}
}
