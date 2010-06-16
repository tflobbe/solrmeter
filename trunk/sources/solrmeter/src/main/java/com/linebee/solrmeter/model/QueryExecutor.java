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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.linebee.solrmeter.model.exception.QueryException;
import com.linebee.solrmeter.model.extractor.FileQueryExtractor;
import com.linebee.solrmeter.model.task.AbstractOperationThread;
import com.linebee.solrmeter.model.task.QueryThread;

/**
 * Creates and manages query execution Threads.
 * @author Tomas
 *
 */
public class QueryExecutor extends AbstractExecutor {
	
	/**
	 * List of existing Fields. These fields are used to add fecets to queries.
	 */
	private List<String> fields;
	
	/**
	 * Solr Server for queries
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
	 * Extra parameters specified to the query
	 */
	private Map<String, String> extraParameters;
	
	public QueryExecutor() {
		super();
		statistics = new LinkedList<QueryStatistic>();
//		operationsPerMinute = Integer.valueOf(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERIES_PER_MINUTE)).intValue();
	}
	
	/**
	 * Prepares the executor to run.
	 */
	public void prepare() {
		operationsPerMinute = Integer.valueOf(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERIES_PER_MINUTE)).intValue();
		queryType = SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERY_TYPE, "standard");
		queryExtractor = new FileQueryExtractor(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERIES_FILE_PATH));
		filterQueryExtractor = new FileQueryExtractor(SolrMeterConfiguration.getProperty("solr.query.filterQueriesFile"));
		loadExtraParameters(SolrMeterConfiguration.getProperty("solr.query.extraParameters", ""));
		loadFields();
		prepareStatistics();
		super.prepare();
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
	protected AbstractOperationThread createThread() {
		return new QueryThread(this, 60);
	}
	/**
	 * Prepares al observer statistics
	 */
	private void prepareStatistics() {
		for(QueryStatistic statistic:statistics) {
			statistic.prepare();
		}
	}

	/**
	 * Loads all fields from the fields file and add every one to the "fields" list.
	 */
	private void loadFields() {
		fields = loadStringsFromFile(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.FIELDS_FILE_PATH));
	}

	/**
	 * Logs queries time and all statistics information.
	 */
	protected void stopStatistics() {
		for(QueryStatistic statistic:statistics) {
			statistic.onFinishedTest();
		}
	}

	/**
	 * 
	 * @return The current Solr Server. If there is no current Solr Server, then the method returns a new one.
	 */
	public synchronized CommonsHttpSolrServer getSolrServer() {
		if(server == null) {
			server = super.getSolrServer(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.SOLR_SEARCH_URL));
		}
		return server;
	}

	/**
	 * @return returns a random Query of the existing ones.
	 */
	public String getRandomQuery() {
		return queryExtractor.getRandomQuery();
	}
	
	/**
	 * @return returns a random filter Query of the existing ones.
	 */
	public String getRandomFilterQuery() {
		return filterQueryExtractor.getRandomQuery();
	}
	
	/**
	 * To be executed when a Query succeeds. 
	 * @param response
	 */
	public void notifyQueryExecuted(QueryResponse response, long clientTime) {
		for(QueryStatistic statistic:statistics) {
			statistic.onExecutedQuery(response, clientTime);
		}
	}

	/**
	 * To be executed when a query fails
	 * @param exception
	 */
	public void notifyError(QueryException exception) {
		for(QueryStatistic statistic:statistics) {
			statistic.onQueryError(exception);
		}
	}

	/**
	 * 
	 * @return returns a random Field of the existing ones.
	 */
	public String getRandomField() {
		return (String) this.getNextRandomObject(fields);
	}
	
	/**
	 * @return Query type
	 */
	public String getQueryType() {
		return queryType;
	}
	
	@Override
	protected String getOperationsPerMinuteConfigurationKey() {
		return "solr.load.queriesperminute";
	}
	
	/**
	 * Adds a Statistic Observer to the executor
	 * @param statistic
	 */
	public void addStatistic(QueryStatistic statistic) {
		this.statistics.add(statistic);
	}

	public int getQueriesPerMinute() {
		return operationsPerMinute;
	}

	public Map<String, String> getExtraParameters() {
		return extraParameters;
	}

}
