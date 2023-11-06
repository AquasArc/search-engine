package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/*
 * TODO Initialize class members in the class and instance members in the constructor
 */

/**
 * Handles query functionality. Both partial/exact
 * 
 * @author Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class QueryProcessor {

	/** The InvertedIndex class... */
	private final InvertedIndex index;

	/** The data structure for results from query searches */
	private final TreeMap<String, List<FileResult>> resultsMap = new TreeMap<String, List<FileResult>>();
	// { Queries : [Count: _ , Score: _ , where: _ ]}

	/**
	 * Initialize
	 * 
	 * @param index to use invertedindex methods
	 */
	public QueryProcessor(InvertedIndex index) {
		this.index = index;
	}

	// TODO FileStemmer listUniqueStems? or uniqueStems?
	/**
	 * ReadQueries is mainly for cleaning the files that contains the queries that
	 * is being used to search with. Either exact or partial
	 * 
	 * @param queryPath The given argument that is path to file
	 * @return List of strings that holds all the clean/parsed queries
	 * @throws IOException throws exception if issues occur
	 */
	public List<String> readQueries(Path queryPath) throws IOException { // TODO Remove
		return Files.lines(queryPath).map(FileStemmer::clean).filter(line -> !line.matches(".*\\d+.*"))
				.collect(Collectors.toList());
	}

	/**
	 * ProcessQuery is the start of the search exact/partial functionality. It first
	 * creates a list of strings that will hold all the unique queries Then using an
	 * enhanced for loop, checks if each query is not empty, then Checks whether or
	 * not partial boolean flag is given Depending on whether or not partial is
	 * given, we split to either search exact or partial
	 * 
	 * 
	 * @param queryPath The given path that holds the address to file
	 * @param isPartial The boolean value to determine if we do partial search over
	 *                  exact
	 * @return resultsMap Returns the results map with the populated information
	 * @throws IOException throws io exception if issues hit
	 */
	public Map<String, List<FileResult>> processQuery(Path queryPath, boolean isPartial) throws IOException {
		// TODO Is this an appropriate place for outputting console output?
		if (queryPath == null || !Files.exists(queryPath) || !Files.isRegularFile(queryPath)
				|| Files.isDirectory(queryPath)) {
			System.out.println("Error: Missing value for -query flag");
			return null;
		}
		
		List<String> queries = readQueries(queryPath);

		for (String query : queries) {
			if (!query.isEmpty()) {
				TreeSet<String> cleanedUniqueQueries = new TreeSet<>(FileStemmer.uniqueStems(query)); // TODO What is going on here? Shouldn't need the copy?
				List<FileResult> sortedResults;

				TreeMap<String, FileResult> tempMap = new TreeMap<String, FileResult>(); // TODO Make this where it is needed (inside the search methods)

				sortedResults = isPartial ? searchPartial(cleanedUniqueQueries, tempMap)
						: searchExact(cleanedUniqueQueries, tempMap);

				resultsMap.put(String.join(" ", cleanedUniqueQueries), sortedResults);
			}
		}
		resultsMap.remove("");
		return resultsMap;
	}
	
	/*
	 * TODO Think about: 
	 * 
	 * Why does processQuery belong here and not in the inverted index?
	 * - The stemming logic
	 * 
	 * Why does searchExact belong in the inverted index and not here?
	 * - This is based on how the data is stored
	 */
	
	/*
	 * TODO Why are you using 2 data structures for search?
	 * 
	 * TreeMap and a List that both store the same FileResult objects
	 */

	/**
	 * Performs an exact search for cleaned and unique queries and returns a sorted
	 * list of FileResult objects.
	 *
	 * @param cleanedUniqueQueries the cleaned and unique queries
	 * @param inputMap             the map to store the search results
	 * @return a sorted list of FileResult objects
	 */
	public List<FileResult> searchExact(TreeSet<String> cleanedUniqueQueries, TreeMap<String, FileResult> inputMap) { // TODO Can remove map from parameters
		// TODO TreeMap<String, FileResult> tempMap = new TreeMap<String, FileResult>();
		
		for (String word : cleanedUniqueQueries) {
			for (String location : index.getLocations(word)) {
				long totalWords = index.numWordsInLocation(location);
				// TODO Functional will slow you down
				inputMap.computeIfAbsent(location, k -> new FileResult(k, totalWords))
						.incrementCount(index.getPositions(word, location).size());
			}
		}
		return inputMap.values().stream().sorted().collect(Collectors.toList());
	}

	/**
	 * Performs a partial search for cleaned and unique queries and returns a sorted
	 * list of FileResult objects.
	 *
	 * @param cleanedUniqueQueries the cleaned and unique queries
	 * @param inputMap             the map to store the search results
	 * @return a sorted list of FileResult objects
	 */
	public List<FileResult> searchPartial(TreeSet<String> cleanedUniqueQueries, TreeMap<String, FileResult> inputMap) {

		for (String word : cleanedUniqueQueries) {
			// Word within invertedIndex
			// Checking to see if the query word starts with the inverted index word.
			for (String w : index.getWords()) {
				if (w.startsWith(word)) {
					for (String location : index.getLocations(w)) {
						long totalWords = index.numWordsInLocation(location);
						inputMap.computeIfAbsent(location, k -> new FileResult(k, totalWords))
								.incrementCount(index.getPositions(w, location).size());
					}
				}
			}
		}

		// Convert the map values to a sorted list
		return inputMap.values().stream().sorted().collect(Collectors.toList());
	}

	/**
	 * Writes the results map to the specified output file in JSON format.
	 *
	 * @param outputPath the path to the output file
	 * @throws IOException if an I/O error occurs
	 */
	public void writeResults(Path outputPath) throws IOException {
		JsonWriter.writeResultsToFile(resultsMap, outputPath);
	}
}