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
package com.plugtree.solrmeter.model.operation;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.plugtree.solrmeter.model.QueryExecutor;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.generator.QueryGenerator;

/**
 * Operation that executes a single query
 * @author tflobbe
 *
 */
public class QueryOperation implements Operation {
  
  private final static Logger logger = Logger.getLogger(QueryOperation.class);
  
  private final QueryExecutor executor;
  
  private final QueryGenerator queryGenerator;
  
  public QueryOperation(QueryExecutor executor, QueryGenerator queryGenerator) {
    this.executor = executor;
    this.queryGenerator = queryGenerator;
  }
  
  
  public boolean execute() {
    SolrQuery query = queryGenerator.generate();
    try {
      logger.debug("executing query: " + query);
      long init = System.nanoTime();
      QueryResponse response = this.executeQuery(query);
      long clientTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - init);
      logger.debug(response.getResults().getNumFound() + " results found in " + response.getQTime() + " ms");
      if(response.getQTime() < 0) {
        throw new RuntimeException("The query returned less than 0 as q time: " + response.getResponseHeader().get("q") + response.getQTime());
      }
      executor.notifyQueryExecuted(response, clientTime);
    } catch (SolrServerException e) {
      logger.error("Error on Query " + query);
      e.printStackTrace();
      executor.notifyError(new QueryException(e, query));
      return false;
    }
    return true;
  }
  
  protected QueryResponse executeQuery(SolrQuery query) throws SolrServerException {
	String requestMethod = SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERY_METHOD, "GET");
	return executor.getSolrServer().query(query, METHOD.valueOf(requestMethod));
  }
  
}
