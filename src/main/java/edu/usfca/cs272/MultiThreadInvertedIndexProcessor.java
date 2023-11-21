package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class MultiThreadInvertedIndexProcessor {
	/**
	 * Processes a file, stems its words, and updates the inverted index data structure.
	 * 
	 * @param filePath The path to the file to process.
	 * @param index The InvertedIndex instance used for updating word occurrences.
	 * @throws IOException If an error occurs while reading the file.
	 */
	public static void processFile(Path filePath, InvertedIndex index) throws IOException { //utilize task... it is the task, doesnt need to know the workqueue exists
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
	public static void processDirectory(Path dirPath, ThreadSafeInvertedIndex index, WorkQueue workQueue) throws IOException { // adding each file as basically a task to execute
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
			// Create the work queue
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					processDirectory(entry, index, workQueue);
				} else if (Files.isRegularFile(entry) && isTextFile(entry)) {
					workQueue.execute(new Task(entry, index)); //create task here
				}
			}
		}
		workQueue.finish();
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
	public static void processText(Path inputPath, ThreadSafeInvertedIndex index, WorkQueue workQueue) throws IOException {
		if (Files.isRegularFile(inputPath)) {
			processFile(inputPath, index);
		} else if (Files.isDirectory(inputPath)) {
			processDirectory(inputPath, index, workQueue);
		}
	}
	
	public static class Task implements Runnable {
	    private final Path path;
	    private final ThreadSafeInvertedIndex index;

	    public Task(Path path, ThreadSafeInvertedIndex index) {
	        this.path = path;
	        this.index = index;
	    }

	    @Override
	    public void run() {
	        try {
	            processFile(path, index);
	        } catch (IOException e) {
	            throw new UncheckedIOException(e);
	        }
	    }
	}
}
