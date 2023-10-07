package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



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
	 * @throws IOException If an error occurs while reading the file
	 */
	public static void main(String[] args) throws IOException { // TODO This is the one method that should not throw exceptions (most other methods will throw exceptions)
		/* TODO 
		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex index = new InvertedIndex(); 
		
		if (parser.hasFlag("-text")) {
			Path inputPath = parser.getPath("-text");
			
			try {
				call a method in your file processor class
			}
			catch (...) {
				System.out.println("Unable to build the inverted index from path: " + inputPath);
			}
		}
		
		if (parser.hasFlag("-counts")) {
			Path countPath = parser.getPath("-counts", Path.of("counts.json"));
			
			try {
				1 method call
			}
			catch (...) {
				System.out.println("Unable to write the word counts to path: " + countPath);
			}
		}

		if (parser.hasFlag("-index")) {
			...
		}
		*/

		
		ArgumentParser parser = new ArgumentParser(args);

		boolean countsFlagProvided = parser.hasFlag("-counts");
		boolean indexFlagProvided = parser.hasFlag("-index");

		InvertedIndex index = new InvertedIndex();
		
		// If -text flag is provided, parse & get the path
		if (parser.hasFlag("-text")) {
			Path inputPath = parser.getPath("-text");

			// Check if counts flag is provided
			if(countsFlagProvided) {
				Path outputPath = parser.getPath("-counts");

				// Check if the path wasn't provided, if not create a default...
				if (outputPath == null) {
					outputPath = Paths.get("counts.json"); // Default
				}

				FileProcessor.fileOrDirCount(inputPath, outputPath);
			}

			// Check if index flag is provided
			if (indexFlagProvided) {
				Path indexPath = parser.getPath("-index");

				// Check if the path wasn't provided, if not create a default...
				if(indexPath == null) {
					indexPath = Paths.get("index.json");
				}
				index.fileOrDirIndex(inputPath, indexPath);
			}
		} else {
			if (!parser.hasFlag("-text")) {
				if (countsFlagProvided) {
					Path outputPath = parser.getPath("-counts");

					if (outputPath == null) {
						outputPath = Paths.get("counts.json");
					}

					try {
						Files.writeString(outputPath, "{}");
					} catch (IOException e) {
						System.out.println("Failed to write empty JSON file");
					}
				}

				if(indexFlagProvided) {
					Path indexPath = parser.getPath("index.json");

					if (indexPath == null) {
						indexPath = Paths.get("index.json");
					}

					try {
						Files.writeString(indexPath, "{}");
					} catch (IOException e) {
						System.out.println("Failed to write empty JSON file");
					}
				}
			}
		}
	}
}