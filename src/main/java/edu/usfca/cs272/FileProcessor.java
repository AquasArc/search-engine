package edu.usfca.cs272;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class FileProcessor {

	/**
	 * Processes the input path and writes output if needed.
	 * 
	 * @param inputPath  The path of the file or directory to process.
	 * @param outputPath The path where the output should be written.
	 */
	public static void processInput(Path inputPath, Path outputPath) {
		if (inputPath != null) {
			if (Files.isRegularFile(inputPath)) {
				long wordCount = Driver.processFile(inputPath);

				if (outputPath != null) {
					writeOutput(inputPath, outputPath, wordCount);
				}
			} else if (Files.isDirectory(inputPath)) {
				if (outputPath != null) {
					Driver.processDirectory(inputPath, outputPath);
				}
			} else {
				System.out.println("Invalid input path");
			}
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

	/**
	 * Placeholder method to demonstrate how to integrate with the -index flag.
	 *
	 * @param indexPath The path where the index should be written.
	 */
	public static void processIndex(Path indexPath) {
		// Printing path...
		System.out.println("Processing index: " + indexPath.toString());
	}
}