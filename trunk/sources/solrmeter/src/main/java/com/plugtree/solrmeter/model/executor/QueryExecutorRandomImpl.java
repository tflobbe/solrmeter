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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.operation.QueryOperation;
import com.plugtree.solrmeter.model.operation.RandomOperationExecutorThread;

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
	private CommonsHttpSolrServer server;
	
	/**
	 * Query Type of all executed Queries
	 */
	private String queryType;
	
	/**
	 * List of Statistics observing this Executor.
	 */
	private List<QueryStatistic> statistics;
	
	/**
	 * The standard query extractor
	 */
	private QueryExtractor queryExtractor;
	
	/**
	 * The filter query extractor
	 */
	private QueryExtractor filterQueryExtractor;
	
	/**
	 * The extra parameters query extractor
	 */
	private QueryExtractor extraParamExtractor;
	
	/**
	 * The facet fields extractor
	 */
	private FieldExtractor facetFieldExtractor;
	
	/**
	 * Extra parameters specified to the query
	 */
	private Map<String, String> extraParameters;
	
	@Inject
	public QueryExecutorRandomImpl(
			@Named("queryExtractor") QueryExtractor queryExtractor,
			@Named("filterQueryExtractor") QueryExtractor filterQueryExtractor,
			FieldExtractor facetFieldExtractor, 
			@Named("extraParamExtractor")QueryExtractor extraParamExtractor) {
		super();
		statistics = new LinkedList<QueryStatistic>();
		this.queryExtractor = queryExtractor;
		this.filterQueryExtractor = filterQueryExtractor;
		this.facetFieldExtractor = facetFieldExtractor;
		this.extraParamExtractor= extraParamExtractor;
		this.operationsPerMinute = Integer.valueOf(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERIES_PER_MINUTE)).intValue();
		this.queryType = SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERY_TYPE);
		this.loadExtraParameters(SolrMeterConfiguration.getProperty("solr.query.extraParameters", ""));
		super.prepare();
	}

	public QueryExecutorRandomImpl() {
		super();
		statistics = new LinkedList<QueryStatistic>();
//		operationsPerMinute = Integer.valueOf(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERIES_PER_MINUTE)).intValue();
	}
	
	protected void loadExtraParameters(String property) {
		extraParameters = new HashMap<String, String>();
		if(property == null || "".equals(property.trim())) {
			return;
		}
		for(String param:property.split(",")) {
			int equalSignIndex = param.indexOf("=");
			if(equalSignIndex > 0) {
				extraParameters.put(param.substring(0, equalSignIndex).trim(), param.substring(equalSignIndex + 1).trim());
			}
		}
	}

	@Override
	protected RandomOperationExecutorThread createThread() {
		return new RandomOperationExecutorThread(new QueryOperation(this, queryExtractor, filterQueryExtractor, facetFieldExtractor, extraParamExtractor), 60);
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
	public synchronized CommonsHttpSolrServer getSolrServer() {
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
	public String getQueryType() {
		return queryType;
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

	@Override
	public Map<String, String> getExtraParameters() {
		return extraParameters;
	}

}
