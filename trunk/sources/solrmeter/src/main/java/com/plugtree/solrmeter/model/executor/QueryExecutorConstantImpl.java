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

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.model.FieldExtractor;
import com.plugtree.solrmeter.model.QueryExecutor;
import com.plugtree.solrmeter.model.QueryExtractor;
import com.plugtree.solrmeter.model.QueryStatistic;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.SolrServerRegistry;
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.operation.ConstantOperationExecutorThread;
import com.plugtree.solrmeter.model.operation.QueryOperation;

/**
 * This query executor calculates the interval between queries to achieve
 * the specified number of queries per minute and tries to execute them in
 * constant time.
 * @see com.plugtree.solrmeter.model.operation.ConstantOperationExecutorThread
 * @author tflobbe
 *
 */
@StressTestScope
public class QueryExecutorConstantImpl extends AbstractExecutor implements QueryExecutor{
	
	/**
	 * Solr Server for strings
	 */
	private CommonsHttpSolrServer server;
	
	/**
	 * List of Statistics observing this Executor.
	 */
	private List<QueryStatistic> statistics;
	
	/**
	 * Query Type of all executed Queries
	 */
	private String queryType;
	

	
	/**
	 * The facet fields extractor
	 */
	private FieldExtractor facetFieldExtractor;
	
	/**
	 * The filter query extractor
	 */
	private QueryExtractor filterQueryExtractor;
	
	/**
	 * The standard query extractor
	 */
	private QueryExtractor queryExtractor;
	
	private QueryExtractor extraParamExtractor;
	
	/**
	 * Indicates wether the Executor is running or not
	 */
	private boolean running;
	
	private int operationsPerMinute;
	
	/**
	 * Thread that execute queries periodically
	 */
	private ConstantOperationExecutorThread executerThread;
	
	@Inject
	public QueryExecutorConstantImpl(FieldExtractor facetFieldExtractor,
			@Named("filterQueryExtractor") QueryExtractor filterQueryExtractor,
			@Named("queryExtractor") QueryExtractor queryExtractor,
			@Named("extraParamExtractor")QueryExtractor extraParamExtractor) {
		super();
		statistics = new LinkedList<QueryStatistic>();
		this.filterQueryExtractor = filterQueryExtractor;
		this.facetFieldExtractor = facetFieldExtractor;
		this.queryExtractor = queryExtractor;
		this.queryType = SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERY_TYPE);
		this.operationsPerMinute = Integer.valueOf(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERIES_PER_MINUTE)).intValue();
		this.extraParamExtractor = extraParamExtractor;
		this.loadExtraParameters(SolrMeterConfiguration.getProperty("solr.query.extraParameters", ""));
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
	public String getQueryType() {
		return queryType;
	}

	@Override
	public synchronized CommonsHttpSolrServer getSolrServer() {
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
		executerThread = new ConstantOperationExecutorThread(new QueryOperation(this, queryExtractor, filterQueryExtractor, facetFieldExtractor, extraParamExtractor));
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
