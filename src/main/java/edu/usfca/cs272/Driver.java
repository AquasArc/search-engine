package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;



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

		boolean isPartial = parser.hasFlag("-partial");

		if (parser.hasFlag("-text")) {
			Path inputPath = parser.getPath("-text");
			try {
				InvertedIndexProcessor.processText(inputPath, index);
			} catch (IOException | NullPointerException e) {
				System.out.println("Error Detected:");
				System.out.println("Error processing text: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-counts")) {
			try {
				index.writeCountsMap(parser.getPath("-counts", Path.of("counts.json")));
			} catch (IOException e) {
				System.out.println("Error processing counts: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-index")) {
			try {
				index.processIndex(parser.getPath("-index", Path.of("index.json")));
			} catch (IOException e) {
				System.out.println("Error processing index: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-query")) {
			try {
				processor.processQuery(parser.getPath("-query"), isPartial); 
			} catch (IOException e) {
				System.out.println("Error processing query: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-results")) {
			try {
				processor.writeResults(parser.getPath("-results", Path.of("results.json")));
			} catch (IOException e) {
				System.out.println("Error processing results: " + e.getMessage());
			}
		}
	}
}