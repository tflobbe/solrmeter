package com.linebee.solrmeter.model.executor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.linebee.solrmeter.model.FieldExtractor;
import com.linebee.solrmeter.model.QueryExecutor;
import com.linebee.solrmeter.model.QueryExtractor;
import com.linebee.solrmeter.model.QueryStatistic;
import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.model.SolrServerRegistry;
import com.linebee.solrmeter.model.exception.QueryException;
import com.linebee.solrmeter.model.task.ConstantOperationExecutorThread;
import com.linebee.solrmeter.model.task.QueryOperation;
import com.linebee.stressTestScope.StressTestScope;

/**
 * This query executor calculates the interval between queries to achieve
 * the specified number of queries per minute and tries to execute them in
 * constant time.
 * @author tflobbe
 *
 */
@StressTestScope
public class QueryExecutorConstantImpl implements QueryExecutor {
	
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
	 * Extra parameters specified to the query
	 */
	private Map<String, String> extraParameters;
	
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
			@Named("queryExtractor") QueryExtractor queryExtractor) {
		super();
		statistics = new LinkedList<QueryStatistic>();
		this.filterQueryExtractor = filterQueryExtractor;
		this.facetFieldExtractor = facetFieldExtractor;
		this.queryExtractor = queryExtractor;
		this.queryType = SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERY_TYPE, "standard");
		this.operationsPerMinute = Integer.valueOf(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERIES_PER_MINUTE)).intValue();
		this.loadExtraParameters(SolrMeterConfiguration.getProperty("solr.query.extraParameters", ""));
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
	public void decrementConcurrentQueries() {
		operationsPerMinute--;
		updateThreadWaitTime();
	}

	@Override
	public Map<String, String> getExtraParameters() {
		return extraParameters;
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
	public String getRandomField() {
		return facetFieldExtractor.getRandomFacetField();
	}

	@Override
	public String getRandomFilterQuery() {
		return filterQueryExtractor.getRandomQuery();
	}

	@Override
	public String getRandomQuery() {
		return queryExtractor.getRandomQuery();
	}

	@Override
	public synchronized CommonsHttpSolrServer getSolrServer() {
		if(server == null) {
			server = SolrServerRegistry.getSolrServer(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.SOLR_SEARCH_URL));
		}
		return server;
	}

	@Override
	public void incrementConcurrentOperations() {
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
		executerThread = new ConstantOperationExecutorThread(new QueryOperation(this));
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
