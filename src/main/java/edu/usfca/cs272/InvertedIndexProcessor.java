package edu.usfca.cs272;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.BufferedReader;
import java.io.IOException;

import opennlp.tools.stemmer.snowball.SnowballStemmer;
import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

/**
 * Provides utility functions for processing files, directories, and generating data outputs.
 * Utilizes the InvertedIndex to handle and manage the indexing of words within files.
 * 
 * @author Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class InvertedIndexProcessor {

	/** To enable single threading processing... Will only be multithreaded if thread flag is reached... */
	public static int THREAD_COUNT = 1;


	/**
	 * Processes a file, stems its words, and updates the inverted index data structure.
	 * 
	 * @param filePath The path to the file to process.
	 * @param index The InvertedIndex instance used for updating word occurrences.
	 * @throws IOException If an error occurs while reading the file.
	 */
	public static void processFile(Path filePath, ThreadSafeInvertedIndex index) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(filePath)) {
			String line;
			int position = 0;
			String filePathStr = filePath.toString();
			SnowballStemmer stemmer = new SnowballStemmer(ENGLISH);
			while ((line = reader.readLine()) != null) {
				String[] words = FileStemmer.parse(line);
				for (String word : words) {
					String stemmedWord = stemmer.stem(word).toString();
					position++;
					index.add(stemmedWord, filePathStr, position);
				}
			}
		}
	}

	/**
	 * Processes a directory by iterating through its files and updating the inverted index.
	 * Only processes files with .txt or .text extensions.
	 * 
	 * @param dirPath The path to the directory to process
	 * @param index The InvertedIndex instance used for updating word occurrences.
	 * @throws IOException If an error occurs while reading files within the directory.
	 */
	public static void processDirectory(Path dirPath, ThreadSafeInvertedIndex index) throws IOException { 
		WorkQueue workQueue = new WorkQueue(THREAD_COUNT);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
			// Create the work queue
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					processDirectory(entry, index);
				} else if (Files.isRegularFile(entry) && isTextFile(entry)) {
					workQueue.execute(() -> {
						// Reminder to add a logger here...
						synchronized(index) {
							try {
								processFile(entry, index);
							} catch (IOException e) {
								System.out.println("Error Processing File while sync");
							}
						}
					});
				}
			}
			workQueue.join();
		} finally {
			workQueue.shutdown();
		}
	}
	/**Check to see if the file ends with a .txt or .text
	 * 
	 * @param filePath is the argument given
	 * @return boolean return in regards to .txt or .text
	 */
	public static boolean isTextFile(Path filePath) {
		String fileName = filePath.toString().toLowerCase();
		return fileName.endsWith(".txt") || fileName.endsWith(".text");
	}


	/**
	 * Processes a given input path, checking whether it's a regular file or a directory,
	 * and delegates the task to the appropriate method in the InvertedIndex.
	 * 
	 * @param inputPath The path to either a single file or a directory to process.
	 * @param index The InvertedIndex instance to use for processing.
	 * @throws IOException If an error occurs during file or directory processing.
	 */
	public static void processText(Path inputPath, ThreadSafeInvertedIndex index) throws IOException {
		if (Files.isRegularFile(inputPath)) {
			processFile(inputPath, index);
		} else if (Files.isDirectory(inputPath)) {
			processDirectory(inputPath, index);
		}
	}

	// If the [num] argument is not provided, not a number, or less than 1, use 5 as the default number of worker threads.
	/** Processes the threadpath and determines how many [nums] was requested. Updates the global THREAD_COUNT depending
	 *  on result...
	 *
	 * @param threadPath The path to the thread [num].
	 * @throws IOException if an error has occured
	 */
	public static void updateThreadCount(Object threadPath) throws IOException {
		THREAD_COUNT = 5;
		if (threadPath != null && ((Integer) threadPath).intValue() > 0) {
			THREAD_COUNT = ((Integer) threadPath).intValue();
		}
	}
}
