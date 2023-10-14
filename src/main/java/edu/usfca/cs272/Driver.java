package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



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
	public static void main(String[] args){
		ArgumentParser parser = new ArgumentParser(args);

		InvertedIndex index = new InvertedIndex();
		QueryProcessor processor = new QueryProcessor(index);
		Map<String, List<FileResult>> results = new HashMap<>();


		if (parser.hasFlag("-text")) {
			Path inputPath = parser.getPath("-text");
			try {
				FileProcessor.processText(inputPath, index);
			} catch (IOException | NullPointerException e) {
				System.out.println("Error Detected:");
				System.out.println("Error processing text: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-counts")) {
			try {
				FileProcessor.processCounts(parser.getPath("-counts", Path.of("counts.json")), index);
			} catch (IOException e) {
				System.out.println("Error processing counts: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-index")) {
			try {
				FileProcessor.processIndex(parser.getPath("-index", Path.of("index.json")), index);
			} catch (IOException e) {
				System.out.println("Error processing index: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-query")) {
			Path queryPath = parser.getPath("-query");
			if (queryPath == null) {
				System.out.println("Error: Missing value for -query flag");
			} else {
				try {
					results = processor.processQuery(queryPath);
				} catch (IOException e) {
					System.out.println("Error processing query: " + e.getMessage());
				}
			}
		}

		if (parser.hasFlag("-results")) {
			try {
				processor.writeResults(results, parser.getPath("-results", Path.of("results.json")));
			} catch (IOException e) {
				System.out.println("Error processing results: " + e.getMessage());
			}
		}
	}
}