package edu.usfca.cs272;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;



/**The multi threaded variant of the inverted index processor
 * It makes the processFile method as a task as a whole
 * 
 */
public class MultiThreadInvertedIndexProcessor {

	/**
	 * Processes a directory by iterating through its files and updating the inverted index.
	 * Only processes files with .txt or .text extensions.
	 * 
	 * @param dirPath The path to the directory to process
	 * @param index The InvertedIndex instance used for updating word occurrences.
	 * @param workQueue the Workqueue that will be used to execute said tasks
	 * @throws IOException If an error occurs while reading files within the directory.
	 */
	public static void processDirectory(Path dirPath, ThreadSafeInvertedIndex index, WorkQueue workQueue) throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
			// Create the work queue
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					processDirectory(entry, index, workQueue);
				} else if (Files.isRegularFile(entry) && InvertedIndexProcessor.isTextFile(entry)) {
					workQueue.execute(new Task(entry, index));
				}
			}
		}
	}

	/**
	 * Processes a given input path, checking whether it's a regular file or a directory,
	 * and delegates the task to the appropriate method in the InvertedIndex.
	 * 
	 * @param inputPath The path to either a single file or a directory to process.
	 * @param index The InvertedIndex instance to use for processing.
	 * @param workQueue the Workqueue that will be used to execute said tasks
	 * @throws IOException If an error occurs during file or directory processing.
	 */
	public static void processText(Path inputPath, ThreadSafeInvertedIndex index, WorkQueue workQueue) throws IOException {
		if (Files.isRegularFile(inputPath)) {
			workQueue.execute(new Task(inputPath, index));
		} else if (Files.isDirectory(inputPath)) {
			processDirectory(inputPath, index, workQueue);
		}
		workQueue.finish();
	}


	/**
	 * Represents a task for processing a file in a separate thread.
	 * This task is responsible for reading a file, processing its contents,
	 * and updating the given ThreadSafeInvertedIndex with the results.
	 */
	public static class Task implements Runnable {
		/**
		 * The path to the file that this task will process.
		 */
		private final Path path;

		/**
		 * The ThreadSafeInvertedIndex instance where the results of file processing
		 * will be stored.
		 */
		private final ThreadSafeInvertedIndex index;

		/**
		 * Creates a new task for processing the specified file.
		 *
		 * @param path  The file path to process.
		 * @param index The ThreadSafeInvertedIndex instance to update with the results of processing the file.
		 * 
		 */
		public Task(Path path, ThreadSafeInvertedIndex index) {
			this.path = path;
			this.index = index;
		}

		/**
		 * The main method that runs when this task is executed by a thread.
		 * It calls the processFile method to handle the actual file processing.
		 * If an IOException occurs during file processing, it is caught and rethrown
		 * as an UncheckedIOException.
		 */
		@Override
		public void run() {
			try {
				// Create a local inverted index
				InvertedIndex localIndex = new InvertedIndex();

				// Process the file and update the local index
				InvertedIndexProcessor.processFile(path, localIndex);

				// Safely add the local index to the shared index
				index.addAll(localIndex);

			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
}
