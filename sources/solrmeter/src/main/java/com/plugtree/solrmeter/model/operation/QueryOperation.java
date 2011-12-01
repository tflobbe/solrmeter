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
import com.plugtree.solrmeter.model.QueryExecutor;
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.generator.QueryGenerator;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.util.Date;

/**
 * Operation that executes a single query
 * @author tflobbe
 *
 */
public class QueryOperation implements Operation {
  
  private Logger logger = Logger.getLogger(this.getClass());
  
  private QueryExecutor executor;
  
  private QueryGenerator queryGenerator;
  
  public QueryOperation(QueryExecutor executor, QueryGenerator queryGenerator) {
    this.executor = executor;
    this.queryGenerator = queryGenerator;
  }
  
  
  public boolean execute() {
    SolrQuery query = queryGenerator.generate();
    try {
      logger.debug("executing query: " + query);
      long init = new Date().getTime();
      QueryResponse response = this.executeQuery(query);
      long clientTime = new Date().getTime() - init;
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
    return executor.getSolrServer().query(query);
  }
  
}
