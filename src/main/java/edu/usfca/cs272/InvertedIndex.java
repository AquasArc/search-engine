package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * Class responsible for dealing with inverted index
 *
 * @author Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class InvertedIndex {

	// The core data structure of the inverted index
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> indexMap;

	// Constructor initializes the data structure
	public InvertedIndex() {
		this.indexMap = new TreeMap<>();
	}
	// TODO Add the word count data structure in here too


	/**
	 * Takes in a inputPath to check if it is null, if its not
	 * then it checks the inputPath. Checks if its a file or directory.
	 * Depending on what it is, direct the inputs to the corresponding helper method
	 * 
	 * @param inputPath: The path of the file or directory to process.
	 * @param indexPath: The path where the output should be written.
	 * @throws IOException If an error occurs while reading the file
	 */
	public void fileOrDirIndex(Path inputPath,Path indexPath) throws IOException {
		if (inputPath != null) {
			if (Files.isRegularFile(inputPath)) {
				try {
					processFileInvertedIndex(inputPath);
				} catch (IOException e) {
					System.out.println("Error in attempting to use information from indexMap to updateFile");
				}
				JsonWriter.writeNestedMapToFile(indexMap, indexPath);
			} else if (Files.isDirectory(inputPath)) {
				processDirectoryInvertedIndex(inputPath, indexPath);
			} else {
				System.out.println("Invalid input path");
			}
		}
	}

	/**
	 * Updates a nested map with a word, its file path, and its position in the file.
	 * 
	 * @param word         The word to be added to the map.
	 * @param filePath     The path of the file where the word is located.
	 * @param wordPosition The position of the word in the file.
	 */
	private void updateNestedMap(String word, String filePath, int wordPosition) {
		indexMap.putIfAbsent(word, new TreeMap<>());
		indexMap.get(word).putIfAbsent(filePath, new TreeSet<>());
		indexMap.get(word).get(filePath).add(wordPosition);
	}


	/**
	 * Reads the given text file, stems the words, and then adds them to 
	 * the nested Map 'indexMap' along with their positions
	 * 
	 *
	 * @param filePath The Path of the text file to read
	 * @throws IOException If an error occurs while reading the file
	 */
	public void processFileInvertedIndex(Path filePath) throws IOException {
		ArrayList<String> stemmedWords = FileStemmer.listStems(filePath);

		int wordPosition = 0;
		for (String word : stemmedWords) {
			wordPosition++;
			updateNestedMap(word, filePath.toString(), wordPosition);
		}
	}

	/**
	 * Writes the data that is in a nested map into a given file in a pretty json format
	 * It does this by, taking in a directory as an argument
	 * Then looping through the directory going to each file,
	 * and if necessary all of the children directories and their files,
	 * and applying the file helper method that is used to write to a file
	 * 
	 * @param inputDir  The path of the input directory.
	 * @param indexPath The path where the output should be written.
	 */
	public void processDirectoryInvertedIndex(Path inputDir, Path indexPath) {
		try {
			Files.walk(inputDir)
			.forEach(path -> {
				if (Files.isRegularFile(path)) {
					String fileName = path.toString().toLowerCase();
					if (fileName.endsWith(".txt") || fileName.endsWith(".text")) {
						try {
							processFileInvertedIndex(path);
						} catch (IOException e) {
							System.out.println("FileProcessor 150: Error updating inverted index.");
						}
					}
				}
			});
			JsonWriter.writeNestedMapToFile(indexMap, indexPath);
		} catch (IOException e) {
			System.out.println("Failed to read the directory: " + inputDir);
		}
	}
}