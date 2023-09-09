package edu.usfca.cs272;

import java.time.Duration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.nio.file.FileVisitOption;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.nio.file.FileVisitOption;


/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author TODO Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class Driver {
	public static void main(String[] args) {
		Instant start = Instant.now();

		System.out.println("Command-line arguments: " + Arrays.toString(args));

		Path inputPath = Paths.get(args[1]);
		Path outputPath = Paths.get(args[3]);

		try {
			long wordCount = countWordsInFile(inputPath);

			// Output wordCount in JSON format
			String jsonOutput;
			if (wordCount == 0) {
				jsonOutput = "{\n}";
			} else {
				jsonOutput = String.format("{\n  \"%s\": %d\n}", inputPath.toString(), wordCount);
			}

			Files.writeString(outputPath, jsonOutput);

		} catch (IOException e) {
			e.printStackTrace();
		}

		long elapsed = Duration.between(start, Instant.now()).toMillis();
		double seconds = (double) elapsed / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	public static long countWordsInFile(Path filePath) throws IOException {
		List<String> lines = Files.readAllLines(filePath);
		long wordCount = 0;

		for (String line : lines) {
			// Clean the line using FileStemmer
			String cleanedLine = FileStemmer.clean(line);

			// Split the cleaned line into words
			String[] words = FileStemmer.split(cleanedLine);

			// Count the words
			wordCount += words.length;
		}

		return wordCount;
	}
}
