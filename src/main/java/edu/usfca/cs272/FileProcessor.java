package edu.usfca.cs272;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
	
	public static void processDirectoryIndex(Path inputDir, Path indexPath, Map<String, Map<String, List<Integer>>> indexMap) {
		try (Stream<Path> paths = Files.walk(inputDir, FileVisitOption.FOLLOW_LINKS)) {
			List<Path> fileList = paths
					.filter(Files::isRegularFile)
					.filter(path -> path.toString().toLowerCase().endsWith(".txt") || path.toString().toLowerCase().endsWith(".text"))
					.collect(Collectors.toList());

			fileList.forEach(file -> {
				Driver.updateInvertedIndex(file, indexMap);
			});

			JsonWriter.writeNestedMapToFile(indexMap, indexPath);

		} catch (IOException e) {
			System.out.println("Failed to read the directory: " + inputDir);
		}
	}
}