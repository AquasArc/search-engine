package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;


/**
 * Represents a thread safe version of the query processor
 * 
 * @author Anton Lim
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class ThreadSafeQueryProcessor extends QueryProcessor {

	/** Creating a MultiReaderLock object */
	private final MultiReaderLock lock;


	public ThreadSafeQueryProcessor(InvertedIndex index, boolean isPartial) {
		super(index, isPartial);
		this.lock = new MultiReaderLock();
	}

	@Override
	/** To string method... 
	 * 
	 * @return toString method..
	 */
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasQuery(String query) {
		lock.readLock().lock();
		try {
			return super.hasQuery(query);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasFileResults(String query) {
		lock.readLock().lock();
		try {
			return super.hasFileResults(query);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numResultsForQuery(String query) {
		lock.readLock().lock();
		try {
			return super.numResultsForQuery(query);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numQueriesProcessed() {
		lock.readLock().lock();
		try {
			return super.numQueriesProcessed();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getQueries() {
		lock.readLock().lock();
		try {
			return super.getQueries();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<InvertedIndex.FileResult> getResultsForQuery(String query) {
		lock.readLock().lock();
		try {
			return super.getResultsForQuery(query);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void processQuery(String line) {
		lock.writeLock().lock();
		try {
			super.processQuery(line);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void writeResults(Path outputPath) throws IOException {
		lock.readLock().lock();
		try {
			super.writeResults(outputPath);
		} finally {
			lock.readLock().unlock();
		}
	}

}
