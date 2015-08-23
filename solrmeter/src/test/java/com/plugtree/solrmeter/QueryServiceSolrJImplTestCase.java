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
package com.plugtree.solrmeter;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;

import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.service.impl.QueryServiceSolrJImpl;

public class QueryServiceSolrJImplTestCase extends BaseTestCase {

	public void testGetFacets() throws QueryException {
		QueryServiceSolrJSpy service = new QueryServiceSolrJSpy();
		assertEquals(0, service.getFacets("").size());
		assertEquals(1, service.getFacets("name").size());
		assertEquals(1, service.getFacets(" name ").size());
		assertEquals(1, service.getFacets("name,").size());
		assertEquals(2, service.getFacets("name, category").size());
		assertEquals(3, service.getFacets("name, category, type").size());
		try {
			service.getFacets("name category type");
			fail("Exception expected");
		}catch(QueryException e) {
			//expected
		}
	}
	
	public void testGetFilterQueries() throws QueryException {
		QueryServiceSolrJSpy service = new QueryServiceSolrJSpy();
		assertEquals(0, service.getFilterQueries("").size());
		assertEquals(1, service.getFilterQueries("name:tomas").size());
		assertEquals(2, service.getFilterQueries("name:tomas, surname:\"Fernandez Lobbe\"").size());
		assertEquals(2, service.getFilterQueries("name:tomas, surname:(Fernandez Lobbe)").size());
		assertEquals(2, service.getFilterQueries("name:tomas, surname:Fernandez Lobbe").size());
		try {
			service.getFilterQueries("name=tomas");
			fail("Expected Exception");
		}catch(QueryException e) {
			//expected
		}
		
		try {
			service.getFilterQueries("name tomas");
			fail("Expected Exception");
		}catch(QueryException e) {
			//expected
		}
		try {
			service.getFilterQueries(":tomas");
			fail("Expected Exception");
		}catch(QueryException e) {
			//expected
		}
		try {
			service.getFilterQueries("name:");
			fail("Expected Exception");
		}catch(QueryException e) {
			//expected
		}
		
	}
	
	public void testGetOtherParams() throws QueryException {
		QueryServiceSolrJSpy service = new QueryServiceSolrJSpy();
		assertEquals(0, service.getOtherParams("").size());
		assertEquals(1, service.getOtherParams("indent=on").size());
		assertEquals(2, service.getOtherParams("indent=on, debugQuery=true").size());
		try {
			service.getOtherParams("=true");
			fail("Expected Exception");
		}catch(QueryException e) {
			//expected
		}
	}
	
	public void testGetParamNameAndValue() {
		QueryServiceSolrJSpy service = new QueryServiceSolrJSpy();
		assertEquals("indent", service.getParamName("indent=on"));
		assertEquals("on", service.getParamValue("indent=on"));
	}
	
	public void testCreateQuery() throws QueryException {
		QueryServiceSolrJSpy service = new QueryServiceSolrJSpy();
		SolrQuery query = service.createQuery("some query", "name:tomas", "/dismax", false, "name, surname", null, null, 10, 0, "");
		assertEquals("some query", query.get("q"));
		assertEquals(new String[]{"name:tomas"}, query.getFilterQueries());
		assertEquals("/dismax", query.getQueryType());
		assertEquals(new String[]{"name", "surname"}, query.getFacetFields());
		
		service.createQuery(null, "name:tomas", "/dismax", false, "name, surname", null, null, 10, 0, "");
		service.createQuery("", null, "/dismax", false, "name, surname", null, null, 10, 0, "");
		service.createQuery("", "name:tomas", null, false, "name, surname", null, null, 10, 0, "");
		service.createQuery("", "name:tomas",  "/dismax", false, null, null, null, 10, 0, "");
		service.createQuery("", "name:tomas",  "/dismax", false, "name, surname", "name", "desc", 10, 0, "");
		service.createQuery("", "name:tomas",  "/dismax", false, "name, surname", "name", "asc", 10, 0, "");
		service.createQuery("", "name:tomas",  "/dismax", false, "name, surname", "name", "asc", 10, 0, null);
		service.createQuery("", "name:tomas",  "/dismax", false, "name, surname", "name", "asc", null, 0, "");
		service.createQuery("", "name:tomas",  "/dismax", false, "name, surname", "name", "asc", 10, null, "");
	}
	
	private class QueryServiceSolrJSpy extends QueryServiceSolrJImpl {
		
		public List<String> getFacets(String facetFields) throws QueryException {
			return super.getFacets(facetFields);
		}
		
		@Override
		public List<String> getFilterQueries(String fq) throws QueryException {
			return super.getFilterQueries(fq);
		}
		
		@Override
		protected List<String> getOtherParams(String otherParams)
				throws QueryException {
			return super.getOtherParams(otherParams);
		}
		
		@Override
		protected String getParamName(String param) {
			return super.getParamName(param);
		}
		
		@Override
		protected String getParamValue(String param) {
			return super.getParamValue(param);
		}
		
		@Override
		protected SolrQuery createQuery(String q, String fq, String qt,
				boolean highlight, String facetFields, String sort,
				String sortOrder, Integer rows, Integer start, String otherParams)
				throws QueryException {
			return super.createQuery(q, fq, qt, highlight, facetFields, sort, sortOrder,
					rows, start, otherParams);
		}
	}
}
