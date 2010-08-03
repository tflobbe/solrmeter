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
package com.linebee.solrmeter.model.executor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.linebee.solrmeter.model.AbstractExecutor;
import com.linebee.solrmeter.model.FieldExtractor;
import com.linebee.solrmeter.model.QueryExecutor;
import com.linebee.solrmeter.model.QueryExtractor;
import com.linebee.solrmeter.model.QueryStatistic;
import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.model.exception.QueryException;
import com.linebee.solrmeter.model.task.QueryOperation;
import com.linebee.solrmeter.model.task.RandomOperationExecutorThread;
import com.linebee.stressTestScope.StressTestScope;

/**
 * Creates and manages query execution Threads.
 * @author tflobbe
 *
 */
@StressTestScope
public class QueryExecutorRandomImpl extends AbstractExecutor implements QueryExecutor {
	
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
			FieldExtractor facetFieldExtractor) {
		super();
		statistics = new LinkedList<QueryStatistic>();
		this.queryExtractor = queryExtractor;
		this.filterQueryExtractor = filterQueryExtractor;
		this.facetFieldExtractor = facetFieldExtractor;
		this.operationsPerMinute = Integer.valueOf(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERIES_PER_MINUTE)).intValue();
		this.queryType = SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERY_TYPE, "standard");
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
		return new RandomOperationExecutorThread(new QueryOperation(this), 60);
	}

	/**
	 * Logs strings time and all statistics information.
	 */
	protected void stopStatistics() {
		for(QueryStatistic statistic:statistics) {
			statistic.onFinishedTest();
		}
	}

	/* (non-Javadoc)
	 * @see com.linebee.solrmeter.model.QueryExecutor#getSolrServer()
	 */
	public synchronized CommonsHttpSolrServer getSolrServer() {
		if(server == null) {
			server = super.getSolrServer(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.SOLR_SEARCH_URL));
		}
		return server;
	}

	/* (non-Javadoc)
	 * @see com.linebee.solrmeter.model.QueryExecutor#getRandomQuery()
	 */
	public String getRandomQuery() {
		return queryExtractor.getRandomQuery();
	}
	
	/* (non-Javadoc)
	 * @see com.linebee.solrmeter.model.QueryExecutor#getRandomFilterQuery()
	 */
	public String getRandomFilterQuery() {
		return filterQueryExtractor.getRandomQuery();
	}
	
	/* (non-Javadoc)
	 * @see com.linebee.solrmeter.model.QueryExecutor#notifyQueryExecuted(org.apache.solr.client.solrj.response.QueryResponse, long)
	 */
	public void notifyQueryExecuted(QueryResponse response, long clientTime) {
		for(QueryStatistic statistic:statistics) {
			statistic.onExecutedQuery(response, clientTime);
		}
	}

	/* (non-Javadoc)
	 * @see com.linebee.solrmeter.model.QueryExecutor#notifyError(com.linebee.solrmeter.model.exception.QueryException)
	 */
	public void notifyError(QueryException exception) {
		for(QueryStatistic statistic:statistics) {
			statistic.onQueryError(exception);
		}
	}

	/* (non-Javadoc)
	 * @see com.linebee.solrmeter.model.QueryExecutor#getRandomField()
	 */
	public String getRandomField() {
		return facetFieldExtractor.getRandomFacetField();
	}
	
	/* (non-Javadoc)
	 * @see com.linebee.solrmeter.model.QueryExecutor#getQueryType()
	 */
	public String getQueryType() {
		return queryType;
	}
	
	@Override
	protected String getOperationsPerMinuteConfigurationKey() {
		return "solr.load.queriesperminute";
	}
	
	/* (non-Javadoc)
	 * @see com.linebee.solrmeter.model.QueryExecutor#addStatistic(com.linebee.solrmeter.model.QueryStatistic)
	 */
	public void addStatistic(QueryStatistic statistic) {
		this.statistics.add(statistic);
	}

	/* (non-Javadoc)
	 * @see com.linebee.solrmeter.model.QueryExecutor#getQueriesPerMinute()
	 */
	public int getQueriesPerMinute() {
		return operationsPerMinute;
	}

	/* (non-Javadoc)
	 * @see com.linebee.solrmeter.model.QueryExecutor#getExtraParameters()
	 */
	public Map<String, String> getExtraParameters() {
		return extraParameters;
	}

}
