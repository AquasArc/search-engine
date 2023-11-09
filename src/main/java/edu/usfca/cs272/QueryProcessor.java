package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;


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
	private final TreeMap<String, List<FileResult>> resultsMap;
	// { Queries : [Count: _ , Score: _ , where: _ ]}

	/**
	 * Initialize
	 * 
	 * @param index to use invertedindex methods
	 */
	public QueryProcessor(InvertedIndex index) {
		this.index = index;
		this.resultsMap = new TreeMap<String, List<FileResult>>();
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
		try (BufferedReader reader = Files.newBufferedReader(queryPath)) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            TreeSet<String> cleanedUniqueQueries = FileStemmer.uniqueStems(line);
	            if (!cleanedUniqueQueries.isEmpty()) {
	                List<FileResult> sortedResults = isPartial ? index.searchPartial(cleanedUniqueQueries)
	                                                           : index.searchExact(cleanedUniqueQueries);
	                resultsMap.put(String.join(" ", cleanedUniqueQueries), sortedResults);
	            }
	        }
	    }
		resultsMap.remove("");
		return resultsMap;
	}
	
	/*
	 * Think about: 
	 * 
	 * Why does processQuery belong here and not in the inverted index?
	 * - The stemming logic
	 * - We want to keep the inverted index general. 
	 * - The inverted index will still work even if we don't stem words
	 * - Its just an extra step of pre-processing that we are doing
	 * - So we want to keep the data structure nice and general
	 * - Inverted Index data structure can store stem words, non-stem words, even
	 * 	 phrases like n-grams
	 * - We want the data structure as general as possible
	 * - This is specific how we are doing the processing. Like how we want to do
	 *   stemming first.
	 * - Which will help us improve our search results later.
	 * - We keep processing step/cleaning step outside of the data structure, 
	 *   so that the data structure cares only about the storing logic...
	 *   
	 * Why does searchExact belong in the inverted index and not here?
	 * - This is based on how the data is stored
	 * - While processQuery stays inside the QueryProcessor because its main
	 *   functionality is just to stem, parse, clean, etc
	 * - searchExact and searchPartial needs to stay inside the inverted index because
	 *   their functionality more leans to how information is stored...
	 */
	
	/*
	 * Why are you using 2 data structures for search?
	 * 
	 * TreeMap and a List that both store the same FileResult objects
	 * 
	 * This was terrible coding on my part, and too wasteful/redundant. My 
	 * Original thought was having a TreeMap that would store the FileResults objects
	 * 
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