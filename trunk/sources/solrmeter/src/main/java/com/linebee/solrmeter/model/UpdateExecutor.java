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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.linebee.solrmeter.model.exception.CommitException;
import com.linebee.solrmeter.model.exception.UpdateException;
import com.linebee.solrmeter.model.task.RandomOperationExecutorThread;
import com.linebee.solrmeter.model.task.UpdateOperation;
import com.linebee.stressTestScope.StressTestScope;

/** 
 * manages update execution Threads.
 * @author tflobbe
 *
 */
@StressTestScope
public class UpdateExecutor extends AbstractExecutor {
	
	//TODO DI
	private CommonsHttpSolrServer server;
	
	private Integer numberOfDocumentsBeforeCommit;
	
	private Integer maxTimeBeforeCommit;
	
	private List<UpdateStatistic> statistics;
	
	protected boolean autocommit;
	
	private int notCommitedDocuments;
	
	private CommiterThread commiterThread;
	
	private InputDocumentExtractor documentExtractor;
	
	@Inject
	public UpdateExecutor(@Named("updateExtractor") InputDocumentExtractor documentExtractor) {
		super();
		this.documentExtractor = documentExtractor;
		statistics = new LinkedList<UpdateStatistic>();
		operationsPerMinute = Integer.valueOf(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.UPDATES_PER_MINUTE)).intValue();
		autocommit = Boolean.valueOf(SolrMeterConfiguration.getProperty("solr.update.solrAutocommit", "false"));;
		maxTimeBeforeCommit = Integer.valueOf(SolrMeterConfiguration.getProperty("solr.update.timeToCommit", "10000"));
		numberOfDocumentsBeforeCommit = Integer.valueOf(SolrMeterConfiguration.getProperty("solr.update.documentsToCommit", "100"));
		super.prepare();
	}

	public UpdateExecutor() {
		super();
		statistics = new LinkedList<UpdateStatistic>();
	}
	
	public synchronized CommonsHttpSolrServer getSolrServer() {
		if(server == null) {
			server = super.getSolrServer(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.SOLR_ADD_URL));
		}
		return server;
	}
	
	protected RandomOperationExecutorThread createThread() {
		return new RandomOperationExecutorThread(new UpdateOperation(this), 60);
	}

	private void prepareCommiter() {
		if(commiterThread != null) {
			commiterThread.destroy();
		}
		commiterThread = new CommiterThread();
	}

	public void start() {
		if(this.isRunning()) {
			return;
		}
		super.start();
		if(!isAutocommit()) {
			prepareCommiter();
			commiterThread.start();
		}
		logger.info("Update Executor started");
	}
	
	public void stop() {
		if(!this.isRunning()) {
			return;
		}
		if(!isAutocommit()) {
			commiterThread.destroy();
		}
		super.stop();
	}

	protected void stopStatistics() {
		for(UpdateStatistic statistic:statistics) {
			statistic.onFinishedTest();
		}
	}

	public SolrInputDocument getNextDocument() {
		return documentExtractor.getRandomDocument();
	}

	public void notifyAddedDocument(UpdateResponse response) {
		for(UpdateStatistic statistic:statistics) {
			statistic.onAddedDocument(response);
		}
		if(!isAutocommit()) {
			notCommitedDocuments++;//synchronize this?
			if(notCommitedDocuments >= numberOfDocumentsBeforeCommit) {
				logger.debug("Not Commited Docs is " + notCommitedDocuments + ". Waking commiter thread.");
				commiterThread.wake();
			}
		}
	}
	
	public void notifyCommitSuccessfull(UpdateResponse response) {
		for(UpdateStatistic statistic:statistics) {
			statistic.onCommit(response);
		}
	}
	
	public void notifyCommitError(CommitException exception) {
		for(UpdateStatistic statistic:statistics) {
			statistic.onCommitError(exception);
		}
	}
	
	public void notifyUpdateError(UpdateException updateException) {
		for(UpdateStatistic statistic:statistics) {
			statistic.onAddError(updateException);
		}
	}
	
	public void addStatistic(UpdateStatistic statistic) {
		this.statistics.add(statistic);
	}
	
	public class CommiterThread extends Thread {
		
		private boolean stopping = false;
		
		@Override
		public synchronized void run() {
			while(!stopping) {
				try {
					this.wait(new Long(maxTimeBeforeCommit));
					if(!stopping) {
						performCommit();
					}
				} catch (InterruptedException e) {
					Logger.getLogger(this.getClass()).error("Error on commiter thread", e);
					throw new RuntimeException(e);
				}
			}
		}
		
		private void performCommit() {
			Logger.getLogger(this.getClass()).info("commiting");
			try {
				UpdateResponse response = getSolrServer().commit();
				Logger.getLogger(this.getClass()).info("Commit OK");
				notifyCommitSuccessfull(response);
			} catch (SolrServerException e) {
				Logger.getLogger(this.getClass()).error("Error on commiter thread", e);
				notifyCommitError(new CommitException(e));
			} catch (IOException e) {
				Logger.getLogger(this.getClass()).error("Error on commiter thread", e);
				notifyCommitError(new CommitException(e));
			}
			notCommitedDocuments = 0;
			
		}
		
		public void destroy() {
			this.stopping = true;
		}
		
		public synchronized void wake() {
			this.notify();
		}
	}

	public int getNotCommitedDocuments() {
		return notCommitedDocuments;
	}
	
	public void incrementNumberOfDocumentsBeforeCommit() {
		if(numberOfDocumentsBeforeCommit == Integer.MAX_VALUE) {
			throw new RuntimeException("Number of documents before commit can't be more than " + Integer.MAX_VALUE);
		}
		numberOfDocumentsBeforeCommit+= 1;
		SolrMeterConfiguration.setProperty("solr.update.documentsToCommit", String.valueOf(numberOfDocumentsBeforeCommit));
	}
	
	public void decrementNumberOfDocumentsBeforeCommit() {
		if(numberOfDocumentsBeforeCommit <= 1) {
			throw new RuntimeException("Number of documents before commit can't be 0");
		}
		numberOfDocumentsBeforeCommit-= 1;
		SolrMeterConfiguration.setProperty("solr.update.documentsToCommit", String.valueOf(numberOfDocumentsBeforeCommit));
	}

	public Integer getNumberOfDocumentsBeforeCommit() {
		return numberOfDocumentsBeforeCommit;
	}
	
	public void setMaxTimeBeforeCommit(Integer value) {
		if(value <= 0) {
			throw new RuntimeException("Time before commit can't be 0");
		}
		maxTimeBeforeCommit = value;
		SolrMeterConfiguration.setProperty("solr.update.timeToCommit", String.valueOf(maxTimeBeforeCommit));
	}

	public Integer getMaxTimeBeforeCommit() {
		return maxTimeBeforeCommit;
	}

	public boolean isAutocommit() {
		return autocommit;
	}

	public Integer getUpdatesPerMinute() {
		return this.operationsPerMinute;
	}

	@Override
	protected String getOperationsPerMinuteConfigurationKey() {
		return "solr.load.updatesperminute";
	}

}
