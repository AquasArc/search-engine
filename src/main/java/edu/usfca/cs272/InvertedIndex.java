package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Class responsible for dealing with inverted index
 *
 * @author Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class InvertedIndex {


	/**
	 * Updates a nested map with a word, its file path, and its position in the file.
	 * 
	 * @param word         The word to be added to the map.
	 * @param filePath     The path of the file where the word is located.
	 * @param wordPosition The position of the word in the file.
	 * @param indexMap     The nested map to be updated.
	 */
	private static void updateNestedMap(String word, String filePath, int wordPosition, Map<String, Map<String, List<Integer>>> indexMap) {
		indexMap.putIfAbsent(word, new TreeMap<>());
		indexMap.get(word).putIfAbsent(filePath, new ArrayList<>());
		indexMap.get(word).get(filePath).add(wordPosition);
	}


	/**
	 * Reads the given text file, stems the words, and then adds them to 
	 * the nested Map 'indexMap' along with their positions
	 * 
	 *
	 * @param filePath The Path of the text file to read
	 * @param indexMap The nested Map to update with stemmed words and their positions
	 * @throws IOException If an error occurs while reading the file
	 */
	public static void updateInvertedIndex(Path filePath, Map<String, Map<String, List<Integer>>> indexMap) throws IOException {
		ArrayList<String> stemmedWords;
		System.out.println("This is the filepath: " + filePath.toString());

		// Moved try/catch logic out of this method
		stemmedWords = FileStemmer.listStems(filePath);
		System.out.println("Stemmed words: " + stemmedWords);  // Debugging line

		int wordPosition = 0;
		for (String word : stemmedWords) {
			wordPosition++;
			updateNestedMap(word, filePath.toString(), wordPosition, indexMap);
		}

		// Need to:  Update the word count here based on wordPosition
	}
}