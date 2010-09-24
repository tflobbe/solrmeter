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

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.linebee.solrmeter.model.FieldExtractor;
import com.linebee.solrmeter.model.QueryExecutor;
import com.linebee.solrmeter.model.QueryExtractor;
import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.model.exception.QueryException;

/**
 * Operation that executes a single query
 * @author tflobbe
 *
 */
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
	
	private boolean addRandomExtraParams = Boolean.valueOf(SolrMeterConfiguration.getProperty("solr.query.addRandomExtraParams", "true"));
	
	private QueryExtractor queryExtractor;
	
	private QueryExtractor filterQueryExtractor;
	
	private FieldExtractor facetFieldExtractor;
	
	private QueryExtractor extraParameterExtractor;
	
	public QueryOperation(QueryExecutor executor,
			QueryExtractor queryExtractor, 
			QueryExtractor filterQueryExtractor, 
			FieldExtractor facetFieldExtractor,
			QueryExtractor extraParamExtractor) {
		this.executor = executor;
		this.queryExtractor = queryExtractor;
		this.filterQueryExtractor = filterQueryExtractor;
		this.facetFieldExtractor = facetFieldExtractor;
		this.extraParameterExtractor = extraParamExtractor;
		
	}

	public void execute() {
		SolrQuery query = new SolrQuery();
		query.setQuery(queryExtractor.getRandomQuery());
		query.setQueryType(executor.getQueryType());
		query.setIncludeScore(true);
		this.addExtraParameters(query);
		
		if(useFacets) {
			addFacetParameters(query);
		}
		if(useFilterQueries) {
			addFilterQueriesParameters(query);
		}
		if(addRandomExtraParams) {
			this.addRandomExtraParameters(query);
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
	
	/**
	 * Adds a random line of the extra parameters extractor
	 * @param query
	 */
	private void addRandomExtraParameters(SolrQuery query) {
		String randomExtraParam = extraParameterExtractor.getRandomQuery();
		if(randomExtraParam == null || "".equals(randomExtraParam.trim())) {
			return;
		}
		for(String param:randomExtraParam.split("&")) {
			int equalSignIndex = param.indexOf("=");
			if(equalSignIndex > 0) {
				query.add(param.substring(0, equalSignIndex).trim(), param.substring(equalSignIndex + 1).trim());
			}
		}
	}

	private void addFilterQueriesParameters(SolrQuery query) {
		String filterQString = filterQueryExtractor.getRandomQuery();
		if(!"".equals(filterQString.trim())) {
			query.addFilterQuery(filterQString);
		}
	}

	protected QueryResponse executeQuery(SolrQuery query) throws SolrServerException {
		return executor.getSolrServer().query(query);
	}

	private void addFacetParameters(SolrQuery query) {
		query.setFacet(true);
		query.addFacetField(facetFieldExtractor.getRandomFacetField());
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
