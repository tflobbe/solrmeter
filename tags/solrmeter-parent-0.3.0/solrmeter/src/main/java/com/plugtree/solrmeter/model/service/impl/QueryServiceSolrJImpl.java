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
package com.plugtree.solrmeter.model.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.google.inject.Singleton;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.SolrServerRegistry;
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.service.QueryService;

/**
 * SolrJ Implementation of the QueryService
 * @author tflobbe
 *
 */
@Singleton
public class QueryServiceSolrJImpl implements QueryService {

	@Override
	public QueryResponse executeQuery(String q, String fq, String qt,
			boolean highlight, String facetFields, String sort, String sortOrder, Integer rows, Integer start, 
			String otherParams) throws QueryException {
		SolrServer server = SolrServerRegistry.getSolrServer(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.SOLR_SEARCH_URL));
		SolrQuery query = this.createQuery(q, fq, qt, highlight, facetFields, sort, sortOrder, rows, start, otherParams);
		QueryResponse response = null;
		try {
			response = server.query(query);
		} catch (SolrServerException e) {
			throw new QueryException(e);
		}
		return response;
	}

	protected SolrQuery createQuery(String q, String fq, String qt,
			boolean highlight, String facetFields, String sort, String sortOrder, Integer rows, Integer start, 
			String otherParams) throws QueryException {
		SolrQuery query = new SolrQuery();
		if(q != null) {
			query.setQuery(q);
		}
		if(fq != null) {
			List<String> filterQueries = this.getFilterQueries(fq);
			for(String filterQuery:filterQueries) {
				query.addFilterQuery(filterQuery);
			}
		}
		if(qt != null) {
			query.setQueryType(qt);
		}
		query.setHighlight(highlight);
		if(facetFields == null || "".equals(facetFields)) {
			query.setFacet(false);
		}else {
			query.setFacet(true);
			List<String> facets = this.getFacets(facetFields);
			for(String facet:facets) {
				query.addFacetField(facet);
			}
		}
		if(sort != null && !"".equals(sort)) {
			query.setSortField(sort, ORDER.valueOf(sortOrder));
		}
		if(rows != null && rows < 0) {
			throw new QueryException("Rows can't be less than 0");
		}else if(rows != null) {
			query.setRows(rows);
		}
		if(start != null && start < 0) {
			throw new QueryException("Rows can't be less than 0");
		}else if(start != null) {
			query.setStart(start);
		}
		
		if(otherParams != null) {
			List<String> params = this.getOtherParams(otherParams);
			for(String param:params) {
				query.add(getParamName(param), getParamValue(param));
			}
		}
		return query;
	}

	protected String getParamName(String param) {
		return param.substring(0, param.indexOf("="));
	}

	protected String getParamValue(String param) {
		return param.substring(param.indexOf("=") + 1 );
	}

	/**
	 * Return the list of name=value pairs for other params.
	 * @param otherParams
	 * @return
	 * @throws QueryException
	 */
	protected List<String> getOtherParams(String otherParams) throws QueryException {
		List<String> list = getCommaSeparatedValues(otherParams);
		for(String param:list) {
			this.validateOtherParam(param);
		}
		return list;
	}

	/**
	 * validate that the param is of type type=value. Value can be empty, but not type
	 * @param param
	 * @throws QueryException
	 */
	private void validateOtherParam(String param) throws QueryException {
		if(!param.contains("=") || param.indexOf("=") == 0) {
			throw new QueryException("The parameter " + param + " must contain the field name");
		}
	}

	/**
	 * returns the list of filter queries to add to the query
	 * @param fq
	 * @return
	 */
	protected List<String> getFilterQueries(String fq) throws QueryException {
		List<String> list = getCommaSeparatedValues(fq);
		for(String filter:list) {
			this.validateFilterQuery(filter);
		}
		return list;
	}

	/**
	 * Validate that the filter query contains a field and a value
	 * @param filter
	 * @throws QueryException
	 */
	private void validateFilterQuery(String filter) throws QueryException {
		if(!filter.contains(":") || filter.indexOf(":") == 0) {
			throw new QueryException("Filter query " + filter + " must contain the field name");
		}
		if(filter.indexOf(":") == filter.length() - 1) {
			throw new QueryException("Filter query " + filter + " must contain a field value");
		}
	}

	/**
	 * Returns a list of trings parsing the comma separated values of the parameter string.
	 * @param facetFields
	 * @return
	 * @throws QueryException 
	 */
	protected List<String> getFacets(String facetFields) throws QueryException {
		List<String> list = getCommaSeparatedValues(facetFields);
		for(String facet:list) {
			if(hasWitespaces(facet)) {
				throw new QueryException("Facet fields can't have whitespaces");
			}
		}
		return list;
	}

	private boolean hasWitespaces(String facet) {
		return facet.contains(" ");
	}
	
	private List<String> getCommaSeparatedValues(String value) {
		List<String> list = new LinkedList<String>();
		String[] splitted = value.split(",");
		for(String chunk:splitted) {
			String facet = chunk.trim();
			if(!facet.isEmpty()) {
				list.add(chunk.trim());
			}
		}
		return list;
	}

}
