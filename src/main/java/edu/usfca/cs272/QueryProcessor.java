package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
	public QueryProcessor(InvertedIndex index, Boolean isPartial) { // TODO boolean
		this.index = index;
		this.isPartial = isPartial;
		this.resultsMap = new TreeMap<String, List<FileResult>>();
	}

	/**
	 * ProcessQuery is the start of the search exact/partial functionality. It first
	 * creates a list of strings that will hold all the unique queries Then using an
	 * enhanced for loop, checks if each query is not empty, then Checks whether or
	 * not partial boolean flag is given Depending on whether or not partial is
	 * given, we split to either search exact or partial
	 * 
	 * @param queryPath The given path that holds the address to file
	 * @return resultsMap Returns the results map with the populated information
	 * @throws IOException throws io exception if issues hit
	 */
	public void processQuery(Path queryPath) throws IOException {
		Stemmer stemmer = new SnowballStemmer(ENGLISH);
		
		try (BufferedReader reader = Files.newBufferedReader(queryPath)) {
			String line;

			while ((line = reader.readLine()) != null) {
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