package edu.usfca.cs272;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.io.IOException;

public class FileProcessor {

	/**
	 * Processes the input path and writes output if needed.
	 * 
	 * @param inputPath  The path of the file or directory to process.
	 * @param outputPath The path where the output should be written.
	 */
	public static void fileOrDirCount(Path inputPath, Path outputPath) {
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
	 * Takes in a inputPath to check if it is null, if its not
	 * then it checks the inputPath. Checks if its a file or directory.
	 * Depending on what it is, direct the inputs to the corresponding helper method
	 * 
	 * @param inputPath: The path of the file or directory to process.
	 * @param indexPath: The path where the output should be written.
	 * @param indexMap: The nested map that contains all of the words and their positions 
	 */
	public static void fileOrDirIndex(Path inputPath,Path indexPath, Map<String, Map<String, List<Integer>>> indexMap ) {
		if (inputPath != null) {
			if (Files.isRegularFile(inputPath)) {
				Driver.updateInvertedIndex(inputPath, indexMap);
				JsonWriter.writeNestedMapToFile(indexMap, indexPath);
			} else if (Files.isDirectory(inputPath)) {
				processDirectoryIndex(inputPath, indexPath, indexMap);
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
	 * Writes the data that is in a nested map into a given file in a pretty json format
	 * It does this by, taking in a directory as an argument
	 * Then looping through the directory going to each file,
	 * and if necessary all of the children directories and their files,
	 * and applying the file helper method that is used to write to a file
	 * 
	 * @param inputPath  The path of the input directory.
	 * @param indexPath The path where the output should be written.
	 * @param indexMap The nested map with all the data...
	 */
	public static void processDirectoryIndex(Path inputDir, Path indexPath, Map<String, Map<String, List<Integer>>> indexMap) {
		try {
			Files.walk(inputDir)
			.forEach(path -> {
				if (Files.isRegularFile(path)) {
					String fileName = path.toString().toLowerCase();
					if (fileName.endsWith(".txt") || fileName.endsWith(".text")) {
						Driver.updateInvertedIndex(path, indexMap);
					}
				}
			});
			JsonWriter.writeNestedMapToFile(indexMap, indexPath);
		} catch (IOException e) {
			System.out.println("Failed to read the directory: " + inputDir);
		}
	}
}