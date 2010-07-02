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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tflobbe
 *
 */
public class SolrMeterConfiguration {
	
	public static final String DOCUMENTS_ID = "solr.documentIdField";
	public static final String FIELDS_FILE_PATH = "solr.documentFieldsFile";
	public static final String QUERY_TYPE = "solr.search.queryType";
	public static final String SOLR_ADD_URL = "solr.addUrl";
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
	
	private static Logger logger = LoggerFactory.getLogger(SolrMeterConfiguration.class);
	private static String FILE_CONFIG_NAME = "solrmeter.properties";
	private static Properties prop = new Properties();

	static {
		loadDefatultConfiguration();
	}
	
	private SolrMeterConfiguration() {
	}
	
	public static String getProperty(String name) {
		return getProperty(name, null);
	}
	
	public static String getProperty(String name, String defaultValue) {
		return prop.getProperty(name, defaultValue);
	}
	
	public static String setProperty(String propertyName, String value) {
		return (String) prop.setProperty(propertyName, value);
	}
	
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
	
	public static void loadDefatultConfiguration() {
		LoggerFactory.getLogger("boot").info("Loading Default configuration");
		InputStream inStream = SolrMeterConfiguration.class.getClassLoader().getResourceAsStream(
				FILE_CONFIG_NAME);
		try {
			if(inStream == null) {
				LoggerFactory.getLogger("boot").info("Configuration File " + FILE_CONFIG_NAME + " not found");
				throw new FileNotFoundException("Configuration File " + FILE_CONFIG_NAME + " not found");
			}
			LoggerFactory.getLogger("boot").info("Srteam availability: " + inStream.available());
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
	
}
