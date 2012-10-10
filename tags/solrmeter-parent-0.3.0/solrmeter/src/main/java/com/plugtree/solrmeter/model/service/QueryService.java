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
package com.plugtree.solrmeter.model.service;

import org.apache.solr.client.solrj.response.QueryResponse;

import com.plugtree.solrmeter.model.exception.QueryException;

/**
 * Query Service Interface
 * @author tflobbe
 *
 */
public interface QueryService {
	
	public QueryResponse executeQuery(String q, 
			String fq, 
			String qt, 
			boolean highlight, 
			String facetFields, 
			String sort, 
			String sortOrder,
			Integer rows,
			Integer start,
			String otherParams) throws QueryException;

}
