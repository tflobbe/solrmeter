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
package com.plugtree.solrmeter.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.plugtree.solrmeter.SolrMeterMain;
/**
 * 
 * Utility methods related with Files
 * @author tflobbe
 *
 */
public class FileUtils {
	
    public static final String UTF8_BOM = "\uFEFF";
	
	private static Logger logger = Logger.getLogger(FileUtils.class);

	/**
	 * Loads from the file with path "filePath" all lines as strings.
	 * @param filePath Path to file
	 * @return The list of strings loaded from the file.
	 */
	public static List<String> loadStringsFromFile(String filePath) {
		InputStream stream;
		List<String> list = new LinkedList<String>();
		try {
			stream = findFileAsStream(filePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, SolrMeterConfiguration.getProperty("files.charset", "UTF-8")));
			String nextLine = reader.readLine();
			//workaround for these issues: http://bugs.sun.com/view_bug.do?bug_id=4508058 and http://bugs.sun.com/view_bug.do?bug_id=6378911
			if(nextLine != null && nextLine.length() >= 1) {
				if(nextLine.startsWith(UTF8_BOM)) {
					nextLine = nextLine.substring(1);
				}
			}
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
	
	public static InputStream findFileAsStream(String filePath) throws FileNotFoundException {
		logger.debug("looking for file " + filePath);
		File file = new File(filePath);
		if(file != null && file.exists()) {
			try {
				logger.debug(filePath + " was found as a file");
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(filePath);
		if(stream != null) {
			logger.debug(filePath + " was found with the system classloader");
			return stream;
		}
		stream = FileUtils.class.getClassLoader().getResourceAsStream(filePath);
		if(stream != null) {
			logger.debug(filePath + " was found with the actual class classloader");
			return stream;
		}
		stream = SolrMeterMain.class.getClassLoader().getResourceAsStream(filePath);
		if(stream != null) {
			logger.debug(filePath + " was found with the main class classloader");
			return stream;
		}
		if (filePath.startsWith("./")) {
			return findFileAsStream(filePath.substring(2));
		}
		throw new FileNotFoundException("File could not be found on standard locations " + filePath);
	}
	
	public static URL findFileAsResource(String filePath) throws FileNotFoundException {
		logger.debug("looking for file " + filePath);
		File file = new File(filePath);
		if(file != null && file.exists()) {
			try {
				logger.debug(filePath + " was found as a file");
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		URL url = ClassLoader.getSystemClassLoader().getResource(filePath);
		if(url != null) {
			logger.debug(filePath + " was found with the system classloader");
			return url;
		}
		url = FileUtils.class.getClassLoader().getResource(filePath);
		if(url != null) {
			logger.debug(filePath + " was found with the actual class classloader");
			return url;
		}
		url = SolrMeterMain.class.getClassLoader().getResource(filePath);
		if(url != null) {
			logger.debug(filePath + " was found with the main class classloader");
			return url;
		}
		if (filePath.startsWith("./")) {
			return findFileAsResource(filePath.substring(2));
		}
		throw new FileNotFoundException("File could not be found on standard locations " + filePath);
	}
	
	public static String findFileAsString(String filePath) throws FileNotFoundException {
		URL fileUrl = findFileAsResource(filePath);
		try {
			return URLDecoder.decode(fileUrl.getPath(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("UTF-8 encoding not supported");
			throw new RuntimeException(e);
		}
	}
}
