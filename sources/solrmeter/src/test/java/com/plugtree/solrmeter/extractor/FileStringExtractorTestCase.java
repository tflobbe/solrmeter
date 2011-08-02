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
package com.plugtree.solrmeter.extractor;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.model.FileUtils;

public class FileStringExtractorTestCase extends BaseTestCase {
	
	private File queriesFile;
	
	@Override
	protected void setUp() throws Exception {
		queriesFile = new File(FileUtils.findFileAsString("./com/plugtree/solrmeter/queriesTest.txt"));
	}

	public void testExtraxt() {
		FileStringExtractorSpy extractor = new FileStringExtractorSpy(queriesFile.getPath());
		List<String> strings = extractor.getParsedStrings();
		assertTrue(strings.contains(""));
		assertTrue(strings.contains("some"));
		assertTrue(strings.contains("query"));
		assertTrue(strings.contains("some query"));
		assertTrue(strings.contains("field:value"));
	}
	
	/**
	 * It just tests the random functionality not to break
	 */
	public void testRandomQuery() {
		List<String> possibleQueries = new LinkedList<String>();
		possibleQueries.add("");
		possibleQueries.add("some");
		possibleQueries.add("query");
		possibleQueries.add("some query");
		possibleQueries.add("field:value");
		FileStringExtractorSpy extractor = new FileStringExtractorSpy(queriesFile.getPath());
		for(int i = 0; i < 100; i++) {
			assertTrue(possibleQueries.contains(extractor.getRandomString()));
		}
	}
}
