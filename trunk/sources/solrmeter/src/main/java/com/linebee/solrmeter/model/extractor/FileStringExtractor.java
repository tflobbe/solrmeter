package com.linebee.solrmeter.model.extractor;

import java.util.LinkedList;
import java.util.List;

import com.linebee.solrmeter.model.FileUtils;

/**
 * This class obtains all lines from a txt file and stores them. Then, client classes can
 * request for all, some or some random string.
 * @author tflobbe
 *
 */
public class FileStringExtractor {

	/**
	 * List of all strings obtained from the file
	 */
	protected List<String> strings;
	
	public FileStringExtractor(String filePath) {
		super();
		loadStrings(filePath);
	}
	
	/**
	 * Load all strings from the strings file.
	 */
	protected void loadStrings(String filePath) {
		strings = FileUtils.loadStringsFromFile(filePath);
	}

	/**
	 * 
	 * @return A random string of the ones obtained from the file
	 */
	public String getRandomString() {
		return (String)FileUtils.getNextRandomObject(strings);
	}
	
	/**
	 * 
	 * @return All Strings obtained from the file.
	 */
	public List<String> getAllStrings() {
		return new LinkedList<String>(strings);
	}
	
}
