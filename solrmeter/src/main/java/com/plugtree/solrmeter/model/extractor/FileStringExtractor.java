/**
 * Copyright Plugtree LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.plugtree.solrmeter.model.extractor;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.plugtree.solrmeter.model.FileUtils;

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
		if(filePath == null) {
			Logger.getLogger(this.getClass()).info("No path specified for FileStringExtractor. No Strings added");
			strings = new LinkedList<String>();
		} else {
			loadStrings(filePath);
		}
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
	 * @return null if the strings list is null or empty
	 */
	public String getRandomString() {
		if(strings == null || strings.isEmpty()) {
			return null;
		}
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
