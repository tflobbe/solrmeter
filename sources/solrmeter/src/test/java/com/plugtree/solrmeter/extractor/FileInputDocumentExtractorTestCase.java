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

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.model.FileUtils;

public class FileInputDocumentExtractorTestCase extends BaseTestCase {

	public void testSingleDoc() throws FileNotFoundException {
		
		FileInputDocumentExtractorSpy extractor = new FileInputDocumentExtractorSpy(FileUtils.findFileAsString("FileInputDocumentExtractorTestCase1.txt"));
		assertEquals(1, extractor.getParsedDocuments().size());
		
		for(int i = 0; i < 10; i++) {
			SolrInputDocument document = extractor.getRandomDocument();
	//		fieldName1=value1;fieldName2=value2;fieldName3=value3
			Iterator<Object> values = document.getFieldValues("fieldName1").iterator();
			assertEquals("value1", values.next());
			assertEquals("value2", values.next());
			assertEquals("value2", document.getFieldValue("fieldName2"));
			assertEquals("value3", document.getFieldValue("fieldName3"));
		}
	}
	
	public void testManyDocs() throws FileNotFoundException {
		
		FileInputDocumentExtractorSpy extractor = new FileInputDocumentExtractorSpy(FileUtils.findFileAsString("FileInputDocumentExtractorTestCase2.txt"));
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
	
	public void testLoadDocuments() throws FileNotFoundException {
		FileInputDocumentExtractorSpy executor = new FileInputDocumentExtractorSpy(FileUtils.findFileAsString("FileInputDocumentExtractorTestCase3.txt"));
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
	
	public void testEscapedChars() throws FileNotFoundException {
		FileInputDocumentExtractorSpy executor = new FileInputDocumentExtractorSpy(FileUtils.findFileAsString("FileInputDocumentExtractorTestCase4.txt"));
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
