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

import org.apache.solr.client.solrj.SolrClient;
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

	private SolrClient server;

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
		operationsPerSecond = Integer.parseInt(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.UPDATES_PER_SECOND));
		autocommit = Boolean.valueOf(SolrMeterConfiguration.getProperty("solr.update.solrAutocommit", "false"));;
		maxTimeBeforeCommit = Integer.valueOf(SolrMeterConfiguration.getProperty("solr.update.timeToCommit", "10000"));
		numberOfDocumentsBeforeCommit = Integer.valueOf(SolrMeterConfiguration.getProperty("solr.update.documentsToCommit", "100"));
		super.prepare();
	}

	public UpdateExecutorRandomImpl() {
		super();
		statistics = new LinkedList<UpdateStatistic>();
	}

	@Override
	public synchronized SolrClient getSolrServer() {
		if(server == null) {
			server = super.getSolrServer(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.SOLR_ADD_URL));
		}
		return server;
	}

	@Override
	protected RandomOperationExecutorThread createThread() {
		return new RandomOperationExecutorThread(new UpdateOperation(this, documentExtractor), 1000);
	}

	private void prepareCommitter() {
		if(commiterThread != null) {
			commiterThread.destroy();
		}
		commiterThread = new ConstantOperationExecutorThread(new CommitOperation(this));
		commiterThread.setTimeToWait(maxTimeBeforeCommit);
	}

	@Override
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

	@Override
	public void stop() {
		if(!this.isRunning()) {
			return;
		}
		if(!isAutocommit()) {
			commiterThread.destroy();
		}
		super.stop();
	}

	@Override
	protected void stopStatistics() {
		for(UpdateStatistic statistic:statistics) {
			statistic.onFinishedTest();
		}
	}

	@Override
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

	@Override
	public void notifyCommitSuccessfull(UpdateResponse response) {
		notCommitedDocuments = 0;
		for(UpdateStatistic statistic:statistics) {
			statistic.onCommit(response);
		}
	}

	@Override
	public void notifyCommitError(CommitException exception) {
		for(UpdateStatistic statistic:statistics) {
			statistic.onCommitError(exception);
		}
	}

	@Override
	public void notifyUpdateError(UpdateException updateException) {
		for(UpdateStatistic statistic:statistics) {
			statistic.onAddError(updateException);
		}
	}

	@Override
	public void addStatistic(UpdateStatistic statistic) {
		this.statistics.add(statistic);
	}

	@Override
	public int getNotCommitedDocuments() {
		return notCommitedDocuments;
	}

	@Override
	public void setNumberOfDocumentsBeforeCommit(int value) {
		if (value == Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Number of documents before commit can't be more than " + Integer.MAX_VALUE);
		}
		if (value < 0) {
			throw new IllegalArgumentException("Number of documents before commit can't be less than 0");
		}
		numberOfDocumentsBeforeCommit= value;
		SolrMeterConfiguration.setProperty("solr.update.documentsToCommit", String.valueOf(numberOfDocumentsBeforeCommit));
	}

	@Override
	public Integer getNumberOfDocumentsBeforeCommit() {
		return numberOfDocumentsBeforeCommit;
	}

	@Override
	public void setMaxTimeBeforeCommit(Integer value) {
		if(value <= 0) {
			throw new RuntimeException("Time before commit can't be 0");
		}
		maxTimeBeforeCommit = value;
		SolrMeterConfiguration.setProperty("solr.update.timeToCommit", String.valueOf(maxTimeBeforeCommit));
		commiterThread.setTimeToWait(value);
	}

	@Override
	public Integer getMaxTimeBeforeCommit() {
		return maxTimeBeforeCommit;
	}

	@Override
	public boolean isAutocommit() {
		return autocommit;
	}

	@Override
	public Integer getUpdatesPerMinute() {
		return this.operationsPerSecond;
	}

	@Override
	protected String getOperationsPerSecondConfigurationKey() {
		return "solr.load.updatespersecond";
	}

}
