package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;
import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader; // For try-with resources
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import java.util.regex.Pattern;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * Utility class for parsing, cleaning, and stemming text and text files into
 * collections of processed words.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * 
 * @version Fall 2023
 */
public class FileStemmer {
	/** Regular expression that matches any whitespace. **/
	public static final Pattern SPLIT_REGEX = Pattern.compile("(?U)\\p{Space}+");

	/** Regular expression that matches non-alphabetic characters. **/
	public static final Pattern CLEAN_REGEX = Pattern.compile("(?U)[^\\p{Alpha}\\p{Space}]+");

	/**
	 * Cleans the text by removing any non-alphabetic characters (e.g. non-letters
	 * like digits, punctuation, symbols, and diacritical marks like the umlaut) and
	 * converting the remaining characters to lowercase.
	 *
	 * @param text the text to clean
	 * @return cleaned text
	 */
	public static String clean(String text) {
		String cleaned = Normalizer.normalize(text, Normalizer.Form.NFD);
		cleaned = CLEAN_REGEX.matcher(cleaned).replaceAll("");
		return cleaned.toLowerCase();
	}

	/**
	 * Splits the supplied text by whitespaces.
	 *
	 * @param text the text to split
	 * @return an array of {@link String} objects
	 */
	public static String[] split(String text) {
		return text.isBlank() ? new String[0] : SPLIT_REGEX.split(text.strip());
	}

	/**
	 * Parses the text into an array of clean words.
	 *
	 * @param text the text to clean and split
	 * @return an array of {@link String} objects
	 *
	 * @see #clean(String)
	 * @see #parse(String)
	 */
	public static String[] parse(String text) {
		return split(clean(text));
	}

	/**
	 * Parses the line into cleaned and stemmed words and adds them to the provided
	 * collection.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @param stems the collection to add stems
	 *
	 * @see #parse(String)
	 * @see Stemmer#stem(CharSequence)
	 * @see Collection#add(Object)
	 */
	public static void addStems(String line, Stemmer stemmer, Collection<String>stems) {
		// Parse the input to individual words

		String[] words = parse(line);

		// Loop through the series of words and apply the stemmer algorithm
		for (String word : words) {
			String stemWord = stemmer.stem(word).toString();

			// Add the stem word into the collection
			stems.add(stemWord);
		}
	}

	/**
	 * Parses the line into a list of cleaned and stemmed words.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a list of cleaned and stemmed words in parsed order
	 *
	 * @see #parse(String)
	 * @see Stemmer#stem(CharSequence)
	 * @see #addStems(String, Stemmer, Collection)
	 */
	public static ArrayList<String> listStems(String line, Stemmer stemmer) {
		// Creating an Array List to hold the list of stemmed words
		ArrayList<String> stemmedWords = new ArrayList<>();

		// Using the method addStems to add stemmedWords into ArrayList
		addStems(line, stemmer, stemmedWords);

		// Return the array list of stemmed words
		return stemmedWords;
	}

	/**
	 * Parses the line into a list of cleaned and stemmed words using the default
	 * stemmer for English.
	 *
	 * @param line the line of words to parse and stem
	 * @return a list of cleaned and stemmed words in parsed order
	 *
	 * @see SnowballStemmer#SnowballStemmer(ALGORITHM)
	 * @see ALGORITHM#ENGLISH
	 * @see #listStems(String, Stemmer)
	 */
	public static ArrayList<String> listStems(String line) {
		return listStems(line, new SnowballStemmer(ENGLISH));

	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words
	 * using the default stemmer for English.
	 *
	 * @param input the input file to parse and stem
	 * @return a list of stems from file in parsed order
	 * @throws IOException if unable to read or parse file
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see StandardCharsets#UTF_8
	 * @see #listStems(String, Stemmer)
	 */
	public static ArrayList<String> listStems(Path input) throws IOException {
		ArrayList<String> stemWords = new ArrayList<>();
		Stemmer stemmer = new SnowballStemmer(ENGLISH);

		try (BufferedReader bufferreader = Files.newBufferedReader(input)) {
			String line;
			while ((line = bufferreader.readLine()) != null) {
				addStems(line, stemmer, stemWords);
			}
		}
		return stemWords;
	}

	/**
	 * Parses the line into a set of unique, sorted, cleaned, and stemmed words.
	 *
	 * @param line the line of words to parse and stem
	 * @param stemmer the stemmer to use
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see #parse(String)
	 * @see Stemmer#stem(CharSequence)
	 * @see #addStems(String, Stemmer, Collection)
	 */
	public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) {
		TreeSet<String> uniqueStems = new TreeSet<>();
		addStems(line, stemmer, uniqueStems);
		return uniqueStems;
	}

	/**
	 * Parses the line into a set of unique, sorted, cleaned, and stemmed words
	 * using the default stemmer for English.
	 *
	 * @param line the line of words to parse and stem
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see SnowballStemmer#SnowballStemmer(ALGORITHM)
	 * @see ALGORITHM#ENGLISH
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static TreeSet<String> uniqueStems(String line) {
		Stemmer stemmer = new SnowballStemmer(ENGLISH);

		return uniqueStems(line, stemmer);
	}

	/**
	 * Reads a file line by line, parses each line into a set of unique, sorted,
	 * cleaned, and stemmed words using the default stemmer for English.
	 *
	 * @param input the input file to parse and stem
	 * @return a sorted set of unique cleaned and stemmed words from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see StandardCharsets#UTF_8
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static TreeSet<String> uniqueStems(Path input) throws IOException {
		TreeSet<String> uniqueStems = new TreeSet<>();
		Stemmer stemmer = new SnowballStemmer(ENGLISH);

		try(BufferedReader reader = Files.newBufferedReader(input, UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				addStems(line, stemmer, uniqueStems);
			}
		}
		return uniqueStems;
	}

	/**
	 * Reads a file line by line, parses each line into unique, sorted, cleaned, and
	 * stemmed words using the default stemmer for English, and adds the set of
	 * unique sorted stems to a list per line in the file.
	 *
	 * @param input the input file to parse and stem
	 * @return a list where each item is the sets of unique sorted stems parsed from
	 *   a single line of the input file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see StandardCharsets#UTF_8
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static ArrayList<TreeSet<String>> listUniqueStems(Path input) throws IOException {
		ArrayList<TreeSet<String>> uniqueStemsList = new ArrayList<>();
		Stemmer stemmer = new SnowballStemmer(ENGLISH);

		try(BufferedReader reader = Files.newBufferedReader(input, UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				uniqueStemsList.add(uniqueStems(line, stemmer));
			}
		}
		return uniqueStemsList;
	}
}