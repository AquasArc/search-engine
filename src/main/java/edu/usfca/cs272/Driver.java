package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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
		boolean countsFlagProvided = parser.hasFlag("-counts");

		// If -text flag is provided
		if (parser.hasFlag("-text")) {
			inputPath = parser.getPath("-text");
			try {
				// Handle input...
			} catch (Exception e) {
				System.out.println("Could not process the file(s) at the input path: " + inputPath);
			}
		}

		// If only -text is provided, don't create any output files
		if (inputPath != null && outputPath == null && !countsFlagProvided) {
			return;
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

		// Print the paths for debugging
		System.out.println("Parsed Input Path: " + (inputPath == null ? "null" : inputPath.toString()));
		System.out.println("Parsed Output Path: " + (outputPath == null ? "null" : outputPath.toString()));

		FileProcessor.processInput(inputPath, outputPath);
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
}