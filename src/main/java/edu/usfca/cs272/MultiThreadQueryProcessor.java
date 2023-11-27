package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
public class MultiThreadQueryProcessor {

	/** The InvertedIndex class... */
	private final InvertedIndex index;

	/** To determine partial/exact search */
	private final boolean isPartial;

	/** The data structure for results from query searches */
	private final TreeMap<String, List<InvertedIndex.FileResult>> resultsMap;


	/**Constructor to establish the values for index, isPartial, and resultsMap
	 * 
	 * @param index is the threadSafeInvertedIndex with threadsafe methods with locks
	 * @param isPartial is a boolean value to determine exact or partial search...
	 */
	public MultiThreadQueryProcessor(ThreadSafeInvertedIndex index, boolean isPartial) {
		this.index = index;
		this.isPartial = isPartial;
		this.resultsMap = new TreeMap<String, List<InvertedIndex.FileResult>>();
	}

	/**
	 * ProcessQuery is the start of the search exact/partial functionality. It first
	 * creates a list of strings that will hold all the unique queries Then using an
	 * enhanced for loop, checks if each query is not empty, then Checks whether or
	 * not partial boolean flag is given Depending on whether or not partial is
	 * given, we split to either search exact or partial
	 * 
	 * @param queryPath The given path that holds the address to file
	 * @param workQueue The workqueue class that will be used to execute task...
	 * @throws IOException throws io exception if issues hit
	 */
	public void processQuery(Path queryPath, WorkQueue workQueue) throws IOException {	
		try (BufferedReader reader = Files.newBufferedReader(queryPath)) {
			String line;

			while ((line = reader.readLine()) != null) {
				workQueue.execute(new Task(line, isPartial)); //processQuery(line) was originally here, treating this as the main task...
			}
		}
		workQueue.finish();
	}

	/**
	 * Writes the results map to the specified output file in JSON format.
	 *
	 * @param outputPath the path to the output file
	 * @throws IOException if an I/O error occurs
	 */
	public void writeResults(Path outputPath) throws IOException {
		synchronized (resultsMap) {
			JsonWriter.writeResultsToFile(resultsMap, outputPath);
		}
	}

	/**Task class for processing a query of search requests
	 *
	 */
	public class Task implements Runnable {

		/**
		 * the string line to parse
		 */
		private final String line;

		/**
		 * boolean to determine search type
		 */
		private final boolean isPartial;

		/**
		 * constructor declaration
		 * 
		 * @param line  the line to parse from
		 * @param isPartial the boolean to see if we are doing exact search or not 
		 *
		 */
		public Task(String line, boolean isPartial) {
			this.line = line;
			this.isPartial = isPartial;

		}

		@Override
		/**
		 * the run method to execute with queuer
		 */
		public void run() {
			TreeSet<String> cleanedUniqueQueries = FileStemmer.uniqueStems(line);

			String query = String.join(" ", cleanedUniqueQueries);

			if (!cleanedUniqueQueries.isEmpty() && !resultsMap.containsKey(query)) {
				var local = isPartial ? index.searchPartial(cleanedUniqueQueries)
						: index.searchExact(cleanedUniqueQueries);

				synchronized (resultsMap) {
					resultsMap.put(query, local);
				}
			}
		}
	}
}
