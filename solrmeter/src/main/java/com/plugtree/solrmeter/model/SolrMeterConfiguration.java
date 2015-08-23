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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import com.plugtree.solrmeter.runMode.SolrMeterRunModeHeadless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds all the current configuration. The settings page will change the values of this configuration
 * but the changes won't be persistent unless a exportConfiguration method is invoked.
 * @author tflobbe
 *
 */
public class SolrMeterConfiguration {
	
	public static final String DOCUMENTS_ID = "solr.documentIdField";
	public static final String FIELDS_FILE_PATH = "solr.documentFieldsFile";
	public static final String QUERY_TYPE = "solr.search.queryType";
	public static final String SOLR_ADD_URL = "solr.addUrl";
    public static final String CONFIGURATION_FILE_PATH_PROPERTY = "solrmeter.configurationFile";
    public static final String RUN_MODE_PROPERTY = "solrmeter.runMode";
	public static String SOLR_SEARCH_URL = "solr.searchUrl";
	public static String QUERIES_FILE_PATH = "solr.queriesFiles";
	public static String UPDATES_FILE_PATH = "solr.updatesFiles";
	public static String DELETES_FILE_PATH = "solr.deletesFiles";
	public static String INSERTS_FILE_PATH = "solr.insertsFiles";
	public static String QUERIES_PER_MINUTE = "solr.load.queriesperminute";
	public static String UPDATES_PER_MINUTE = "solr.load.updatesperminute";
	public static String DELETES_PER_MINUTE = "solr.load.deletesperminute";
	public static String INSERTS_PER_MINUTE = "solr.load.deletesperminute";
	public static String TEST_TIME = "solr.testTime";
	public static String QUERY_METHOD = "solr.queryMethod";

	private static Logger logger = LoggerFactory.getLogger(SolrMeterConfiguration.class);
	private static String FILE_CONFIG_NAME = "solrmeter.properties";
	private static Properties prop = new Properties();
	private static Map<String, String> transientProperties = new HashMap<String, String>();

	static {
		loadConfiguration();
	}
	
	private SolrMeterConfiguration() {
	}
	
	/**
	 * Returns the value currently being used for the property with key = 'name'
	 * or null if the key is not found. If a systsme property exists with the same key
	 * it will be returned. Configuration specified as system property can be changed 
	 * on runtime.
	 * @param name the key of the property
	 * @return 	the system property if there is one for the key 'name'
	 * 			the configuration property if there is one for the key 'name'
	 * 			null if there is no configuration for the key 'name'
	 */
	public static String getProperty(String name) {
		return getProperty(name, null);
	}
	
	/**
	 * Returns the value currently being used for the property with key = 'name'
	 * or 'defaultValue' if the key is not found. If a systsme property exists with the same key
	 * it will be returned. Configuration specified as system property can be changed 
	 * on runtime.
	 * @param name the key of the property
	 * @return 	the system property if there is one for the key 'name'
	 * 			the configuration property if there is one for the key 'name'
	 * 			'defaultValue' if there is no configuration for the key 'name'
	 */
	public static String getProperty(String name, String defaultValue) {
		String systemProp = System.getProperty(name);
		if(systemProp != null) {
			return systemProp;
		}
		return prop.getProperty(name, defaultValue);
	}
	
	/**
	 * Sets the current configuration property for the key 'propertyName' to
	 * the value 'value' if it exists, or create a new configuration property for the
	 * key 'propertyName'. System properties can't be changed on runtime.
	 * @param propertyName the key of the configuration property
	 * @param value the value of the configuration
	 * @return the previous value of the specified key in this property list, or null if it did not have one.
	 */
	public static String setProperty(String propertyName, String value) {
		return (String) prop.setProperty(propertyName, value);
	}
	
	/**
	 * Removes the key (and its corresponding value) from the configuration. This method does nothing if the key is not in the configuration. 
	 * @param propertyKey
	 */
	public static void removeProperty(String propertyKey) {
		prop.remove(propertyKey);
	}
	
	/**
	 * 
	 * @param pattern 
	 * @return All the configuration properties that match the specified pattern. System properties
	 * are not included.
	 */
	public static List<String> getKeys(Pattern pattern) {
		List<String> keys = new LinkedList<String>();
		for(Object propertyKey:prop.keySet()) {
			if(pattern.matcher((String)propertyKey).matches()) {
				keys.add((String)propertyKey);
			}
		}
		return keys;
	}
	
	public static String getTransientProperty(String propertyName) {
		return transientProperties.get(propertyName);
	}
	
	public static void setTransientProperty(String propertyName, String propertyValue) {
		transientProperties.put(propertyName, propertyValue);
	}
	
	/**
	 * imports an existing configuration file to this test
	 * @param configurationFile
	 * @throws IOException
	 */
	public static void importConfiguration(File configurationFile) throws IOException {
		prop.clear();
		FileInputStream inputStrean;
		try {
			inputStrean = new FileInputStream(configurationFile);
			prop.loadFromXML(inputStrean);
		} catch (IOException e) {
			logger.error("Failed to Import Configuration", e);
			throw e;
		}
	}
	
	/**
	 * Creates a file with the current configuration so it can be loaded in further tests.
	 * @param file
	 * @throws IOException
	 */
	public static void exportConfiguration(File file) throws IOException {
		if(file.exists()) {
			file.delete();
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			prop.storeToXML(fos, "Solr Meter Configuration File. Generated at " + SimpleDateFormat.getDateInstance().format(new Date()));
		} catch (IOException e) {
			logger.error("Failed to export configuration", e);
			throw e;
		}
	}
	
	/**
	 * Forces the loading of the default configuration. Used by tests.
	 * 
	 */
	public static void loadDefaultConfiguration(){
		loadConfiguration(FILE_CONFIG_NAME);		
	}
	
	/**
	 * Loads the configuration from the available files. If a file name is specified as a VM parameter, that file will be used, 
	 * otherwise, the default configuration file will be used. 
	 * @see SolrMeterConfiguration.getDefaultFile()
	 */
	public static void loadConfiguration() {
		String fileName = System.getProperty(CONFIGURATION_FILE_PATH_PROPERTY);
		if(fileName == null) {
			fileName = getDefaultFile();
			LoggerFactory.getLogger("boot").info("Loading Default configuration");
		}
		if(isXML(fileName)) {
			try {
				importConfiguration(new File(FileUtils.findFileAsString(fileName)));
			} catch (FileNotFoundException e) {
				logger.error("File '" + fileName + "' was not found. Will load default configuration.", e);
				loadConfiguration(getDefaultFile());
			} catch (IOException e) {
				logger.error("There was an error trying to load configuration from file '" + fileName + "', will use the default configuration.", e);
				loadConfiguration(getDefaultFile());
			}
		} else {
			loadConfiguration(fileName);
		}
		
	}

    public static boolean isHeadless() {
        return getProperty(RUN_MODE_PROPERTY, "").equals(SolrMeterRunModeHeadless.RUN_MODE_NAME);
    }
	
	/**
	 * The default file can be an external file located at the same directory as solrmeter with the name
	 * solrmeter.properties or solrmeter.smc.xml. If non of this exists, then the default configuration file
	 * will be the the file located inside the SolrMeter jar called "solrmeter.properties"
	 * @return
	 */
	private static String getDefaultFile() {
		File file = new File("./" + FILE_CONFIG_NAME);
		if(file.exists()) {
			return "./" + FILE_CONFIG_NAME;
		}
		file = new File("./" + FILE_CONFIG_NAME.replace(".properties", ".smc.xml"));
		if(file.exists()) {
			return "./" + FILE_CONFIG_NAME.replace(".properties", ".smc.xml");
		}
		return FILE_CONFIG_NAME;
	}

	/**
	 * Loads the configuration for the configuration file named 'fileName'
	 * @param fileName The name of the configuration file
	 */
	private static void loadConfiguration(String fileName) {
		LoggerFactory.getLogger("boot").info("Loading Configuration with file " + fileName);
		InputStream inStream = null;
		try {
			inStream = FileUtils.findFileAsStream(fileName);
			if(inStream == null) {
				LoggerFactory.getLogger("boot").info("Configuration File " + fileName + " not found");
				throw new FileNotFoundException("Configuration File " + fileName + " not found");
			}
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		try {
			prop.clear();
			prop.load(inStream);
		} catch (IOException e) {
			LoggerFactory.getLogger("boot").error("Error", e);
		}
	}
	
	/**
	 * Simple method that is used to determine wether to asume the file as an XML file (exported from SolrMeter)
	 * or not (otherwise it will be considered as a .properties file)
	 * @param fileName
	 * @return
	 */
	private static boolean isXML(String fileName) {
		return fileName.endsWith(".smc.xml");
	}


}
