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

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.plugtree.solrmeter.model.exception.QueryException;
/**
 * Interface that all query executors must implement.
 * @author tflobbe
 *
 */
public interface QueryExecutor {

	/**
	 *
	 * @return The current Solr Server. If there is no current Solr Server, then the method returns a new one.
	 */
	SolrClient getSolrServer();

	/**
	 * To be executed when a Query succeeds. 
	 * @param response
	 */
	void notifyQueryExecuted(QueryResponse response,
							 long clientTime);

	/**
	 * To be executed when a query fails
	 * @param exception
	 */
	void notifyError(QueryException exception);

	/**
	 * Adds a Statistic Observer to the executor
	 * @param statistic
	 */
	void addStatistic(QueryStatistic statistic);

	/**
	 * Returns the number of queries to be executed every minute
	 * @return
	 */
	int getQueriesPerSecond();

	/**
	 * Prepares this executor to run
	 */
	void prepare();

	/**
	 * Starts this executor
	 */
	void start();

	/**
	 * Stops this executor
	 */
	void stop();

	/**
	 * Set the number of operations expected per second
	 */
	void setOperationsPerSecond(int newOperationsPerSecond);

	/**
	 * Determines whether this executor is running.
	 * @return
	 */
	boolean isRunning();

}