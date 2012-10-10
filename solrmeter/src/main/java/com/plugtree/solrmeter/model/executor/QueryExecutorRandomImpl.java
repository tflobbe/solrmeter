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
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.generator.QueryGenerator;
import com.plugtree.solrmeter.model.operation.QueryOperation;
import com.plugtree.solrmeter.model.operation.RandomOperationExecutorThread;
import com.plugtree.stressTestScope.StressTestScope;

/**
 * Creates and manages query execution Threads. The queries are executed with 
 * RandomOperationExectionThread.
 * @see com.plugtree.solrmeter.model.operation.RandomOperationExecutorThread
 * @author tflobbe
 *
 */
@StressTestScope
public class QueryExecutorRandomImpl extends AbstractRandomExecutor implements QueryExecutor {
	
	/**
	 * Solr Server for strings
	 * TODO implement provider
	 */
	private SolrServer server;
	
	/**
	 * List of Statistics observing this Executor.
	 */
	private List<QueryStatistic> statistics;
	
    /**
     * The generator that creates a query depending on the query mode selected
     */
    private QueryGenerator queryGenerator;

    @Inject
	public QueryExecutorRandomImpl(@Named("queryGenerator") QueryGenerator queryGenerator) {
		super();
        this.queryGenerator = queryGenerator;
		statistics = new LinkedList<QueryStatistic>();
		this.operationsPerMinute = Integer.valueOf(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERIES_PER_MINUTE)).intValue();
		super.prepare();
	}



	public QueryExecutorRandomImpl() {
		super();
		statistics = new LinkedList<QueryStatistic>();
//		operationsPerMinute = Integer.valueOf(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERIES_PER_MINUTE)).intValue();
	}
	
	

	@Override
	protected RandomOperationExecutorThread createThread() {
		return new RandomOperationExecutorThread(new QueryOperation(this, queryGenerator), 60);
	}

	/**
	 * Logs strings time and all statistics information.
	 */
	@Override
	protected void stopStatistics() {
		for(QueryStatistic statistic:statistics) {
			statistic.onFinishedTest();
		}
	}

	@Override
	public synchronized SolrServer getSolrServer() {
		if(server == null) {
			server = super.getSolrServer(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.SOLR_SEARCH_URL));
		}
		return server;
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
	protected String getOperationsPerMinuteConfigurationKey() {
		return "solr.load.queriesperminute";
	}
	
	@Override
	public void addStatistic(QueryStatistic statistic) {
		this.statistics.add(statistic);
	}

	@Override
	public int getQueriesPerMinute() {
		return operationsPerMinute;
	}

}
