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

import org.apache.solr.client.solrj.response.UpdateResponse;

import com.plugtree.solrmeter.mock.SolrServerMock;
import com.plugtree.solrmeter.mock.UpdateExecutorSpy;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.UpdateStatistic;
import com.plugtree.solrmeter.model.exception.CommitException;
import com.plugtree.solrmeter.model.exception.UpdateException;

/**
 * 
 * @author tflobbe
 *
 */
public class UpdateExecutorTestCase extends BaseTestCase {
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		SolrMeterConfiguration.loadConfiguration();
	}
//	TODO rethink this tests. Can't rely on java sleep
//	public void testMaxTimeBeforeCommit() throws InterruptedException {
//		SolrMeterConfiguration.setProperty("solr.update.timeToCommit", "1000");
//		SolrMeterConfiguration.setProperty("solr.update.solrAutocommit", "false");
//		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_PER_MINUTE, "0");
//		SolrMeterConfiguration.setProperty("solr.update.documentsToCommit", "1000");
//		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_FILE_PATH, "./src/test/resources/FileInputDocumentExtractorTestCase1.txt");
//		UpdateExecutorSpy updateExecutor = new UpdateExecutorSpy();
//		updateExecutor.prepare();
//		updateExecutor.start();
//		for(int i = 1; i <= 10; i++) {
//			updateExecutor.notifyAddedDocument(createUpdateResponse(1));
//			assertEquals(i, updateExecutor.getNotCommitedDocuments());
//		}
//		Thread.sleep(1100);
//		assertEquals(0, updateExecutor.getNotCommitedDocuments());
//		assertEquals(1, ((SolrServerMock)updateExecutor.getSolrServer()).getNumberOfCommits());
//		updateExecutor.stop();
//	}
//	
//	public void testIncrementMaxTimeBeforeCommit() throws InterruptedException {
//		SolrMeterConfiguration.setProperty("solr.update.timeToCommit", "1000");
//		SolrMeterConfiguration.setProperty("solr.update.solrAutocommit", "false");
//		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_PER_MINUTE, "0");
//		SolrMeterConfiguration.setProperty("solr.update.documentsToCommit", "1000");
//		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_FILE_PATH, "./src/test/resources/FileInputDocumentExtractorTestCase1.txt");
//		UpdateExecutorSpy updateExecutor = new UpdateExecutorSpy();
//		updateExecutor.prepare();
//		updateExecutor.start();
//		updateExecutor.setMaxTimeBeforeCommit(2000);
//		for(int i = 1; i <= 10; i++) {
//			updateExecutor.notifyAddedDocument(createUpdateResponse(1));
//			assertEquals(i, updateExecutor.getNotCommitedDocuments());
//		}
//		Thread.sleep(500);
//		assertEquals(10, updateExecutor.getNotCommitedDocuments());
//		Thread.sleep(500);
//		assertEquals(10, updateExecutor.getNotCommitedDocuments());
//		Thread.sleep(500);
//		assertEquals(10, updateExecutor.getNotCommitedDocuments());
//		Thread.sleep(700);
//		assertEquals(0, updateExecutor.getNotCommitedDocuments());
//		assertEquals(1, ((SolrServerMock)updateExecutor.getSolrServer()).getNumberOfCommits());
//		updateExecutor.stop();
//		
//		SolrMeterConfiguration.setProperty("solr.update.timeToCommit", "2147483600");
//		updateExecutor.prepare();
//	}
//	
//	public void testDecrementMaxTimeBeforeCommit() throws InterruptedException {
//		SolrMeterConfiguration.setProperty("solr.update.timeToCommit", "1000");
//		SolrMeterConfiguration.setProperty("solr.update.solrAutocommit", "false");
//		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_PER_MINUTE, "0");
//		SolrMeterConfiguration.setProperty("solr.update.documentsToCommit", "1000");
//		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_FILE_PATH, "./src/test/resources/FileInputDocumentExtractorTestCase1.txt");
//		UpdateExecutorSpy updateExecutor = new UpdateExecutorSpy();
//		updateExecutor.prepare();
//		updateExecutor.start();
//		updateExecutor.setMaxTimeBeforeCommit(500);
//		while(((SolrServerMock)updateExecutor.getSolrServer()).getNumberOfCommits() == 0) {
//			//changed time before commit will be materialized on next commit
//		}
//		for(int i = 1; i <= 10; i++) {
//			updateExecutor.notifyAddedDocument(createUpdateResponse(1));
//			assertEquals(i, updateExecutor.getNotCommitedDocuments());
//		}
//		Thread.sleep(700);
//		assertEquals(0, updateExecutor.getNotCommitedDocuments());
//		assertEquals(2, ((SolrServerMock)updateExecutor.getSolrServer()).getNumberOfCommits());
//		updateExecutor.stop();
//		
//		try {
//			updateExecutor.setMaxTimeBeforeCommit(-1);
//			fail("Exception should be thrown");
//		}catch(RuntimeException e) {
//			//extected
//		}
//	}
	

	public void testMaxDocsBeforeCommit() throws InterruptedException{
		SolrMeterConfiguration.setProperty("solr.update.timeToCommit", "10000");
		SolrMeterConfiguration.setProperty("solr.update.solrAutocommit", "false");
		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_PER_SECOND, "0");
		SolrMeterConfiguration.setProperty("solr.update.documentsToCommit", "100");
		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_FILE_PATH, "./src/test/resources/FileInputDocumentExtractorTestCase1.txt");
		UpdateExecutorSpy updateExecutor = new UpdateExecutorSpy();
		updateExecutor.prepare();
		updateExecutor.start();
		Thread.sleep(100);//Wait to the thread to start correctly
		for(int i = 1; i < 100; i++) {
			updateExecutor.notifyAddedDocument(createUpdateResponse(1));
			assertEquals(i, updateExecutor.getNotCommitedDocuments());
		}
		updateExecutor.notifyAddedDocument(createUpdateResponse(1));
		Thread.sleep(500);//wait until the commiter thread perfoms commit
		assertEquals(0, updateExecutor.getNotCommitedDocuments());
		assertEquals(1, ((SolrServerMock)updateExecutor.getSolrServer()).getNumberOfCommits());
		updateExecutor.stop();
	}
	
	public void testDecrementMaxDocsBeforeCommit() throws InterruptedException{
		SolrMeterConfiguration.setProperty("solr.update.timeToCommit", "10000");
		SolrMeterConfiguration.setProperty("solr.update.solrAutocommit", "false");
		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_PER_SECOND, "0");
		SolrMeterConfiguration.setProperty("solr.update.documentsToCommit", "100");
		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_FILE_PATH, "./src/test/resources/FileInputDocumentExtractorTestCase1.txt");
		UpdateExecutorSpy updateExecutor = new UpdateExecutorSpy();
		updateExecutor.prepare();
		updateExecutor.start();
		Thread.sleep(100);//Wait to the thread to start correctly
		
		updateExecutor.setNumberOfDocumentsBeforeCommit(90);
		assertEquals(90, updateExecutor.getNumberOfDocumentsBeforeCommit().intValue());
		for(int i = 1; i < 90; i++) {
			updateExecutor.notifyAddedDocument(createUpdateResponse(1));
			assertEquals(i, updateExecutor.getNotCommitedDocuments());
		}
		updateExecutor.notifyAddedDocument(createUpdateResponse(1));
		Thread.sleep(500);//wait until the commiter thread perfoms commit
		assertEquals(0, updateExecutor.getNotCommitedDocuments());
		assertEquals(1, ((SolrServerMock)updateExecutor.getSolrServer()).getNumberOfCommits());
		updateExecutor.stop();
		
		try {
			updateExecutor.setNumberOfDocumentsBeforeCommit(-1);
			fail("Exception should be thrown");
		}catch(IllegalArgumentException e) {
			//extected
		}
		try {
            updateExecutor.setNumberOfDocumentsBeforeCommit(Integer.MAX_VALUE);
            fail("Exception should be thrown");
        }catch(IllegalArgumentException e) {
            //extected
        }
	}
	
	public void testIncrementMaxDocsBeforeCommit() throws InterruptedException{
		SolrMeterConfiguration.setProperty("solr.update.timeToCommit", "100000");
		SolrMeterConfiguration.setProperty("solr.update.solrAutocommit", "false");
		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_PER_SECOND, "0");
		SolrMeterConfiguration.setProperty("solr.update.documentsToCommit", "100");
		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_FILE_PATH, "./src/test/resources/FileInputDocumentExtractorTestCase1.txt");
		int documentsToCommit = 100;
		UpdateExecutorSpy updateExecutor = new UpdateExecutorSpy();
		updateExecutor.prepare();
		updateExecutor.start();
		Thread.sleep(100);//Wait to the thread to start correctly
		
		updateExecutor.setNumberOfDocumentsBeforeCommit(110);
		for(int i = 1; i < 110; i++) {
			updateExecutor.notifyAddedDocument(createUpdateResponse(1));
			assertEquals(i, updateExecutor.getNotCommitedDocuments());
		}
		updateExecutor.notifyAddedDocument(createUpdateResponse(1));
		Thread.sleep(500);//wait until the commiter thread perfoms commit
		assertEquals(0, updateExecutor.getNotCommitedDocuments());
		assertEquals(1, ((SolrServerMock)updateExecutor.getSolrServer()).getNumberOfCommits());
		updateExecutor.stop();
	}
	
	public void testAutocommit() {
		SolrMeterConfiguration.setProperty("solr.update.solrAutocommit", "true");
		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_PER_SECOND, "0");
		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_FILE_PATH, "./src/test/resources/FileInputDocumentExtractorTestCase1.txt");
		UpdateExecutorSpy updateExecutor = new UpdateExecutorSpy();
		UpdateTestSatistic statistic = new UpdateTestSatistic();
		updateExecutor.addStatistic(statistic);
		updateExecutor.prepare();
		updateExecutor.start();
		for(int i = 0; i < 100; i++) {
			updateExecutor.notifyAddedDocument(createUpdateResponse(1));
		}
		assertEquals(0, updateExecutor.getNotCommitedDocuments());
		assertEquals(0, ((SolrServerMock)updateExecutor.getSolrServer()).getNumberOfCommits());
		assertEquals(100, statistic.getAddedDocs());
		updateExecutor.stop();
	}
	
	private class UpdateTestSatistic implements UpdateStatistic {
		
		private int addedDocs = 0;

		@Override
		public void onAddError(UpdateException exception) {}

		@Override
		public void onAddedDocument(UpdateResponse response) {
			addedDocs++;
		}

		@Override
		public void onCommit(UpdateResponse response) {}

		@Override
		public void onCommitError(CommitException exception) {}

		@Override
		public void onFinishedTest() {}

		public int getAddedDocs() {
			return addedDocs;
		}
		
	}
	
	public void testManyAdds() throws InterruptedException {
		SolrMeterConfiguration.setProperty("solr.update.timeToCommit", "1000000");//Don't want to commit due to time
		SolrMeterConfiguration.setProperty("solr.update.solrAutocommit", "false");
		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_PER_SECOND, "0");
		SolrMeterConfiguration.setProperty("solr.update.documentsToCommit", "10");
		SolrMeterConfiguration.setProperty(SolrMeterConfiguration.UPDATES_FILE_PATH, "./src/test/resources/FileInputDocumentExtractorTestCase1.txt");
		UpdateExecutorSpy updateExecutor = new UpdateExecutorSpy();
		updateExecutor.prepare();
		updateExecutor.start();
		for(int i = 0; i < 100125; i++) {
			updateExecutor.notifyAddedDocument(createUpdateResponse(1));
		}
		Thread.sleep(500);
		assertTrue(updateExecutor.getNotCommitedDocuments() < 10);
		updateExecutor.stop();
	}
}
