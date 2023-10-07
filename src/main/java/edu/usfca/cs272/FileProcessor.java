package edu.usfca.cs272;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * The FileProcessor class is responsible for differentiating/splitting
 * the responsibilities of handling files and directories.
 * 
 * 
 * 
 *
 * @author Anton Lim
 * @version Fall 2023
 */
public class FileProcessor {

	/**
	 * Processes the input path and writes output if needed.
	 * 
	 * @param inputPath  The path of the file or directory to process.
	 * @param outputPath The path where the output should be written.
	 * @throws IOException If an error occurs while reading the file
	 */
	public static void fileOrDirCount(Path inputPath, Path outputPath) throws IOException {
		if (inputPath != null) {
			if (Files.isRegularFile(inputPath)) {
				long wordCount = processFile(inputPath);

				if (outputPath != null) {
					writeOutput(inputPath, outputPath, wordCount);
				}
			} else if (Files.isDirectory(inputPath)) {
				if (outputPath != null) {
					processDirectory(inputPath, outputPath);
				}
			} else {
				System.out.println("Invalid input path");
			}
		}
	}

	// Reminder: None of the methods should take both an input and output path at the same time

	/* Working on
	public static void processFile(Path file, InvertedIndex index) throws IOException {
		stem add to the index and update the count
	}
	 */

	/**
	 * Processes a single file and returns its word count.
	 *
	 * @param filePath Path of the file to process
	 * @return The word count of the file
	 * @throws IOException If an error occurs while reading the file
	 */
	public static long processFile(Path filePath) throws IOException {
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
	 * I need to update it to just call processFile per file...
	 *
	 * @param dirPath Path of the directory to process
	 * @param outputPath Path of the output file
	 */
	public static void processDirectory(Path dirPath, Path outputPath) { // TODO Don't create methods that both process input and produce output
		// TODO Avoid functional for project 1
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
					// Reminder Must Change: Wrong place to calculate, the specification stated this needs to be calculated with the index when you read a file for the first time
					long wordCount = FileProcessor.countWordsInFile(path);  // Check existing word methods

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
	 * Writes the word count output to a file.
	 * 
	 * @param inputPath  The path of the input file.
	 * @param outputPath The path where the output should be written.
	 * @param wordCount  The word count to write.
	 */
	private static void writeOutput(Path inputPath, Path outputPath, long wordCount) {
		String jsonOutput;
		if (wordCount == 0) {
			jsonOutput = "{\n}";
		} else {
			jsonOutput = String.format("{\n  \"%s\": %d\n}", inputPath.toString(), wordCount);
		}

		try {
			System.out.println("About to write to: " + outputPath.toString());
			System.out.println("Content to write: \n" + jsonOutput);
			Files.writeString(outputPath, jsonOutput);
		} catch (IOException e) {
			System.out.println("Error writing to output file: " + outputPath);
		}
	}


	/**Reminder, might need to replace in future
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