package com.linebee.solrmeter.model;

import java.util.Map;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.linebee.solrmeter.model.exception.QueryException;
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
	CommonsHttpSolrServer getSolrServer();

	/**
	 * @return returns a random Query of the existing ones.
	 */
	String getRandomQuery();

	/**
	 * @return returns a random filter Query of the existing ones.
	 */
	String getRandomFilterQuery();

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
	 * 
	 * @return returns a random Field of the existing ones.
	 */
	String getRandomField();

	/**
	 * @return Query type
	 */
	String getQueryType();

	/**
	 * Adds a Statistic Observer to the executor
	 * @param statistic
	 */
	void addStatistic(QueryStatistic statistic);

	/**
	 * Returns the number of queries to be executed every minute
	 * @return
	 */
	int getQueriesPerMinute();

	/**
	 * Returns the extra added parameters.
	 * @return
	 */
	Map<String, String> getExtraParameters();

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
	 * Increments in one the number of strings per minute
	 */
	void incrementConcurrentOperations();

	/**
	 * Decrements in one the number of strings per minute
	 */
	void decrementConcurrentQueries();

	/**
	 * Determines whether this executor is running.
	 * @return
	 */
	boolean isRunning();
	
}