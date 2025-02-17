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
		InvertedIndex index = null;
		ThreadSafeInvertedIndex safe = null;
		WorkQueue workQueue = null;

		/** Boolean flag to determine exact/partial search*/
		boolean isPartial = parser.hasFlag("-partial");

		/** QueryProcessor object for search*/
		QueryInterface processor = null;

		/** Logic to determine multi-threading or not*/
		if (parser.hasFlag("-threads")) {
			index = new ThreadSafeInvertedIndex();
			
			safe = new ThreadSafeInvertedIndex();
			index = safe;
			
			workQueue = new WorkQueue(parser.getPositiveInteger("-threads", 5));
			processor = new MultiThreadQueryProcessor(safe, isPartial, workQueue);
		} else {
			index = new InvertedIndex();
			processor = new QueryProcessor(index, isPartial);
		}

		if (parser.hasFlag("-text")) {
			try {
				if (safe != null && workQueue != null) {
					MultiThreadInvertedIndexProcessor.processText(parser.getPath("-text"), safe, workQueue);
				} else {
					InvertedIndexProcessor.processText(parser.getPath("-text"), index);
				}
			} catch (IOException | NullPointerException e) {
				System.out.println("Error Detected:");
				System.out.println("Error processing text: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-query")) {
			try {
				processor.processQuery(parser.getPath("-query"));
			} catch (IOException | NullPointerException e) {
				System.out.println("Error processing query: " + e.getMessage());
			}
		}

		if (workQueue != null) {
			workQueue.shutdown();
		}

		if (parser.hasFlag("-counts")) {
			try {
				index.writeCounts(parser.getPath("-counts", Path.of("counts.json")));
			} catch (IOException e) {
				System.out.println("Error processing counts: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-index")) {
			try {
				index.writeIndex(parser.getPath("-index", Path.of("index.json")));
			} catch (IOException e) {
				System.out.println("Error processing index: " + e.getMessage());
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