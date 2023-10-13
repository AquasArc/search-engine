package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using spaces.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class JsonWriter {
	/**
	 * Indents the writer by the specified number of times. Does nothing if the
	 * indentation level is 0 or less.
	 *
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(Writer writer, int indent) throws IOException {
		while (indent-- > 0) {
			writer.write("  ");
		}
	}

	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write(element);
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeQuote(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Writer writer, int indent) throws IOException {
		java.util.Iterator<? extends Number> iterator = elements.iterator();

		writer.write("[\n");
		
		// TODO Need to take an if/while approach here instead (same with the other methods)

		while (iterator.hasNext()) {
			writeIndent(iterator.next().toString(), writer, indent + 1);

			if (iterator.hasNext()) {
				writer.write(",");
			}

			writer.write("\n");
		}

		writeIndent("]", writer, indent);
	}


	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static String writeArray(Collection<? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Writer writer, int indent) throws IOException {
		writer.write("{\n");

		// TODO All of these methods need to use a more efficent approach
		
		int i = 0;
		for (Map.Entry<String, ? extends Number> entry : elements.entrySet()) {
			writeIndent(writer, indent + 1);
			writer.write("\"" + entry.getKey() + "\": " + entry.getValue().toString());

			// Adding a comma only if its not the last element
			if (i < elements.size() - 1)  {
				writer.write(",\n");
			} else {
				writer.write("\n");
			}
			i++;
		}

		writeIndent(writer, indent);
		writer.write("}\n");
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObject(Map, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObject(Map, Writer, int)
	 */
	public static String writeObject(Map<String, ? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of number objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeArray(Collection)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Writer writer, int indent) throws IOException {
		writer.write("{\n");
		// Changed to a boolean entry check
		boolean firstEntry = true;
		// Changed to use the Map and its entries
		for (Map.Entry<String, ? extends Collection<? extends Number>> entry : elements.entrySet()) {
			if (!firstEntry) {
				writer.write(",\n");
			}
			// Changed to utilize the other methods in the class
			writeQuote(entry.getKey() + ":", writer, indent + 1);
			writeArray(entry.getValue(), writer, indent + 1);
			firstEntry = false;
		}
		writer.write("\n");
		writeIndent(writer, indent);
		writer.write("}\n");
	}


	/**
	 * Writes the elements as a pretty JSON object with nested arrays to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObjectArrays(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static String writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObjectArrays(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects. The generic
	 * notation used allows this method to be used for any type of collection with
	 * any type of nested map of String keys to number objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeObject(Map)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Writer writer, int indent) throws IOException {
		// Check if the collection is empty and return an empty JSON array
		if (elements.isEmpty()) {
			writer.write("[\n]");
			return;
		}
		
		// TODO reuse writeObject for the inner Map data structure

		// Write the opening bracket of the JSON array
		writer.write("[\n");

		boolean isFirstMap = true; // Flag to check if we're at the first map in the collection
		for (var currentMap : elements) {
			// Add a comma if this isn't the first map
			if (!isFirstMap) {
				writer.write(",\n");
			}

			writeIndent(writer, indent + 1); // Indent for the map opening bracket
			writer.write("{\n");

			boolean isFirstEntry = true; // Flag to check if we're at the first entry in the map
			for (var entry : currentMap.entrySet()) {
				// Add a comma if this isn't the first entry
				if (!isFirstEntry) {
					writer.write(",\n");
				}

				// Write the key-value pair
				writeMapEntry(writer, entry, indent + 2);
				isFirstEntry = false;
			}

			// Close the current map
			writer.write("\n");
			writeIndent(writer, indent + 1);
			writer.write("}");
			isFirstMap = false; // Set flag to false as we have processed at least one map
		}

		// Close the JSON array
		writer.write("\n");
		writeIndent(writer, indent);
		writer.write("]\n");
	}

	/**
	 * Writes a single map entry as a key value pair in JSON format.
	 *
	 * @param writer The writer object used for writing the key value pair
	 * @param entry The map entry to be written as a key value pair
	 * @param indent The number of spaces for indentation
	 * @throws IOException If writing fails
	 */
	private static void writeMapEntry(Writer writer, Map.Entry<String, ? extends Number> entry, int indent) throws IOException {
		writeIndent(writer, indent); // Add appropriate indentation
		writer.write(String.format("\"%s\": %s", entry.getKey(), entry.getValue())); // Write the key-value pair
	}


	/**
	 * Writes the elements as a pretty JSON array with nested objects to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArrayObjects(Collection)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArrayObjects(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array with nested objects.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArrayObjects(Collection)
	 */
	public static String writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArrayObjects(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the inverted index data structure to the specified output path in JSON format.
	 *
	 * @param index      The nested map representing the inverted index.
	 * @param outputPath The path where the output should be written.
	 * @throws IOException If an error occurs while writing to the file.
	 */
	public static void writeIndexToFile(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, Path outputPath) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) { // TODO File.newBufferedReader
			writeNestedMap(index, writer, 0);
		} catch (IOException e) {
			throw new IOException("Failed to write index to: " + outputPath, e);
		}
	}

	// TODO Figure out the generic type for the index
	/**
	 * Helper method to format and write the inverted index data structure as a pretty-printed JSON.
	 *
	 * @param index  The nested map representing the inverted index.
	 * @param writer The writer object to output the formatted data.
	 * @param indent The indentation level to use for pretty-printing.
	 * @throws IOException If an error occurs during writing.
	 */
	public static void writeNestedMap(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, Writer writer, int indent) throws IOException {
		writer.write("{\n");

		int outerCount = 0;
		for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> wordEntry : index.entrySet()) {
			writeQuote(wordEntry.getKey(), writer, indent + 1);
			writer.write(": {");
			writer.write("\n");
			
			// TODO Should be able to reuse writeObjectArrays on the inner data structure

			int innerCount = 0;
			for (Map.Entry<String, TreeSet<Integer>> fileEntry : wordEntry.getValue().entrySet()) {
				Path correctedPath = Paths.get(fileEntry.getKey());
				writeQuote(correctedPath.toString(), writer, indent + 2);
				writer.write(": ");
				writeArray(fileEntry.getValue(), writer, indent + 2);

				// Check for trailing comma
				if (innerCount < wordEntry.getValue().size() - 1) {
					writer.write(",");
				}
				writer.write("\n");
				innerCount++;
			}

			writeIndent("}", writer, indent + 1);

			// Check for trailing comma
			if (outerCount < index.size() - 1) {
				writer.write(",");
			}
			writer.write("\n");
			outerCount++;
		}

		writeIndent("}", writer, indent);
	}


	/**
	 * Writes the word counts to the specified output path in JSON format.
	 *
	 *
	 *
	 * @param wordCounts The map containing words and their counts.
	 * @param outputPath The path where the output should be written.
	 * @throws IOException If an error occurs while writing to the file.
	 */
	public static void writeWordCountsToFile(Map<String, Long> wordCounts, Path outputPath) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
			JsonWriter.writeObject(wordCounts, writer, 0);
		} catch (IOException e) {
			throw new IOException("Failed to write word counts to: " + outputPath, e);
		}
	}
}
