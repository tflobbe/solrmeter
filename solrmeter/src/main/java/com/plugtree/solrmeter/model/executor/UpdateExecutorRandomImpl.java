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
package com.plugtree.solrmeter.model.executor;

import java.util.LinkedList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.plugtree.solrmeter.model.InputDocumentExtractor;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.UpdateExecutor;
import com.plugtree.solrmeter.model.UpdateStatistic;
import com.plugtree.solrmeter.model.exception.CommitException;
import com.plugtree.solrmeter.model.exception.UpdateException;
import com.plugtree.solrmeter.model.operation.CommitOperation;
import com.plugtree.solrmeter.model.operation.ConstantOperationExecutorThread;
import com.plugtree.solrmeter.model.operation.RandomOperationExecutorThread;
import com.plugtree.solrmeter.model.operation.UpdateOperation;
import com.plugtree.stressTestScope.StressTestScope;

/** 
 * manages update execution Threads. The updates are executed with 
 * RandomOperationExectionThread.
 * @see com.plugtree.solrmeter.model.operation.RandomOperationExecutorThread
 * @author tflobbe
 *
 */
@StressTestScope
public class UpdateExecutorRandomImpl extends AbstractRandomExecutor implements UpdateExecutor {
	
	//TODO DI
	private SolrServer server;
	
	private Integer numberOfDocumentsBeforeCommit;
	
	private Integer maxTimeBeforeCommit;
	
	private List<UpdateStatistic> statistics;
	
	protected boolean autocommit;
	
	private int notCommitedDocuments;
	
	private ConstantOperationExecutorThread commiterThread;
	
	private InputDocumentExtractor documentExtractor;
	
	@Inject
	public UpdateExecutorRandomImpl(@Named("updateExtractor") InputDocumentExtractor documentExtractor) {
		super();
		this.documentExtractor = documentExtractor;
		statistics = new LinkedList<UpdateStatistic>();
		operationsPerMinute = Integer.valueOf(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.UPDATES_PER_MINUTE)).intValue();
		autocommit = Boolean.valueOf(SolrMeterConfiguration.getProperty("solr.update.solrAutocommit", "false"));;
		maxTimeBeforeCommit = Integer.valueOf(SolrMeterConfiguration.getProperty("solr.update.timeToCommit", "10000"));
		numberOfDocumentsBeforeCommit = Integer.valueOf(SolrMeterConfiguration.getProperty("solr.update.documentsToCommit", "100"));
		super.prepare();
	}

	public UpdateExecutorRandomImpl() {
		super();
		statistics = new LinkedList<UpdateStatistic>();
	}
	
	public synchronized SolrServer getSolrServer() {
		if(server == null) {
			server = super.getSolrServer(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.SOLR_ADD_URL));
		}
		return server;
	}
	
	protected RandomOperationExecutorThread createThread() {
		return new RandomOperationExecutorThread(new UpdateOperation(this, documentExtractor), 60);
	}

	private void prepareCommitter() {
		if(commiterThread != null) {
			commiterThread.destroy();
		}
		commiterThread = new ConstantOperationExecutorThread(new CommitOperation(this));
		commiterThread.setTimeToWait(maxTimeBeforeCommit);
	}
	
	public void start() {
		if(this.isRunning()) {
			return;
		}
		super.start();
		if(!isAutocommit()) {
			prepareCommitter();
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
		notCommitedDocuments = 0;
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
		commiterThread.setTimeToWait(value);
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
