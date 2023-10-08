package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * Represents an inverted index where words map to their occurrences within files.
 * Words are stemmed and indexed with their positions.
 * 
 * @author Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class InvertedIndex {
	/**
	 * The core data structure of the inverted index.
	 * It maps words to file paths and the positions of the words within those files.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> indexMap;

	/**
	 * Initializes the inverted index data structure.
	 */

	public InvertedIndex() {
		this.indexMap = new TreeMap<>();
	}

	/**
	 * Processes a file, stems its words, and updates the inverted index data structure.
	 * 
	 * @param filePath The path to the file to process.
	 * @throws IOException If an error occurs while reading the file.
	 */
	public void processFile(Path filePath) throws IOException {
		ArrayList<String> stemmedWords = FileStemmer.listStems(filePath);
		int position = 0;

		for (String word : stemmedWords) {
			position++;
			indexMap.putIfAbsent(word, new TreeMap<>());
			indexMap.get(word).putIfAbsent(filePath.toString(), new TreeSet<>());
			indexMap.get(word).get(filePath.toString()).add(position);
		}
	}

	/**
	 * Processes a directory by iterating through its files and updating the inverted index.
	 * Only processes files with .txt or .text extensions.
	 * 
	 * @param dirPath The path to the directory to process.
	 * @throws IOException If an error occurs while reading files within the directory.
	 */
	public void processDirectory(Path dirPath) throws IOException {
		Files.walk(dirPath)
		.filter(Files::isRegularFile)
		.filter(file -> {
			String fileName = file.toString().toLowerCase();
			return fileName.endsWith(".txt") || fileName.endsWith(".text");
		})
		.forEach(file -> {
			try {
				this.processFile(file);
			} catch (IOException e) {
				System.out.println("Error processing file " + file + ": " + e.getMessage());
			}
		});
	}

	/**
	 * Retrieves the current state of the inverted index data structure.
	 * 
	 * @return The inverted index as a nested map.
	 */
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getIndexMap() {
		return indexMap;
	}
}
