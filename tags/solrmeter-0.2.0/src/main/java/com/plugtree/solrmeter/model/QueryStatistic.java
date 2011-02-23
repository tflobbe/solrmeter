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
package com.plugtree.solrmeter.model;

import org.apache.solr.client.solrj.response.QueryResponse;

import com.plugtree.solrmeter.model.exception.QueryException;
/**
 * Interface to be implemented by all the query statistics.
 * @author tflobbe
 *
 */
public interface QueryStatistic {

	/**
	 * To be executed when a query is performed
	 * @param response solr query response
	 * @param clientTime the time the query took from the client side (from solrmeter)
	 */
	void onExecutedQuery(QueryResponse response, long clientTime);

	/**
	 * This method will be executed when the test finishes.
	 */
	void onFinishedTest();

	/**
	 * This method will be executed when an error ocurrs on a query.
	 * @param exception
	 */
	void onQueryError(QueryException exception);

}
