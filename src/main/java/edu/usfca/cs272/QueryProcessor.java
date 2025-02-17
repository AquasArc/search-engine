package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.IOException;
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
public class QueryProcessor implements QueryInterface {

	/** The InvertedIndex class... */
	private final InvertedIndex index;

	/** To determine partial/exact search */
	private final boolean isPartial;

	/** Made stemmer a member of the class for reusability */
	private final Stemmer stemmer;

	/** The data structure for results from query searches */
	private final TreeMap<String, List<InvertedIndex.FileResult>> resultsMap;
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
		this.stemmer = new SnowballStemmer(ENGLISH);
		this.resultsMap = new TreeMap<String, List<InvertedIndex.FileResult>>();
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
	@Override
	public boolean hasQuery(String query) {
		TreeSet<String> stemmedQueries = FileStemmer.uniqueStems(query, stemmer);
		String processedQuery = String.join(" ", stemmedQueries);
		return resultsMap.containsKey(processedQuery);
	}

	/**Retrieves an unmodifiable set of all the queries processed.
	 *
	 * @return An unmodifiable set of query strings.
	 */
	@Override
	public Set<String> getQueries() {
		return Collections.unmodifiableSet(resultsMap.keySet());
	}

	/**Retrieves the List of meta data associated to a query that has been processed
	 * 
	 * @param query input to search from the results map
	 * @return either a empty list if the query does not exist, or a unmodifiableList 
	 *  of the metadata associated to the processed query
	 */
	@Override
	public List<InvertedIndex.FileResult> getResultsForQuery(String query) {
		TreeSet<String> stemmedQueries = FileStemmer.uniqueStems(query, stemmer);
		String processedQuery = String.join(" ", stemmedQueries);

		if (hasQuery(processedQuery)) {
			return Collections.unmodifiableList(resultsMap.get(processedQuery));
		} 
		return Collections.emptyList();
	}


	/**The query processing logic. This processes one query. Essentially one line.
	 * 
	 * @param line takes in one line of query and adds the result of searching said line into the results map
	 */
	@Override
	public void processQuery(String line) {
		TreeSet<String> cleanedUniqueQueries = FileStemmer.uniqueStems(line, stemmer);

		String query = String.join(" ", cleanedUniqueQueries);

		if (!cleanedUniqueQueries.isEmpty() && !resultsMap.containsKey(query)) {
			List<InvertedIndex.FileResult> sortedResults = index.search(cleanedUniqueQueries, isPartial);

			resultsMap.put(query, sortedResults);
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
		JsonWriter.writeResultsToFile(resultsMap, outputPath);
	}
}