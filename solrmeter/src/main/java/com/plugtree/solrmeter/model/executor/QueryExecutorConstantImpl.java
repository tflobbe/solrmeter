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
import org.apache.solr.client.solrj.response.QueryResponse;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.plugtree.solrmeter.model.QueryExecutor;
import com.plugtree.solrmeter.model.QueryStatistic;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.SolrServerRegistry;
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.generator.QueryGenerator;
import com.plugtree.solrmeter.model.operation.ConstantOperationExecutorThread;
import com.plugtree.solrmeter.model.operation.QueryOperation;
import com.plugtree.stressTestScope.StressTestScope;

/**
 * This query executor calculates the interval between queries to achieve
 * the specified number of queries per minute and tries to execute them in
 * constant time.
 * @see com.plugtree.solrmeter.model.operation.ConstantOperationExecutorThread
 * @author tflobbe
 *
 */
@StressTestScope
public class QueryExecutorConstantImpl implements QueryExecutor{
	
	/**
	 * Solr Server for strings
	 */
	private SolrServer server;
	
	/**
	 * List of Statistics observing this Executor.
	 */
	private List<QueryStatistic> statistics;
	
	/**
	 * Indicates wether the Executor is running or not
	 */
	private boolean running;
	
	private int operationsPerMinute;
	
	/**
	 * Thread that execute queries periodically
	 */
	private ConstantOperationExecutorThread executerThread;

    /**
     * The generator that creates a query depending on the query mode selected 
     */
    private QueryGenerator queryGenerator;

    @Inject
	public QueryExecutorConstantImpl(@Named("queryGenerator") QueryGenerator queryGenerator) {
		super();
        this.queryGenerator = queryGenerator;
        statistics = new LinkedList<QueryStatistic>();
		this.operationsPerMinute = Integer.valueOf(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERIES_PER_MINUTE)).intValue();
	}



	@Override
	public void decrementOperationsPerMinute() {
		operationsPerMinute--;
		updateThreadWaitTime();
	}

	@Override
	public int getQueriesPerMinute() {
		return operationsPerMinute;
	}

	@Override
	public synchronized SolrServer getSolrServer() {
		if(server == null) {
			server = SolrServerRegistry.getSolrServer(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.SOLR_SEARCH_URL));
		}
		return server;
	}

	@Override
	public void incrementOperationsPerMinute() {
		operationsPerMinute++;
		updateThreadWaitTime();
	}
	
	private void updateThreadWaitTime() {
		if(executerThread != null) {
			executerThread.setTimeToWait(60000/operationsPerMinute);
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}
	
	@Override
	public void prepare() {

	}

	@Override
	public void start() {
		running = true;
		executerThread = new ConstantOperationExecutorThread(new QueryOperation(this, queryGenerator));
		this.updateThreadWaitTime();
		executerThread.start();
	}

	@Override
	public void stop() {
		running = false;
		executerThread.destroy();
		this.stopStatistics();
	}
	
	/**
	 * Logs strings time and all statistics information.
	 */
	protected void stopStatistics() {
		for(QueryStatistic statistic:statistics) {
			statistic.onFinishedTest();
		}
	}

	@Override
	public void notifyQueryExecuted(QueryResponse response, long clientTime) {
		for(QueryStatistic statistic:statistics) {
			statistic.onExecutedQuery(response, clientTime);
		}
	}

	@Override
	public void notifyError(QueryException exception) {
		for(QueryStatistic statistic:statistics) {
			statistic.onQueryError(exception);
		}
	}
	
	@Override
	public void addStatistic(QueryStatistic statistic) {
		this.statistics.add(statistic);
	}

}
