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
package com.plugtree.solrmeter.statistic;

import org.apache.solr.client.solrj.SolrQuery;

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.statistic.QueryLogStatistic;

public class QueryLogStatisticTestCase extends BaseTestCase {

	public void simpleTest() {
		QueryLogStatistic statistic = new QueryLogStatistic();
		assertEquals(0, statistic.getLastQueries().size());
		statistic.onExecutedQuery(createQueryResponse(10), 0);
		assertEquals(1, statistic.getLastQueries().size());
		statistic.onExecutedQuery(createQueryResponse(10), 0);
		assertEquals(2, statistic.getLastQueries().size());
		
		statistic.onQueryError(createQueryException());
		statistic.onQueryError(createQueryException());
		assertEquals(4, statistic.getLastQueries().size());
		assertTrue(statistic.getLastQueries().getLast().isError());
		assertEquals(new Integer(-1), statistic.getLastQueries().getLast().getQTime());
		assertEquals("filterQuery=value", statistic.getLastQueries().getLast().getFacetQueryString());
		assertEquals("field=value", statistic.getLastQueries().getLast().getFilterQueryString());
		assertEquals("test", statistic.getLastQueries().getLast().getQueryString());
	}
	
	public void testManyQueries() {
		SolrMeterConfiguration.setProperty("solr.queryLogStatistic.maxStored", "100");
		QueryLogStatistic statistic = new QueryLogStatistic();
		for(int i = 1; i <= 100; i++) {
			statistic.onExecutedQuery(createQueryResponse(i), 0);
			assertEquals(i, statistic.getLastQueries().size());
		}
		assertEquals(new Integer(100), statistic.getLastQueries().getFirst().getQTime());
		assertEquals(new Integer(1), statistic.getLastQueries().getLast().getQTime());
		statistic.onExecutedQuery(createQueryResponse(1000), 0);
		assertEquals(new Integer(1000), statistic.getLastQueries().getFirst().getQTime());
		assertEquals(new Integer(2), statistic.getLastQueries().getLast().getQTime());
		assertFalse(statistic.getLastQueries().getLast().isError());
	}
	
	@Override
	protected void tearDown() throws Exception {
		SolrMeterConfiguration.loadConfiguration();
	}
	
	private QueryException createQueryException() {
		QueryException queryException = new QueryException();
		SolrQuery query = new SolrQuery();
		query.setQuery("test");
		query.setFilterQueries("field=value");
		query.addFacetQuery("filterQuery=value");
		queryException.setQuery(query);
		return queryException;
	}
}
