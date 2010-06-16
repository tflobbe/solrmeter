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
package com.linebee.solrmeter.model.task;

import java.util.Date;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.linebee.solrmeter.model.QueryExecutor;
import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.model.exception.QueryException;

/**
 * Executes a Query Operation
 * 
 * @author Tomás
 *
 */
public class QueryThread extends AbstractOperationThread {
	
	private static Integer facetMinCount = Integer.valueOf(SolrMeterConfiguration.getProperty("solr.query.facet.minCount", "1"));
	
	private static Integer facetLimit = Integer.valueOf(SolrMeterConfiguration.getProperty("solr.query.facet.limit", "8"));
	
//	private static Map<String, List<String>> filterQueries = new HashMap<String, List<String>>();
	
	private QueryExecutor executor;
	
	/**
	 * If set, queries are executed adding random felds as facet.
	 */
	private boolean useFacets = Boolean.valueOf(SolrMeterConfiguration.getProperty("solr.query.useFacets", "true"));
	
	private String facetMethod = SolrMeterConfiguration.getProperty("solr.query.facetMethod");
	
	private boolean useFilterQueries = Boolean.valueOf(SolrMeterConfiguration.getProperty("solr.query.useFilterQueries", "true"));
	
	public QueryThread(QueryExecutor executor, long queryInterval) {
		super(queryInterval);
		this.executor = executor;
	}
	
	protected void executeOperation() {
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
