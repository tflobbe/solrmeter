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
package com.linebee.solrmeter.extractor;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;

import com.linebee.solrmeter.BaseTestCase;

public class FileInputDocumentExtractorTestCase extends BaseTestCase {

	public void testSingleDoc() {
		
		File inputFile = new File(ClassLoader.getSystemClassLoader().getResource("FileInputDocumentExtractorTestCase1.txt").getPath());
		FileInputDocumentExtractorSpy extractor = new FileInputDocumentExtractorSpy(inputFile);
		assertEquals(1, extractor.getParsedDocuments().size());
		for(int i = 0; i < 10; i++) {
			SolrInputDocument document = extractor.getRandomDocument();
	//		fieldName1=value1;fieldName2=value2;fieldName3=value3
			assertEquals("value1", document.getFieldValue("fieldName1"));
			assertEquals("value2", document.getFieldValue("fieldName2"));
			assertEquals("value3", document.getFieldValue("fieldName3"));
		}
	}
	
	public void testManyDocs() {
		
		File inputFile = new File(ClassLoader.getSystemClassLoader().getResource("FileInputDocumentExtractorTestCase2.txt").getPath());
		FileInputDocumentExtractorSpy extractor = new FileInputDocumentExtractorSpy(inputFile);
		assertEquals(21, extractor.getParsedDocuments().size());
		Set<Integer> set = new HashSet<Integer>();
		for(int i = 0; i < 100; i++) {
			SolrInputDocument document = extractor.getRandomDocument();
	//		fieldName1=value1;fieldName2=value2;fieldName3=value3
			assertNotNull(document.getFieldValue("fieldName1"));
			set.add(Integer.parseInt((String)document.getFieldValue("fieldName1")));
			assertEquals("value2", document.getFieldValue("fieldName2"));
			assertEquals("value3", document.getFieldValue("fieldName3"));
		}
		
		assertTrue(set.size() > 1 && set.size() <=21);
	}
	
	public void testLoadDocuments() {
		File inputFile = new File(ClassLoader.getSystemClassLoader().getResource("FileInputDocumentExtractorTestCase3.txt").getPath());
		FileInputDocumentExtractorSpy executor = new FileInputDocumentExtractorSpy(inputFile);
		List<SolrInputDocument> documents = executor.getParsedDocuments();
		assertEquals(5, documents.size());
		SolrInputDocument document = documents.get(0);
		assertEquals("1", document.getFieldValue("documentId"));
		assertEquals("11", document.getFieldValue("entryId"));
		assertEquals("ABC", document.getFieldValue("type"));
		assertEquals("F", document.getFieldValue("active"));
		assertEquals("2", document.getFieldValue("value"));
		assertEquals("Mon Mar 06 00:00:00 ART 2006", document.getFieldValue("date"));
	}
	
	public void testScapedChars() {
		File inputFile = new File(ClassLoader.getSystemClassLoader().getResource("FileInputDocumentExtractorTestCase4.txt").getPath());
		FileInputDocumentExtractorSpy executor = new FileInputDocumentExtractorSpy(inputFile);
		List<SolrInputDocument> documents = executor.getParsedDocuments();
		assertEquals(5, documents.size());
		SolrInputDocument document = documents.get(0);
		assertEquals("1", document.getFieldValue("documentId"));
		assertEquals("11;2", document.getFieldValue("entryId"));
		assertEquals("ABC\\", document.getFieldValue("type"));
		assertEquals("F\\\\", document.getFieldValue("active"));
		assertEquals("2:5", document.getFieldValue("value"));
		assertEquals("Mon Mar 06 00:00:00 ART 2006", document.getFieldValue("date"));
	}
}
