package edu.usfca.cs272;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Provides utility functions for processing files, directories, and generating data outputs.
 * Utilizes the InvertedIndex to handle and manage the indexing of words within files.
 * 
 * 
 * 
 * @author Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class FileProcessor {

	
	
	/**
	 * Processes a file, stems its words, and updates the inverted index data structure.
	 * 
	 * @param filePath The path to the file to process.
	 * @throws IOException If an error occurs while reading the file.
	 */
	public static void processFile(Path filePath, InvertedIndex index) throws IOException {
		ArrayList<String> stemmedWords = FileStemmer.listStems(filePath);
		int position = 0;

		for (String word : stemmedWords) {
			position++;
			index.add(word, filePath.toString(), position);
		}
	}

	/**
	 * Processes a directory by iterating through its files and updating the inverted index.
	 * Only processes files with .txt or .text extensions.
	 * 
	 * @param dirPath The path to the directory to process.
	 * @throws IOException If an error occurs while reading files within the directory.
	 */
	public static void processDirectory(Path dirPath, InvertedIndex index) throws IOException { //Changed to use directory streams
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					processDirectory(entry, index);
				} else if (Files.isRegularFile(entry)) {
					String fileName = entry.toString().toLowerCase();
					if (fileName.endsWith(".txt") || fileName.endsWith(".text")) {
						try {
							processFile(entry, index);
						} catch (IOException e) {
							System.out.println("Error Processing file " + entry);
						}
					}
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
	 * @throws IOException If an error occurs during file or directory processing.
	 */
	public static void processText(Path inputPath, InvertedIndex index) throws IOException {
		if (Files.isRegularFile(inputPath)) {
			processFile(inputPath, index);
		} else if (Files.isDirectory(inputPath)) {
			processDirectory(inputPath, index);
		} else {
			throw new IOException("Invalid input path: " + inputPath);
		}
	}
}
