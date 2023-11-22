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
	 *
	 * @param args Command-line arguments
	 */
	public static void main(String[] args){
		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex index = null;
		//ThreadSafeInvertedIndex index = new ThreadSafeInvertedIndex();
		
		/** Creating the workqueue and setting it to null*/
		WorkQueue workQueue = null;
		
		/** Boolean flag to determine exact/partial search*/
		boolean isPartial = parser.hasFlag("-partial");
		
		/** ThreadSafe QueryProcessor object for search*/
		ThreadSafeQueryProcessor processorTS = null;
		
		/** QueryProcessor object for search*/
		QueryProcessor processor = null;
		
		MultiThreadQueryProcessor multiProcessor = null;

		/** Logic to determine multi-threading or not*/
		if (parser.hasFlag("-threads")) {
			index = new ThreadSafeInvertedIndex();
			workQueue = new WorkQueue(parser.getInteger("-threads", 5));
			processorTS = new ThreadSafeQueryProcessor(index, isPartial);
			multiProcessor = new MultiThreadQueryProcessor((ThreadSafeInvertedIndex) index, isPartial);
		} else {
			index = new InvertedIndex();
			processor = new QueryProcessor(index, isPartial);
		}
		
		
		if (parser.hasFlag("-text")) {
			try {
				if (parser.hasFlag("-threads")) {
					MultiThreadInvertedIndexProcessor.processText(parser.getPath("-text"), (ThreadSafeInvertedIndex) index, workQueue);
				} else {
					InvertedIndexProcessor.processText(parser.getPath("-text"), index);
				}
			} catch (IOException | NullPointerException e) {
				System.out.println("Error Detected:");
				System.out.println("Error processing text: " + e.getMessage());
			}
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

		if (parser.hasFlag("-query")) {
			try {
				if (parser.hasFlag("-threads")) { // Im not sure what to do here... TODO
					multiProcessor.processQuery(parser.getPath("-query"), workQueue);
				} else {
					processor.processQuery(parser.getPath("-query"));
				}
			} catch (IOException | NullPointerException e) {
				System.out.println("Error processing query: " + e.getMessage());
			}
		}

		if (parser.hasFlag("-results")) {
			try {
				if (parser.hasFlag("-threads")) {
					multiProcessor.writeResults(parser.getPath("-results", Path.of("results.json")));
				} else {
					processor.writeResults(parser.getPath("-results", Path.of("results.json")));
				}
			} catch (IOException e) {
				System.out.println("Error processing results: " + e.getMessage());
			}
		}

		if (workQueue != null) {
			workQueue.join();
		}
	}
}