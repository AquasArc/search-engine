package edu.usfca.cs272;

// TODO Configure Eclipse to remove unused imports for you
import java.time.Duration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.nio.file.FileVisitOption;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;

// TODO Tell Eclipse to fix indentation so it isn't a mix of tabs and spaces

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Anton Lim
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
	  
	  /**
	   * Start of the program.
	   *
	   * @param args Command-line arguments
	   */
	  public static void main(String[] args) {
	  	/*
	  	 * TODO
	  	 * Reduce the amount of console output...
	  	 * 
	  	 * 1) A little too complex with the argument handling
	  	 * 
	  	 * 2) A little too much is happening inside of Driver
	  	 * 
	  	 * Move most of what you have here with processing files and directories
	  	 * into a new class instead
	  	 */
	  	
	  	/* TODO 
	  	ArgumentParser parser = new ArgumentParser(args);
	  	
	  	if (parser.hasFlag("-text")) {
	  		Path inputPath = parser.getPath("-text");
	  		
	  		try {
	  			1 or 2 lines of code
	  		}
	  		catch ( ) {
	  			System.out.println("Could not process the file(s) at the input path: ...");
	  		}
	  	}
	  	
	  	if (-counts) {
	  		
	  	}
	  	*/
	  	
		  // Print initial args for debugging
		  System.out.println("Initial args: " + Arrays.toString(args));

		  // Create ArgumentParser with the command-line args
		  ArgumentParser parser = new ArgumentParser(args);
		  
		  boolean countsFlagProvided = parser.hasFlag("-counts");

		  // Get the input and output paths
		  Path inputPath = parser.getPath("-text");
		  Path outputPath = parser.getPath("-counts");
		  
		  // Self-Note: If only -counts flag is provided, maybe solution is to write an empty file?
		  // If only -counts flag is provided, write an empty file
		  if (inputPath == null && countsFlagProvided) {
		      if (outputPath == null) {
		          outputPath = Paths.get("counts.json"); // Set a default if none provided
		      }
		      try {
		          Files.writeString(outputPath, "{}");
		      } catch (IOException e) {
		          System.out.println("Failed to write empty JSON file.");
		      }
		      return;
		  }


		  // If only -text is provided, don't create any output files
		  if (inputPath != null && outputPath == null && !countsFlagProvided) {
			  return;
		  }

		  // Set default output path only if both -text and -counts are provided
		  if (outputPath == null && inputPath != null && countsFlagProvided) {
			  outputPath = Paths.get("counts.json");  // Default
		  }
		  
		  
		  // Print the paths for debugging
		  System.out.println("Parsed Input Path: " + (inputPath == null ? "null" : inputPath.toString()));
		  System.out.println("Parsed Output Path: " + (outputPath == null ? "null" : outputPath.toString()));
		  
		  
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

	  /**
	  * Processes a single file and returns its word count.
	  *
	  * @param filePath Path of the file to process
	  * @return The word count of the file
	  */
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
	  
	  /**
	   * Processes a directory and writes word counts to an output file.
	   *
	   * @param dirPath Path of the directory to process
	   * @param outputPath Path of the output file
	   */
	  public static void processDirectory(Path dirPath, Path outputPath) {
	  	// TODO Avoid functional at this stage
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

	  
	  /**
	   * Counts the number of words in a file.
	   *
	   * @param filePath Path of the file to count words in
	   * @return The word count of the file
	   * @throws IOException If file reading fails
	   */
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

	
