/**
 * Copyright Linebee LLC
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
package com.linebee.solrmeter.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
/**
 * 
 * Utility methods related with Files
 * @author Tomas
 *
 */
public class FileUtils {
	
	private static Logger logger = Logger.getLogger(FileUtils.class);

	/**
	 * Loads from the file with path "filePath" all lines as strings.
	 * @param filePath Path to file
	 * @return The list of strings loaded from tthe file.
	 */
	public static List<String> loadStringsFromFile(String filePath) {
		InputStream stream;
		List<String> list = new LinkedList<String>();
		try {
//			stream = FileUtils.class.getResourceAsStream(filePath);
			stream = new FileInputStream(new File(filePath));
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String nextLine = reader.readLine();
			while(nextLine != null) {
				list.add(nextLine);
				nextLine = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		return list;
	}
	/**
	 * Loads from the file with path "filePath" all lines as strings.
	 * @param resourceName
	 * @return
	 */
	public static List<String> loadStringsFromResource(String resourceName) {
		String path = ClassLoader.getSystemClassLoader().getResource(resourceName).getPath();
		return loadStringsFromFile(path);
	}
	
	/**
	 * 
	 * @param list
	 * @return Returns a random object from the list
	 */
	public static Object getNextRandomObject(List<?> list) {
		int index = (int) (Math.random() * list.size());
		return list.get(index);
	}
}
