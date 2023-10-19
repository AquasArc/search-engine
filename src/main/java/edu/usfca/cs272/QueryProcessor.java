package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class QueryProcessor {

	private final InvertedIndex index;

	public QueryProcessor(InvertedIndex index) {
		this.index = index;
	}

	public List<String> readQueries(Path queryPath) throws IOException {
		return Files.lines(queryPath)
				.map(FileStemmer::clean)
				.filter(line -> !line.matches(".*\\d+.*"))
				.collect(Collectors.toList());
	}

	public Map<String, List<FileResult>> processQuery(Path queryPath, boolean isPartial) throws IOException {

		Map<String, List<FileResult>> resultMap = new TreeMap<>();
		List<String> queries = readQueries(queryPath);

		for (String query : queries) {
			if (!query.isEmpty()) {
				TreeSet<String> processedQuery = new TreeSet<>(processSingleQuery(query));
				List<FileResult> sortedResults;

				sortedResults = isPartial ? searchPartial(processedQuery) : searchExact(processedQuery);

				resultMap.put(String.join(" ", processedQuery), sortedResults);
			}
		}
		resultMap.remove("");
		return resultMap;
	}

	private TreeSet<String> processSingleQuery(String query) {
		return FileStemmer.uniqueStems(query);
	}


	public List<FileResult> searchExact(TreeSet<String> processedQuery) {
		Map<String, FileResult> resultsMap = new TreeMap<>();

		for (String word : processedQuery) {
			Map<String, TreeSet<Integer>> locations = index.getLocations(word);
			for (String location : locations.keySet()) {
				int totalWords = index.getTotalWordsForLocation(location);
				resultsMap.computeIfAbsent(location, k -> new FileResult(k, totalWords))
				.incrementCount(locations.get(location).size());
			}
		}

		// Convert the map values to a sorted list
		return resultsMap.values().stream()
				.sorted()
				.collect(Collectors.toList());
	}
	
	public List<FileResult> searchPartial(TreeSet<String> processedQuery) {
	      Map<String, FileResult> resultsMap = new TreeMap<>();

	      for (String word : processedQuery) {

	        // Word within invertedIndex
	        // Checking to see if the query word starts with the inverted index word.
	        for (String w : index.getIndexMap().keySet()) {
	          if (w.startsWith(word)) {
	            Map<String, TreeSet<Integer>> locations = index.getLocations(w);
	            for (String location : locations.keySet()) {
	                int totalWords = index.getTotalWordsForLocation(location);
	                resultsMap.computeIfAbsent(location, k -> new FileResult(k, totalWords))
	                .incrementCount(locations.get(location).size());
	            }
	          }
	        }
	      }

	      // Convert the map values to a sorted list
	      return resultsMap.values().stream()
	              .sorted()
	              .collect(Collectors.toList());
	  }

	public void writeResults(Map<String, List<FileResult>> results, Path outputPath) throws IOException {
		JsonWriter.writeResultsToFile(results, outputPath);
	}
}
