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

		Path inputPath = null;
		Path outputPath = null;
		Path indexPath = null;
		boolean countsFlagProvided = parser.hasFlag("-counts");
		boolean indexFlagProvided = parser.hasFlag("-index");
		Map<String, Map<String, List<Integer>>> indexMap = new TreeMap<String, Map<String, List<Integer>>>();
		
		// If -text flag is provided
		if (parser.hasFlag("-text")) {
			inputPath = parser.getPath("-text");
			try {
				// Handle input...
			} catch (Exception e) {
				System.out.println("Could not process the file(s) at the input path: " + inputPath);
			}
		}

		// If -counts flag is provided
		if (countsFlagProvided) {
			outputPath = parser.getPath("-counts");

			// Set default output path only if both -text and -counts are provided
			if (outputPath == null && inputPath != null) {
				outputPath = Paths.get("counts.json"); // Default
			}

			try {
				// Handle output...
			} catch (Exception e) {
				System.out.println("Failed to write to the output file: " + outputPath);
			}
		}
		
		if (indexFlagProvided) {
			indexPath = parser.getPath("-index");
			
			// Set default index path only if both -text and -counts are provided
			if (indexPath == null && inputPath != null) {
				indexPath = Paths.get("index.json"); // Default
			}
			
			try {
				FileProcessor.fileOrDirIndex(inputPath,indexPath, indexMap);
				//updateInvertedIndex(inputPath, indexMap);
				System.out.println("Main to see indexMap: " + indexMap);
				//writeNestedMapToFile(indexMap, indexPath);
			} catch (Exception e) {
				System.out.println("Failed to write to the index file(index1) : " + indexPath);
			}
		}
		

		// If only -counts flag is provided, write an empty file
		if (inputPath == null && countsFlagProvided) {
			if (outputPath == null) {
				outputPath = Paths.get("counts.json"); // Set a default if none provided
			}
			try {
				Files.writeString(outputPath, "{}");
			} catch (IOException e) {
				System.out.println("Failed to write empty JSON file.");
			}
			return;
		}

		// If only -index flag is provided, write an empty file
		if (inputPath == null && indexFlagProvided) {
			if (outputPath == null) {
				outputPath = Paths.get("index.json"); // Set a default if none provided
			}
			try {
				Files.writeString(outputPath, "{}");
			} catch (IOException e) {
				System.out.println("Failed to write empty JSON file.");
			}
			return;
		}
		

		// If only -text is provided and neither -counts nor -output is present return for now..
		// [Maybe] Check if -index is provided, if it isn't return and do nothing
		if (inputPath != null && outputPath == null && !countsFlagProvided && !indexFlagProvided) {
			return;
		}



		// Print the paths for debugging
		System.out.println("Parsed Input Path: " + (inputPath == null ? "null" : inputPath.toString()));
		System.out.println("Parsed Output Path: " + (outputPath == null ? "null" : outputPath.toString()));
		System.out.println("Parsed Index Path: " + (indexPath == null ? "null" : indexPath.toString()));
		
		if (indexPath != null) {
			//FileProcessor.processIndex(inputPath, indexPath);
		}
		if (outputPath != null) {
			FileProcessor.processInput(inputPath, outputPath);
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
	

	public static void updateInvertedIndex(Path filePath, Map<String, Map<String, List<Integer>>> indexMap) {
	    ArrayList<String> stemmedWords;
	    System.out.println("This is the filepath: " + filePath.toString());
	    try {
	        stemmedWords = FileStemmer.listStems(filePath);
	        System.out.println("Stemmed words: " + stemmedWords);  // Debugging line
	    } catch (IOException e) {
	        System.out.println("Error reading file(uII): " + filePath.toString());
	        System.out.println("Exception: " + e.getMessage());  // Debugging line
	        return;
	    }

	    int wordPosition = 0;
	    for (String word : stemmedWords) {
	        wordPosition++;

	        indexMap.putIfAbsent(word, new TreeMap<>());
	        indexMap.get(word).putIfAbsent(filePath.toString(), new ArrayList<>());
	        indexMap.get(word).get(filePath.toString()).add(wordPosition);
	    }
	}

	public static void writeNestedMapToFile(Map<String, Map<String, List<Integer>>> indexMap, Path indexPath) {
		try (BufferedWriter writer = Files.newBufferedWriter(indexPath)) {
			// Check if the map is empty
			if (indexMap.isEmpty()) {
				writer.write("{\n}");
				return;
			}
			writer.write("{\n");  // Start of the JSON object

			boolean isFirstOuter = true;
			for (Map.Entry<String, Map<String, List<Integer>>> outerEntry : indexMap.entrySet()) {
				if (!isFirstOuter) {
					writer.write(",\n");
				}
				isFirstOuter = false;

				writer.write("  \"" + outerEntry.getKey() + "\": {\n");  // Outer key

				boolean isFirstInner = true;
				for (Map.Entry<String, List<Integer>> innerEntry : outerEntry.getValue().entrySet()) {
					if (!isFirstInner) {
						writer.write(",\n");
					}
					isFirstInner = false;

					writer.write("    \"" + innerEntry.getKey() + "\": [\n");  // Inner key

					List<Integer> values = innerEntry.getValue();
					for (int i = 0; i < values.size(); i++) {
						writer.write("      " + values.get(i));
						if (i < values.size() - 1) {
							writer.write(",\n");
						} else {
							writer.write("\n    ]");  // Close the array and indent it
						}
					}
				}
				writer.write("\n  }");  // Close inner JSON object
			}
			writer.write("\n}");  // Close outer JSON object
		} catch (IOException e) {
			System.out.println("Failed to write to the file: " + indexPath);
		}
	}
}