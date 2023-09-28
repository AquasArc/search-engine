package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.nio.file.FileVisitOption;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;

// TODO Driver should just have a main method.... everything else is generally useful and should be in another class

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class Driver {

	/**
	 * Start of the program.
	 *
	 * @param args Command-line arguments
	 */
	public static void main(String[] args) {
		// Print initial arguments for debugging
		System.out.println("Initial args: " + Arrays.toString(args));
		ArgumentParser parser = new ArgumentParser(args);

		boolean countsFlagProvided = parser.hasFlag("-counts");
		boolean indexFlagProvided = parser.hasFlag("-index");
		
		/*
		 * TODO 
		 * 
		 * TreeMap<String, TreeMap<String, TreeSet<Integer>>> indexMap
		 * 
		 *  ...move this into a data structure class called InvertedIndex and add with it the word counts map
		 */
		Map<String, Map<String, List<Integer>>> indexMap = new TreeMap<String, Map<String, List<Integer>>>();
		
		// If -text flag is provided, parse & get the path
		if (parser.hasFlag("-text")) {
			Path inputPath = parser.getPath("-text");
			
			// Check if counts flag is provided
			if(countsFlagProvided) {
				Path outputPath = parser.getPath("-counts");
				
				// Check if the path wasn't provided, if not create a default...
				if (outputPath == null) {
					outputPath = Paths.get("counts.json"); // Default
				}
				
				FileProcessor.fileOrDirCount(inputPath, outputPath);
			}
			
			// Check if index flag is provided
			if (indexFlagProvided) {
				Path indexPath = parser.getPath("-index");
				
				// Check if the path wasn't provided, if not create a default...
				if(indexPath == null) {
					indexPath = Paths.get("index.json");
				}
				
				FileProcessor.fileOrDirIndex(inputPath, indexPath, indexMap);
			}
		} else {
			if (!parser.hasFlag("-text")) {
				if (countsFlagProvided) {
					Path outputPath = parser.getPath("-counts");
					
					if (outputPath == null) {
						outputPath = Paths.get("counts.json");
					}
					
					try {
						Files.writeString(outputPath, "{}");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(indexFlagProvided) {
					Path indexPath = parser.getPath("index.json");
					
					if (indexPath == null) {
						indexPath = Paths.get("index.json");
					}
					
					try {
						Files.writeString(indexPath, "{}");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Processes a single file and returns its word count.
	 *
	 * @param filePath Path of the file to process
	 * @return The word count of the file
	 */
	public static long processFile(Path filePath) {
		long wordCount = 0;

		// Check if the filePath is a regular file
		if (!Files.isRegularFile(filePath)) {
			System.out.println("The provided path is not a file: " + filePath);
			return 0;
		}

		try {
			// Count words in the file
			wordCount = countWordsInFile(filePath);
		} catch (IOException e) {
			System.out.println("An error occurred while reading the file: " + filePath);
		}

		return wordCount;
	}

	/**
	 * Processes a directory and writes word counts to an output file.
	 *
	 * @param dirPath Path of the directory to process
	 * @param outputPath Path of the output file
	 */
	public static void processDirectory(Path dirPath, Path outputPath) {
		try (Stream<Path> paths = Files.walk(dirPath, FileVisitOption.FOLLOW_LINKS)) {
			List<Path> filteredPaths = paths
					.filter(Files::isRegularFile)
					.filter(path -> path.toString().toLowerCase().endsWith(".txt") || path.toString().toLowerCase().endsWith(".text"))
					.collect(Collectors.toList());

			filteredPaths.sort((p1, p2) -> p1.toString().compareTo(p2.toString()));  // Sorting the string representations



			try (Writer writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
				writer.write("{\n"); // Opening curly brace for JSON object

				boolean firstEntry = true;
				for (Path path : filteredPaths) {
					// TODO Wrong place to calculate, the specification stated this needs to be calculated with the index when you read a file for the first time
					long wordCount = countWordsInFile(path);  // Check existing word methods

					if (wordCount > 0) {
						if (!firstEntry) {
							writer.write(",\n");  // Comma and newline for previous entry
						}
						firstEntry = false;

						writer.write("  \"");  // Indent and start of key
						writer.write(path.toString());
						writer.write("\": ");
						writer.write(Long.toString(wordCount));
					}
				}

				writer.write("\n}");  // Close the JSON object
			} catch (IOException e) {
				System.out.println("An error occurred while writing to the output file: " + outputPath);
			}
		} catch (IOException e) {
			System.out.println("An error occurred while reading the directory: " + dirPath);
		}
	}

	/**
	 * Counts the number of words in a file.
	 *
	 * @param filePath Path of the file to count words in
	 * @return The word count of the file
	 * @throws IOException If file reading fails
	 */
	public static long countWordsInFile(Path filePath) throws IOException {
		List<String> lines = Files.readAllLines(filePath);
		long wordCount = 0;

		for (String line : lines) {
			// Using the FileStemmer methods
			String cleanedLine = FileStemmer.clean(line);  
			String[] words = FileStemmer.split(cleanedLine);

			// Count the words
			wordCount += words.length;
		}

		return wordCount;
	}

	/**
	 * Reads the given text file, stems the words, and then adds them to 
	 * the nested Map 'indexMap' along with their positions
	 *
	 * @param filePath The Path of the text file to read
	 * @param indexMap The nested Map to update with stemmed words and their positions
	 */
	public static void updateInvertedIndex(Path filePath, Map<String, Map<String, List<Integer>>> indexMap) {
		ArrayList<String> stemmedWords;
		System.out.println("This is the filepath: " + filePath.toString());
		try {
			stemmedWords = FileStemmer.listStems(filePath);
			System.out.println("Stemmed words: " + stemmedWords);  // Debugging line
		} catch (IOException e) { // TODO Wrong place for try/catch... most methods will throw exceptions (except Driver.main)
			System.out.println("Error reading file(uII): " + filePath.toString());
			System.out.println("Exception: " + e.getMessage());  // Debugging line
			return;
		}

		int wordPosition = 0;
		for (String word : stemmedWords) {
			wordPosition++;

			// TODO Make this a method in your new inverted index class
			indexMap.putIfAbsent(word, new TreeMap<>());
			indexMap.get(word).putIfAbsent(filePath.toString(), new ArrayList<>());
			indexMap.get(word).get(filePath.toString()).add(wordPosition);
		}
		
		// TODO update the word count here based on the wordPosition
	}
}