package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;


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
	 */
	public QueryProcessor(InvertedIndex index, Boolean isPartial) {
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
	 * 
	 * @param queryPath The given path that holds the address to file
	 * @param isPartial The boolean value to determine if we do partial search over
	 *                  exact
	 * @return resultsMap Returns the results map with the populated information
	 * @throws IOException throws io exception if issues hit
	 */
	public Map<String, List<FileResult>> processQuery(Path queryPath) throws IOException {
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
		return resultsMap;
	}
	// Why not this: public Map<String, List<FileResult>> processQuery(Path queryPath, boolean isPartial, InvertedIndex index) throws IOException {
	// Because we want the data structure to hold data from search from either partial or exact
	// We don't want the data structure to contain mixed data from both
	// Because its possible to get different values for a specific query
	
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
	 * While removing duplicates and placing them in alphabetical order
	 * And same for the key sets as well
	 * 
	 * Then id use the values for the List to write the results. I thought it would be
	 * more straightforward/easier to do
	 * 
	 * But how id improve upon this:
	 * Change the return type of search (partial and exact) to a list<FileResults>
	 * -  This will remove the unnecessary usage of a Tree<String> that holds location during search
	 * 
	 * 
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