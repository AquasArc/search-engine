package edu.usfca.cs272;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class FileProcessor {

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
}