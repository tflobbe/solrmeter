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
package com.linebee.solrmeter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.linebee.solrmeter.model.SolrMeterConfiguration;

public class SolrMeterConfigurationTestCase extends BaseTestCase {

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		SolrMeterConfiguration.loadDefatultConfiguration();
	}

	public void testBasics() {
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
		File importFile = new File(ClassLoader.getSystemClassLoader().getResource("testImport.smc.xml").getFile());
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
}
