package edu.usfca.cs272;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents results of a file search, including the location of the file,
 * word count, score based on search criteria, and the total number of words
 * in the file.
 * 
 * @author Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 * 
 *
 */
public class FileResult implements Comparable<FileResult> {

	/** contains the information for location*/
	private final String location;

	/** contains the information for count */
	private int count = 0;

	/** contains the information for score*/
	private double score = 0.0;

	/** contains the information for totalwords*/
	private final long totalWords;

	/**
	 * Returns the count.
	 *
	 * @return the count value.
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * Returns the score.
	 *
	 * @return the score value.
	 */
	public double getScore() {
		return this.score;
	}

	/**
	 * Returns the where string.
	 *
	 * @return the where location.
	 */
	public String getWhere() {
		return this.location;
	}

	/**
	 * Initializes a new FileResult with the given location and total word count.
	 * 
	 * @param location    The location (path) of the file.
	 * @param totalWords  The total number of words in the file.
	 */
	public FileResult(String location, long totalWords) {
		this.location = location;
		this.totalWords = totalWords;
	}

	/**
	 * Increments the word count by a given value and updates the score.
	 * 
	 * @param value The value to increment the word count by.
	 */
	public void incrementCount(int value) {
		this.count += value;
		updateScore();
	}

	/**
	 * Updates the score based on the current word count and total words.
	 */
	private void updateScore() {
		if (totalWords != 0) {
			this.score = (double) count / totalWords;
		}
	}

	/**
	 * Converts the properties of this object to a map.
	 * 
	 * @return A map representation of this object.
	 */
	public Map<String, Object> asMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("where", location);
		map.put("count", count);
		map.put("score", String.format("%.8f", score));  // to ensure 8 decimal places
		return map;
	}


	/** Compares the score + the count
	 * 
	 */
	@Override
	public int compareTo(FileResult other) {
		int scoreCompare = Double.compare(other.score, this.score);
		if (scoreCompare != 0) {
			return scoreCompare;
		}

		int countCompare = Integer.compare(other.count, this.count);
		if (countCompare != 0) {
			return countCompare;
		}

		return this.location.compareToIgnoreCase(other.location);
	}

	/**To string method to test if variables have the proper values
	 * 
	 */
	@Override
	public String toString() {
		return "\nCount: " + count + ",\nScore: " + score + "\nLocation: " + location;
	}
}
