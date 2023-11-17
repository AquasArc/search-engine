package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;


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

	/** To determine partial/exact search */
	private final boolean isPartial;

	/** The data structure for results from query searches */
	private final TreeMap<String, List<FileResult>> resultsMap;
	// { Queries : [Count: _ , Score: _ , where: _ ]}

	/**
	 * Initialize
	 * 
	 * @param index to use inverted index methods
	 * @param isPartial to determine either partial/exact search
	 */
	public QueryProcessor(InvertedIndex index, boolean isPartial) {
		this.index = index;
		this.isPartial = isPartial;
		this.resultsMap = new TreeMap<String, List<FileResult>>();
	}
	
	/** A toString method prints inverted index contents
	 * 
	 *@returns to string value of the inverted index 
	 */
	@Override
	public String toString() {
		return resultsMap.toString();
	}
	
	/**Returns true or false depending on if the query exists in the results map
	 * 
	 * @param query string input of a query
	 * @return true or false using containsKey...
	 */
	public boolean hasQuery(String query) {
	// TODO Need to stem and join before accessing the query in the map
	    return resultsMap.containsKey(query);
	}
	
	/**
	 * Checks if a specific query has any associated FileResult objects.
	 *
	 * @param query The query to check.
	 * @return True if the query has one or more FileResult objects, false otherwise.
	 */
	public boolean hasFileResults(String query) {
	// TODO Need to stem and join before accessing the query in the map
	    return resultsMap.containsKey(query) && !resultsMap.get(query).isEmpty();
	}
	
	/**Calculates the number of FileResults for a given query
	 * 
	 * @param query a string containing a line of queries
	 * @return number of file results for a query, otherwise, 0 if none..
	 */
	public int numResultsForQuery(String query) {
		// TODO Need to stem and join before accessing the query in the map
	    return resultsMap.containsKey(query) ? resultsMap.get(query).size() : 0;
	}

	/**Returns an integer number of total queries that was processed
	 * 
	 * @return the size of the resultsMap
	 */
	public int numQueriesProcessed() {
	    return resultsMap.size();
	}
	
	/**
	 * Retrieves an unmodifiable set of all the queries processed.
	 *
	 * @return An unmodifiable set of query strings.
	 */
	public Set<String> getQueries() {
	    return Collections.unmodifiableSet(resultsMap.keySet());
	}
	
	// TODO Need a safe way of getting results without breaking encapsulation

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
		Stemmer stemmer = new SnowballStemmer(ENGLISH); // TODO Make a member of the class so you can reuse more
		
		try (BufferedReader reader = Files.newBufferedReader(queryPath)) {
			String line;

			while ((line = reader.readLine()) != null) {
				// TODO Move the logic here into processQuery(String line)
				TreeSet<String> cleanedUniqueQueries = FileStemmer.uniqueStems(line, stemmer);
				

				String query = String.join(" ", cleanedUniqueQueries);
				
				if (!cleanedUniqueQueries.isEmpty() && !resultsMap.containsKey(query)) {
					List<FileResult> sortedResults = isPartial ? index.searchPartial(cleanedUniqueQueries)
							: index.searchExact(cleanedUniqueQueries);

					resultsMap.put(query, sortedResults);
				}
			}
		}
	}
	
	/* TODO 
	public void processQuery(String line) {
		
	}
	*/

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