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
package com.plugtree.solrmeter.model.exception;

import org.apache.solr.client.solrj.SolrQuery;

public class QueryException extends OperationException {

	private static final long serialVersionUID = -1524232396305950286L;

	/**
	 * Query that produced error
	 */
	private SolrQuery query;
	
	public QueryException() {
		super();
	}

	public QueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueryException(String message) {
		super(message);
	}

	public QueryException(Throwable cause) {
		super(cause);
	}
	
	public QueryException(Throwable cause, SolrQuery query) {
		super(cause);
		this.query = query;
	}
	
	public String getOperationName() {
		return "Query";
	}

	public SolrQuery getQuery() {
		return query;
	}

	public void setQuery(SolrQuery query) {
		this.query = query;
	}

}
