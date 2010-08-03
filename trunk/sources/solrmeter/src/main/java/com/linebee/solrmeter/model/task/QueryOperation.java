package com.linebee.solrmeter.model.task;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.linebee.solrmeter.model.QueryExecutor;
import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.model.exception.QueryException;

public class QueryOperation implements Operation {
	
	private Logger logger = Logger.getLogger(this.getClass());

	private static Integer facetMinCount = Integer.valueOf(SolrMeterConfiguration.getProperty("solr.query.facet.minCount", "1"));
	
	private static Integer facetLimit = Integer.valueOf(SolrMeterConfiguration.getProperty("solr.query.facet.limit", "8"));
	
	private QueryExecutor executor;
	
	/**
	 * If set, strings are executed adding random felds as facet.
	 */
	private boolean useFacets = Boolean.valueOf(SolrMeterConfiguration.getProperty("solr.query.useFacets", "true"));
	
	private String facetMethod = SolrMeterConfiguration.getProperty("solr.query.facetMethod");
	
	private boolean useFilterQueries = Boolean.valueOf(SolrMeterConfiguration.getProperty("solr.query.useFilterQueries", "true"));
	
	public QueryOperation(QueryExecutor executor) {
		this.executor = executor;
	}
	
	public void execute() {
		SolrQuery query = new SolrQuery();
		query.setQuery(executor.getRandomQuery());
		query.setQueryType(executor.getQueryType());
		query.setIncludeScore(true);
		this.addExtraParameters(query);
		
		if(useFacets) {
			addFacetParameters(query);
		}
		if(useFilterQueries) {
			addFilterQueriesParameters(query);
		}
		try {
			logger.debug("executing query: " + query);
			long init = new Date().getTime();
			QueryResponse response = this.executeQuery(query);
			long clientTime = new Date().getTime() - init;
			logger.debug(response.getResults().getNumFound() + " results found in " + response.getQTime() + " ms");
			executor.notifyQueryExecuted(response, clientTime);
		} catch (SolrServerException e) {
			logger.error("Error on Query " + query);
			e.printStackTrace();
			executor.notifyError(new QueryException(e, query));
		}
	}

	/**
	 * Adds extra (not specific) parameters of query
	 * @param query
	 */
	private void addExtraParameters(SolrQuery query) {
		for(String paramKey:executor.getExtraParameters().keySet()) {
			query.add(paramKey, executor.getExtraParameters().get(paramKey));
		}
	}

	private void addFilterQueriesParameters(SolrQuery query) {
		String filterQString = executor.getRandomFilterQuery();
		if(!"".equals(filterQString.trim())) {
			query.addFilterQuery(filterQString);
		}
	}

	protected QueryResponse executeQuery(SolrQuery query) throws SolrServerException {
		return executor.getSolrServer().query(query);
	}

	private void addFacetParameters(SolrQuery query) {
		query.setFacet(true);
		query.addFacetField(executor.getRandomField());
		query.setFacetMinCount(facetMinCount);
		query.setFacetLimit(facetLimit);
		if(facetMethod != null && !"".equals(facetMethod)) {
			query.add("facet.method", facetMethod);
		}
		
	}

	public boolean isUseFacets() {
		return useFacets;
	}

	public void setUseFacets(boolean useFacets) {
		this.useFacets = useFacets;
	}
}
