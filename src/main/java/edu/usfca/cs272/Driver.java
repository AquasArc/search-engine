package edu.usfca.cs272;

import java.time.Duration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.nio.file.FileVisitOption;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;



/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author TODO Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class Driver {
	  /* Important Self-Reminder: 
	  * Maybe for a design idea, create a helper function to deal
	  * with exception handling.
	  * Also, I currently am some what hard coding the counts,
	  * input and output flags. I need to figure out later how to 
	  * make it more universal...
	  */
	
	  public static void main(String[] args) {
		  // Print initial args for debugging
		  System.out.println("Initial args: " + Arrays.toString(args));

		  // Instantiate ArgumentParser with the command-line args
		  ArgumentParser parser = new ArgumentParser(args);

		  // Get the input and output paths
		  Path inputPath = parser.getPath("-text");
		  Path outputPath = parser.getPath("-counts");
		  
		  // If outputPath is null, set a default path
		  if (outputPath == null) {
		      outputPath = Paths.get("counts.json"); // Default
		  }
		  
		  // Print the paths for debugging
		  System.out.println("Parsed Input Path: " + (inputPath == null ? "null" : inputPath.toString()));
		  System.out.println("Parsed Output Path: " + (outputPath == null ? "null" : outputPath.toString()));
		  
		  if (inputPath == null && outputPath != null) {
			  // Handle -counts only case
		      String jsonOutput = "{\n}"; // default empty JSON
		      try {
		    	  System.out.println("About to write to: " + outputPath.toString());
		    	  Files.writeString(outputPath, jsonOutput);
		      } catch (IOException e) {
		    	  System.out.println("Error writing to output file: " + outputPath);
		      }
		      return;
		  }
		  
		  if (inputPath != null) {
			  if (Files.isRegularFile(inputPath)) {
				  long wordCount = processFile(inputPath);

				  if (outputPath != null) {
					  String jsonOutput;
					  if (wordCount == 0) {
						  jsonOutput = "{\n}";
					  } else {
						  jsonOutput = String.format("{\n  \"%s\": %d\n}", inputPath.toString(), wordCount);
					  }

					  try {
					      // Print statement to log what is being written
					      System.out.println("About to write to: " + outputPath.toString());
					      System.out.println("Content to write: \n" + jsonOutput);
						  Files.writeString(outputPath, jsonOutput);
					  } catch (IOException e) {
						  System.out.println("Error writing to output file: " + outputPath);
					  }
				  }
			  } else if (Files.isDirectory(inputPath)) {
				  if (outputPath != null) {
					  processDirectory(inputPath, outputPath);
				  }
			  } else {
				  System.out.println("Invalid input path");
			  }
		  }
	  }

	  public static long processFile(Path filePath) {
	    long wordCount = 0;
	  
	    // Check if the filePath is a regular file
	    if (!Files.isRegularFile(filePath)) {
	      System.out.println("The provided path is not a file: " + filePath);
	      return 0;
	    }
	    
	    try {
	      // Count words in the file
	      wordCount = countWordsInFile(filePath);
	    } catch (IOException e) {
	      System.out.println("An error occurred while reading the file: " + filePath);
	    }
	    
	    return wordCount;
	  }
	  
	  public static void processDirectory(Path dirPath, Path outputPath) {
		    try (Stream<Path> paths = Files.walk(dirPath, FileVisitOption.FOLLOW_LINKS)) {
		        List<Path> filteredPaths = paths
		            .filter(Files::isRegularFile)
		            .filter(path -> path.toString().toLowerCase().endsWith(".txt") || path.toString().toLowerCase().endsWith(".text"))
		            .collect(Collectors.toList());

		        filteredPaths.sort((p1, p2) -> p1.toString().compareTo(p2.toString()));  // Sorting the string representations


		        
		        try (Writer writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
		            writer.write("{\n"); // Opening curly brace for JSON object

		            boolean firstEntry = true;
		            for (Path path : filteredPaths) {
		                long wordCount = countWordsInFile(path);  // Check existing word methods

		                if (wordCount > 0) {
		                    if (!firstEntry) {
		                        writer.write(",\n");  // Comma and newline for previous entry
		                    }
		                    firstEntry = false;

		                    writer.write("  \"");  // Indent and start of key
		                    writer.write(path.toString());
		                    writer.write("\": ");
		                    writer.write(Long.toString(wordCount));
		                }
		            }

		            writer.write("\n}");  // Close the JSON object
		        } catch (IOException e) {
		            System.out.println("An error occurred while writing to the output file: " + outputPath);
		        }
		    } catch (IOException e) {
		        System.out.println("An error occurred while reading the directory: " + dirPath);
		    }
		}

	  public static long countWordsInFile(Path filePath) throws IOException {
	    List<String> lines = Files.readAllLines(filePath);
	    long wordCount = 0;
	  
	    for (String line : lines) {
	      // Using the FileStemmer methods
	      String cleanedLine = FileStemmer.clean(line);  
	      String[] words = FileStemmer.split(cleanedLine);
	  
	      // Count the words
	      wordCount += words.length;
	    }
	  
	    return wordCount;
	  }
	}

	
