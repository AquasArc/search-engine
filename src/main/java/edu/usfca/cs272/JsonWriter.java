package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;



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
		writer.write("[\n");
		var iterator = elements.iterator();

		if (iterator.hasNext()) {
			writeIndent(iterator.next().toString(), writer, indent + 1);

			while(iterator.hasNext()) {
				writer.write(",\n");
				writeIndent(iterator.next().toString(),writer, indent + 1);
			}
			writer.write("\n");
		}

		writeIndent(writer, indent);
		writer.write("]");
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
		var iterator = elements.entrySet().iterator();

		if (iterator.hasNext()) {
			var entry = iterator.next();
			writeIndent(writer, indent + 1);
			writeQuote(entry.getKey(), writer, 0);
			writer.write(": ");
			writer.write(entry.getValue().toString());

			while (iterator.hasNext()) {
				writer.write(",\n");
				entry = iterator.next();
				writeIndent(writer, indent + 1);
				writeQuote(entry.getKey(), writer, 0);
				writer.write(": ");
				writer.write(entry.getValue().toString());
			}
			writer.write("\n");
		}

		writeIndent(writer, indent);
		writer.write("}");
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

		var iterator = elements.entrySet().iterator();
		if (iterator.hasNext()) {
			// handle first entry
			var entry = iterator.next();

			writeIndent(writer, indent + 1);
			writeQuote(entry.getKey(), writer, 0);
			writer.write(": ");
			writeArray(entry.getValue(), writer, indent + 1);

			// handle remaining entries
			while (iterator.hasNext()) {
				writer.write(",\n");
				entry = iterator.next();

				writeIndent(writer, indent + 1);
				writeQuote(entry.getKey(), writer, 0);
				writer.write(": ");
				writeArray(entry.getValue(), writer, indent + 1);
			}

			writer.write("\n");
		}

		writeIndent(writer, indent);
		writer.write("}");
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
	 *
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeObject(Map)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Writer writer, int indent) throws IOException {
		writer.write("[\n");
		var iterator = elements.iterator();

		if (iterator.hasNext()) {
			var currentMap = iterator.next();

			writeIndent(writer, indent + 1);
			writeObject(currentMap, writer, indent + 1);

			while (iterator.hasNext()) {
				writer.write(",\n");
				currentMap = iterator.next();

				writeIndent(writer, indent + 1);
				writeObject(currentMap, writer, indent + 1);
			}
			writer.write("\n");
		}

		writeIndent(writer, indent);
		writer.write("]");
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


	/**Writes the index as a pretty json format
	 * 
	 * @param index the index data structure that contains the data that will be written
	 * @param path The output that will be writing to
	 * @throws IOException throws an error if problems occur
	 */
	public static void writeIndexToFile(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> index, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeIndexToFile(index, writer, 0);
		}
	}

	/**Writes the index a a pretty json format, takes in just the data structure
	 * 
	 * @param elements is the data structure being passed
	 * @return writer.toString(); after calling writeIndexTofile
	 */
	public static String writeIndexToFile(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeIndexToFile(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**This method is to write the inverted index data into files in a pretty json format
	 * 
	 * @param index is the data structure that is being written in jsonformat
	 * @param writer writer to write the data into a file
	 * @param indent indent count for spacing
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndexToFile(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> index, Writer writer, int indent) throws IOException {
		writer.write("{\n");

		var iterator = index.entrySet().iterator();
		if (iterator.hasNext()) {
			// Handle the first entry
			var wordEntry = iterator.next();
			writeQuote(wordEntry.getKey(), writer, indent + 1);
			writer.write(": ");
			writeObjectArrays(wordEntry.getValue(), writer, indent + 1);

			// Handle remaining entries
			while (iterator.hasNext()) {
				writer.write(",\n");
				wordEntry = iterator.next();

				writeQuote(wordEntry.getKey(), writer, indent + 1);
				writer.write(": ");
				writeObjectArrays(wordEntry.getValue(), writer, indent + 1);
			}

			writer.write("\n");
		}
		writer.write("}");
	}



	/**Writes the resultsMap data structure into a json format
	 * 
	 * @param results the data structure containing the data
	 * @param path is the output path that is being written to
	 * @throws IOException if any issues arises
	 */
	public static void writeResultsToFile(Map<String, ? extends Collection<? extends FileResult>> results, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeResultsToFile(results, writer, 0);
		}
	}


	/**Writes the resultsMap data structure into a json format
	 * 
	 * @param results the data structure containing the data
	 * @return writer.toString() or null depending on if errors occur
	 */
	public static String writeResultToFile(Map<String, ? extends Collection<? extends FileResult>> results) {
		try {
			StringWriter writer = new StringWriter();
			writeResultsToFile(results, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**The main method that is being used to write the resultsMap into a pretty json structure
	 * 
	 * @param results the data structure containing the data
	 * @param writer the writer to use 
	 * @param indent indenting the values
	 * @throws IOException if any errors occur
	 */
	public static void writeResultsToFile(Map<String, ? extends Collection<? extends FileResult>> results, Writer writer, int indent) throws IOException {
		writer.write("{\n");

		var iterator = results.entrySet().iterator();
		if (iterator.hasNext()) {
			Map.Entry<String, ? extends Collection<? extends FileResult>> entry = iterator.next();
			writeResultEntry(entry, writer, indent + 1);

			while (iterator.hasNext()) {
				writer.write(",\n");
				entry = iterator.next();
				writeResultEntry(entry, writer, indent + 1);
			}

			writer.write("\n");
		}
		writeIndent("}", writer, indent);
	}

	/**A helper method 
	 * 
	 * @param entry the data structure being passed in to be written in json format
	 * @param writer to write the values
	 * @param indent to write the indents
	 * @throws IOException if any errors occur
	 */
	private static void writeResultEntry(Map.Entry<String, ? extends Collection<? extends FileResult>> entry, Writer writer, int indent) throws IOException {
		writeQuote(entry.getKey(), writer, indent);
		writer.write(": [\n");

		var fileResultsIterator = entry.getValue().iterator();
		if (fileResultsIterator.hasNext()) {
			FileResult fileResult = fileResultsIterator.next();
			writeFileResult(fileResult, writer, indent + 1);

			while (fileResultsIterator.hasNext()) {
				writer.write(",\n");
				fileResult = fileResultsIterator.next();
				writeFileResult(fileResult, writer, indent + 1);
			}

			writer.write("\n");
		}
		writeIndent("]", writer, indent);
	}

	/**A helper method responsible for writing the file result meta data
	 * 
	 * @param fileResult the file result object 
	 * @param writer to write data
	 * @param indent for indenting the values
	 * @throws IOException if any errors occur
	 */
	private static void writeFileResult(FileResult fileResult, Writer writer, int indent) throws IOException {
		writeIndent("{\n", writer, indent);
		writeIndent("\"count\": " + fileResult.getCount() + ",\n", writer, indent + 1);
		writeIndent("\"score\": " + String.format("%.8f", fileResult.getScore()) + ",\n", writer, indent + 1);
		writeIndent("\"where\": \"" + fileResult.getWhere() + "\"\n", writer, indent + 1);
		writeIndent("}", writer, indent);
	}
}