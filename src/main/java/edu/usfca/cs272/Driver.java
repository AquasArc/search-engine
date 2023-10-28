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
				index.writeCountsMap(parser.getPath("-counts", Path.of("counts.json")));
			} catch (IOException e) {
				System.out.println("Error processing counts: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-index")) {
			try {
				// TODO This is still breaking encapsulation...
				JsonWriter.writeIndexToFile(index.viewIndexMap(), parser.getPath("-index", Path.of("index.json")));
			} catch (IOException e) {
				System.out.println("Error processing index: " + e.getMessage());
			}
		}
	}
}