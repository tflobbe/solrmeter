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
package com.linebee.solrmeter.model.statistic;

import java.util.LinkedList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;

import com.google.inject.Inject;
import com.linebee.solrmeter.model.QueryStatistic;
import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.model.exception.QueryException;
import com.linebee.stressTestScope.StressTestScope;

@StressTestScope
public class QueryLogStatistic implements QueryStatistic {
	
	private static int maxStored;
	
	private LinkedList<QueryLogValue> queries;
	
	@Inject
	public QueryLogStatistic() {
		super();
		maxStored = Integer.parseInt(SolrMeterConfiguration.getProperty("solr.queryLogStatistic.maxStored", "400"));
		queries = new LinkedList<QueryLogValue>();
	}

	@Override
	public void onExecutedQuery(QueryResponse response, long clientTime) {
		addToList(new QueryLogValue(response));
	}

	@Override
	public void onFinishedTest() {}

	@Override
	public void onQueryError(QueryException exception) {
		addToList(new QueryLogValue(exception));
	}
	
	private void addToList(QueryLogValue objectToAdd) {
		queries.addFirst(objectToAdd);
		if(queries.size() > maxStored) {
			queries.removeLast();
		}
	}
	
	public LinkedList<QueryLogValue> getLastQueries() {
		return queries;
	}
	
	public class QueryLogValue {
		
		private boolean error;
		
		private String queryString;
		
		private String facetQueryString;
		
		private String filterQueryString;
		
		private Integer qTime;
		
		private Long results;
		
		public QueryLogValue(QueryException exception) {
			error = true;
			queryString = exception.getQuery().getQuery();
			facetQueryString = createString(exception.getQuery().getFacetQuery());
			filterQueryString = createString(exception.getQuery().getFilterQueries());
			qTime = -1;
			results = new Long(0);
		}
		
		@SuppressWarnings("unchecked")
		public QueryLogValue(QueryResponse response) {
			error = false;
			queryString = (String) ((NamedList<Object>)response.getResponseHeader().get("params")).get("q");
			facetQueryString = createFacetQuery(response.getFacetFields());
			filterQueryString = (String) ((NamedList<Object>)response.getResponseHeader().get("params")).get("fq");
			qTime = response.getQTime();
			results = response.getResults().getNumFound();
		}
		
		private String createFacetQuery(List<FacetField> facetFields) {
			if(facetFields == null || facetFields.isEmpty()) {
				return "";
			}
			StringBuffer buff = new StringBuffer();
			for(FacetField query:facetFields) {
				buff.append(query.getName());
				buff.append("(" + query.getValueCount() + ") ");
			}
			return buff.toString();
		}

		private String createString(String[] facetQuery) {
			if(facetQuery == null || facetQuery.length == 0) {
				return "";
			}
			StringBuffer buff = new StringBuffer();
			for(String query:facetQuery) {
				buff.append(query);
			}
			return buff.toString();
		}
		
		@Override
		public String toString() {
			return super.toString();
		}

		public boolean isError() {
			return error;
		}

		public String getQueryString() {
			return queryString;
		}

		public String getFacetQueryString() {
			return facetQueryString;
		}

		public Integer getQTime() {
			return qTime;
		}

		public String getFilterQueryString() {
			return filterQueryString;
		}

		public Long getResults() {
			return results;
		}
	}

}
