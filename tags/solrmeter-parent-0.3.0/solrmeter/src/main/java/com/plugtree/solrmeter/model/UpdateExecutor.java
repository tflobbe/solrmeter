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
package com.plugtree.solrmeter.model;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;

import com.plugtree.solrmeter.model.exception.CommitException;
import com.plugtree.solrmeter.model.exception.UpdateException;
/**
 * Interface that all Update Executors must implement.
 * @author tflobbe
 *
 */
public interface UpdateExecutor {

	/**
	 * 
	 * @return The current Solr Server. If there is no current Solr Server, then the method returns a new one.
	 */
	public SolrServer getSolrServer();

	/**
	 * Starts this executor
	 */
	public void start();

	/**
	 * Stops this executor.
	 */
	public void stop();

	/**
	 * To be executed when an Update succeeds. 
	 * @param response
	 */
	public void notifyAddedDocument(UpdateResponse response);

	/**
	 * To be executed when a Commit succeeds. 
	 * @param response
	 */
	public void notifyCommitSuccessfull(UpdateResponse response);

	/**
	 * To be executed when an error ocurrs when committing.
	 * @param exception
	 */
	public void notifyCommitError(CommitException exception);

	/**
	 * To be executed when an error ocurrs when updating.
	 * @param updateException
	 */
	public void notifyUpdateError(UpdateException updateException);

	/**
	 * Adds a Statistic Observer to the executor
	 * @param statistic
	 */
	public void addStatistic(UpdateStatistic statistic);

	/**
	 * @return The number of added documents that hasn't been committed by solrmeter.
	 * (if a commit is performed outside solrmeter, this counter wont notice)
	 */
	public int getNotCommitedDocuments();

	/**
	 * Increment the number of documents that has to be added before a commit is performed
	 * by solrmeter. This number is useless when solrmeter doesn't perform commits.
	 */
	public void incrementNumberOfDocumentsBeforeCommit();

	/**
	 * Decrement the number of documents that has to be added before a commit is performed
	 * by solrmeter. This number is useless when solrmeter doesn't perform commits.
	 */
	public void decrementNumberOfDocumentsBeforeCommit();

	/**
	 * 
	 * @return The number of documents that has to be added before a commit is performed by
	 * solrmeter.
	 */
	public Integer getNumberOfDocumentsBeforeCommit();

	/**
	 * Sets the time interval between commits executed by solrmeter. This number is useless
	 * when solrmeter doesn't perform commits.
	 * @param value
	 */
	public void setMaxTimeBeforeCommit(Integer value);

	/**
	 * 
	 * @return The time interval between commits executed by solrmeter.
	 */
	public Integer getMaxTimeBeforeCommit();

	/**
	 * If autocommit is set to true, this means that Solr is performing autocommit, and solrmeter
	 * doesn't have to. WHEN SET TO TRUE, SOLRMETER WONT PERFORM COMMITS.
	 * @return
	 */
	public boolean isAutocommit();

	/**
	 * 
	 * @return The number of update operations that has to be executed per minute
	 */
	public Integer getUpdatesPerMinute();

	/**
	 * 
	 * @return true if the executor is currently running.
	 * 			false if the executor is not currently running.
	 */
	public boolean isRunning();

	/**
	 * Increment in one the number of updates that has to be executed in a minute.
	 */
	public void incrementOperationsPerMinute();

	/**
	 * Decrement in one the number of updates that has to be executed in a minute.
	 * It can't be 0 or less.
	 */
	public void decrementOperationsPerMinute();

}