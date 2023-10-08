package edu.usfca.cs272;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides utility functions for processing files, directories, and generating data outputs.
 * Utilizes the InvertedIndex to handle and manage the indexing of words within files.
 * 
 * @author Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class FileProcessor {

	/**
	 * Processes a given input path, checking whether it's a regular file or a directory,
	 * and delegates the task to the appropriate method in the InvertedIndex.
	 * 
	 * @param inputPath The path to either a single file or a directory to process.
	 * @param index The InvertedIndex instance to use for processing.
	 * @throws IOException If an error occurs during file or directory processing.
	 */
	public static void processText(Path inputPath, InvertedIndex index) throws IOException {
		if (Files.isRegularFile(inputPath)) {
			index.processFile(inputPath);
		} else if (Files.isDirectory(inputPath)) {
			index.processDirectory(inputPath);
		} else {
			throw new IOException("Invalid input path: " + inputPath);
		}
	}

	/**
	 * Converts the indexed words and their occurrences into word counts and writes them to a file.
	 * 
	 * @param countPath The path where word counts should be written in JSON format.
	 * @param index The InvertedIndex instance containing the indexed data.
	 * @throws IOException If an error occurs during file writing.
	 */
	public static void processCounts(Path countPath, InvertedIndex index) throws IOException {
		Map<String, Long> wordCounts = generateWordCounts(index);
		JsonWriter.writeWordCountsToFile(wordCounts, countPath);
	}

	/**
	 * Writes the indexed words, their occurrences, and positions within files to a file in a specific JSON format.
	 * 
	 * @param indexPath The path where the index should be written in JSON format.
	 * @param index The InvertedIndex instance containing the indexed data.
	 * @throws IOException If an error occurs during file writing.
	 */
	public static void processIndex(Path indexPath, InvertedIndex index) throws IOException {
		JsonWriter.writeIndexToFile(index.getIndexMap(), indexPath);
	}

	/**
	 * Generates word counts for each file based on the indexed data.
	 * 
	 * @param index The InvertedIndex instance containing the indexed data.
	 * @return A map where each file path is mapped to its respective word count.
	 */
	private static Map<String, Long> generateWordCounts(InvertedIndex index) {
		Map<String, Long> wordCounts = new TreeMap<>();
		for (String word : index.getIndexMap().keySet()) {
			for (String file : index.getIndexMap().get(word).keySet()) {
				wordCounts.put(file, wordCounts.getOrDefault(file, 0L) + index.getIndexMap().get(word).get(file).size());
			}
		}
		return wordCounts;
	}
}
