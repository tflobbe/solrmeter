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
package com.plugtree.solrmeter.model.statistic;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;

import com.google.inject.Inject;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.model.QueryStatistic;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.exception.QueryException;
/**
 * 
 * @author tflobbe
 *
 */
@StressTestScope
public class QueryLogStatistic implements QueryStatistic {
  
  private static int maxStored;
  
  private List<QueryLogValue> queries;
  
  @Inject
  public QueryLogStatistic() {
    super();
    maxStored = Integer.parseInt(SolrMeterConfiguration.getProperty("solr.queryLogStatistic.maxStored", "400"));
    queries = Collections.synchronizedList(new LinkedList<QueryLogValue>());
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
    synchronized (queries) {
      queries.add(0, objectToAdd);
      if(queries.size() > maxStored) {
        queries.remove(queries.size() -1);
      }
    }
  }
  
  public LinkedList<QueryLogValue> getLastQueries() {
    return new LinkedList<QueryLogStatistic.QueryLogValue>(queries);
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
      Object parameter;
      error = false;

      NamedList<Object> params = (NamedList<Object>)response.getResponseHeader().get("params");
      if(params!=null) {
	      parameter = params.get("q");
	      if (parameter != null) {
	        queryString = parameter.toString();
	      }
	      parameter = params.get("fq");
	      if (parameter != null) {
	        filterQueryString = parameter.toString();
	      }
      }
      
      facetQueryString = createFacetQuery(response.getFacetFields());
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
    
    /**
     * Gets a comma separated values representation of this log entry
     * 
     * @return
     */
    
    public String getCSV(){
      return String.valueOf(error) + "," +
      queryString + "," +
      facetQueryString + "," +
      filterQueryString + "," +
      qTime.toString() + "," +
      results.toString();	
    }
  }
  
}
