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
import java.nio.file.Paths; // TODO Unused import. Do you see the warning here?
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

		writer.write("[");

		if (iterator.hasNext()) {
			writer.write("\n");
			writeIndent(iterator.next().toString(), writer, indent + 1);
		}

		while (iterator.hasNext()) {
			writer.write(",\n");
			writeIndent(iterator.next().toString(), writer, indent + 1);
		}

		writer.write("\n");
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

	    if (!elements.isEmpty()) {
	    	// TODO Use var, and same approach everywhere. The writeArray uses the iterator and checks hasNext instead of isEmpty.
	    	java.util.Iterator<? extends Map.Entry<String, ? extends Number>> iterator = elements.entrySet().iterator();
	        Map.Entry<String, ? extends Number> entry = iterator.next();

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
		// TODO Fix this one too. When I give you a TODO, fix it everywhere in your code. Not just where I put it. 
	    if (elements.isEmpty()) {
	        writer.write("{\n");
	        writeIndent(writer, indent);
	        writer.write("}\n");
	        return;
	    }

	    writer.write("{\n");

	    boolean firstEntry = true;
	    for (Map.Entry<String, ? extends Collection<? extends Number>> entry : elements.entrySet()) {
	        if (!firstEntry) {
	            writer.write(",\n");
	        }
	        writeIndent(writer, indent + 1);
	        writeQuote(entry.getKey(), writer, 0);
	        writer.write(": ");
	        writeArray(entry.getValue(), writer, indent + 1);
	        firstEntry = false;
	    }

	    writer.write("\n");
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
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeObject(Map)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Writer writer, int indent) throws IOException {
	    writer.write("[");

	    if (!elements.isEmpty()) {
	        writer.write("\n");
	        
	        // TODO var helps with readability here
	        java.util.Iterator<? extends Map<String, ? extends Number>> iterator = elements.iterator();
	        Map<String, ? extends Number> currentMap = iterator.next();
	        
	        writeIndent(writer, indent + 1);
	        writeObject(currentMap, writer, indent + 1);

	        while (iterator.hasNext()) {
	            writer.write(",\n");
	            currentMap = iterator.next();
	            
	            writeIndent(writer, indent + 1);
	            writeObject(currentMap, writer, indent + 1);
	        }
	    }

	    writer.write("\n");
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

	/*
	 * TODO Think about the design of the other methods. Are you learning from them?
	 * How is the design of your method below different? What could you change to make
	 * it more reusable? How about creating the buffered reader? What do you know about
	 * convering to the File class?
	 * 
	 * Apply what you are learning in the class to your code and practice making these
	 * design choices based on the examples given to you!
	 */
	/**
	 * Writes the given inverted index to a file in JSON format.
	 *
	 * @param index The inverted index map to write.
	 * @param outputPath The path where the JSON should be saved.
	 * @throws IOException If there's an issue writing the file.
	 */
	public static void writeIndexToFile(Map<String, TreeMap<String, TreeSet<Integer>>> index, Path outputPath) throws IOException {
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
	        writer.write("{\n");

	        if (index.isEmpty()) {
	            writer.write("}");
	            return;
	        }
	        
	        int outerCount = 0;
	        for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> wordEntry : index.entrySet()) {
	            if (outerCount > 0) { // Add a comma if this isn't the first entry
	                writer.write(",\n");
	            }
	            writeQuote(wordEntry.getKey(), writer, 1);
	            writer.write(": ");
	            
	            writeObjectArrays(wordEntry.getValue(), writer, 1);

	            outerCount++;
	        }
	        
	        writer.write("\n}");
	        
	    } catch (IOException e) {
	        throw new IOException("Failed to write index to: " + outputPath, e);
	    }
	}




	// TODO Why do you need this method? Don't you already have a writeObject method?
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
