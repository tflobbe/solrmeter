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
package com.plugtree.solrmeter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.plugtree.solrmeter.model.FileUtils;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;

public class SolrMeterConfigurationTestCase extends BaseTestCase {

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		SolrMeterConfiguration.loadConfiguration();
	}

	public void testBasics() {
		SolrMeterConfiguration.loadDefaultConfiguration();
		assertEquals("test", SolrMeterConfiguration.getProperty("test.testProperty"));
		SolrMeterConfiguration.setProperty("test.testProperty", "test2");
		assertEquals("test2", SolrMeterConfiguration.getProperty("test.testProperty"));
		assertNull(SolrMeterConfiguration.getProperty("propertyThatDoesntExist"));
		assertEquals("some value", SolrMeterConfiguration.getProperty("propertyThatDoesntExist", "some value"));
	}
	
	public void testExport() throws FileNotFoundException {
		File exportFile = new File("SolrMeterConfigurationTestCase-testExport");
		if(exportFile.exists()) {
			exportFile.delete();
		}
		
		SolrMeterConfiguration.loadDefaultConfiguration();
		assertEquals("test", SolrMeterConfiguration.getProperty("test.testProperty"));
		SolrMeterConfiguration.setProperty("test.testProperty", "changedPropertyValue");
		assertFalse(exportFile.exists());
		try {
			SolrMeterConfiguration.exportConfiguration(exportFile);
		} catch (IOException e) {
			fail(e);
		}
		assertTrue(exportFile.exists());
		
		BufferedReader reader = new BufferedReader(new FileReader(exportFile));
		String line;
		try {
			line = reader.readLine();
			boolean foundProperty = false;
			while(line != null && !foundProperty) {
				if(line.contains("test.testProperty")) {
					assertTrue(line.contains("changedPropertyValue"));
					foundProperty = true;
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			fail(e);
		}
		exportFile.delete();
		
	}
	
	public void testImport() throws FileNotFoundException {
		File importFile = new File(FileUtils.findFileAsString("testImport.smc.xml"));
		if(!importFile.exists()) {
			throw new FileNotFoundException("Can't find file for import test");
		}
		assertNull(SolrMeterConfiguration.getProperty("test.import.key"));
		try {
			SolrMeterConfiguration.importConfiguration(importFile);
		} catch (IOException e) {
			fail(e);
		}
		assertEquals("importValue", SolrMeterConfiguration.getProperty("test.import.key"));
	}
	
	public void testGetKeys() {
		SolrMeterConfiguration.setProperty("test.testProperty1", "test1");
		SolrMeterConfiguration.setProperty("test.testProperty2", "test2");
		SolrMeterConfiguration.setProperty("test.testProperty3", "test3");
		SolrMeterConfiguration.setProperty("test.testProperty4", "test4");
		SolrMeterConfiguration.setProperty("test.testProperty5", "test5");
		SolrMeterConfiguration.setProperty("test.testProperty6", "test6");
		List<String> keys = SolrMeterConfiguration.getKeys(Pattern.compile("test\\.testProperty."));
		assertEquals(6, keys.size());
		for(int i = 1; i <=6; i++) {
			assertTrue(keys.contains("test.testProperty" + i));
		}
	}
}
