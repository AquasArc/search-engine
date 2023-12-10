package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**An interface for my multithread and normal search logic
 * 
 */
public interface QueryInterface {

	/**Returns true or false depending on if the query exists in the results map
	 * 
	 * @param query string input of a query
	 * @return true or false using containsKey...
	 */
	boolean hasQuery(String query);

	/**Calculates the number of FileResults for a given query
	 * 
	 * @param query a string containing a line of queries
	 * @return number of file results for a query, otherwise, 0 if none..
	 */
	public default int numResultsForQuery(String query) {
		return getResultsForQuery(query).size();
		
	}

	/**Returns an integer number of total queries that was processed
	 * 
	 * @return the size of the resultsMap
	 */
	public default int numQueriesProcessed() {
		return getQueries().size();
	}

	/**Retrieves an unmodifiable set of all the queries processed.
	 *
	 * @return An unmodifiable set of query strings.
	 */
	Set<String> getQueries();

	/**Retrieves the List of meta data associated to a query that has been processed
	 * 
	 * @param query input to search from the results map
	 * @return either a empty list if the query does not exist, or a unmodifiableList 
	 *  of the metadata associated to the processed query
	 */
	List<InvertedIndex.FileResult> getResultsForQuery(String query);

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
	public default void processQuery(Path queryPath) throws IOException {	
		try (BufferedReader reader = Files.newBufferedReader(queryPath)) {
			String line;

			while ((line = reader.readLine()) != null) {
				processQuery(line);
			}
		}
	}

	/**The query processing logic. This processes one query. Essentially one line.
	 * 
	 * @param line takes in one line of query and adds the result of searching said line into the results map
	 */
	void processQuery(String line);

	/**
	 * Writes the results map to the specified output file in JSON format.
	 *
	 * @param outputPath the path to the output file
	 * @throws IOException if an I/O error occurs
	 */
	void writeResults(Path outputPath) throws IOException;

}