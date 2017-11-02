package com.plugtree.solrmeter.statistic;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.util.NamedList;

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.mock.SolrServerMock;
import com.plugtree.solrmeter.model.exception.StatisticConnectionException;
import com.plugtree.solrmeter.model.statistic.CacheData;
import com.plugtree.solrmeter.model.statistic.RequestHandlerConnection;

public class RequestHandlerConnectionTestCase extends BaseTestCase {
	private static String dummyCollection = "SINGLE_COLLECTION";
	
	public void testConnectionData() throws MalformedURLException, StatisticConnectionException {
		SolrServer solrServer = this.createMockSolrServer();
		Map<String, SolrServer> serverMap = new HashMap<>();
		serverMap.put(dummyCollection, solrServer);
		RequestHandlerConnection connection = new RequestHandlerConnection(serverMap);
		Map<String, CacheData> data = connection.getData().get(dummyCollection);
		CacheData filterQueryData = connection.getFilterCacheData(data);
		assertEquals((long)10, filterQueryData.getLookups());
		assertEquals((long)5, filterQueryData.getHits());
		assertEquals((float)0.50, filterQueryData.getHitratio());
		assertEquals((long)0, filterQueryData.getEvictions());
		assertEquals((long)10, filterQueryData.getInserts());
		assertEquals((long)50, filterQueryData.getSize());
	}
	
	public void testAllCachesPresent() throws MalformedURLException, StatisticConnectionException {
		SolrServer solrServer = this.createMockSolrServer();
		Map<String, SolrServer> serverMap = new HashMap<>();
		serverMap.put(dummyCollection, solrServer);
		RequestHandlerConnection connection = new RequestHandlerConnection(serverMap);
		Map<String, CacheData> data = connection.getData().get(dummyCollection);

		assertNotNull(connection.getFilterCacheData(data));
		assertNotNull(connection.getDocumentCacheData(data));
		assertNotNull(connection.getFieldValueCacheData(data));
		assertNotNull(connection.getQueryResultCacheData(data));
		
		assertNotNull(connection.getCumulativeFilterCacheData(data));
		assertNotNull(connection.getCumulativeDocumentCacheData(data));
		assertNotNull(connection.getCumulativeFieldValueCacheData(data));
		assertNotNull(connection.getCumulativeQueryResultCacheData(data));
	}
	
	/*
	 * 	<long name="cumulative_lookups">100</long>
	 *	<long name="cumulative_hits">50</long>
	 *	<str name="cumulative_hitratio">0.50</str>
	 *	<long name="cumulative_inserts">100</long>
	 *	<long name="cumulative_evictions">0</long>
	 */
	public void testCumulativeData() throws MalformedURLException, StatisticConnectionException {
		SolrServer solrServer = this.createMockSolrServer();
		Map<String, SolrServer> serverMap = new HashMap<>();
		serverMap.put(dummyCollection, solrServer);
		RequestHandlerConnection connection = new RequestHandlerConnection(serverMap);
		Map<String, CacheData> data = connection.getData().get(dummyCollection);
		CacheData filterQueryData = connection.getCumulativeFilterCacheData(data);
		
		assertEquals((long)100, filterQueryData.getLookups());
		assertEquals((long)50, filterQueryData.getHits());
		assertEquals((float)0.50, filterQueryData.getHitratio());
		assertEquals((long)0, filterQueryData.getEvictions());
		assertEquals((long)100, filterQueryData.getInserts());
		assertEquals((long)-1, filterQueryData.getSize());
		assertEquals((long)-1, filterQueryData.getWarmupTime());
	}
	
	private SolrServer createMockSolrServer() throws MalformedURLException {
		return this.createMockSolrServer("queryResultCache", "fieldCache", "documentCache", "fieldValueCache", "filterCache");
	}
	
	 private SolrServer createMockSolrServer(String... caches) throws MalformedURLException {
	    SolrServerMock mock = new SolrServerMock();
	    mock.setResponseToRequest("/admin/mbeans", this.createMBeansNamedList(caches));
	    return mock;
	  }

	private NamedList<Object> createMBeansNamedList(String... caches) {
		NamedList<Object> namedList = new NamedList<Object>();
		namedList.add("solr-mbeans", this.createMainCacheNamedList(caches));
		return namedList;
	}

	private NamedList<Object> createMainCacheNamedList(String... caches) {
		NamedList<Object> namedList = new NamedList<Object>();
		namedList.add("CACHE", this.createCachesNamedList(caches));
		return namedList;
	}

	private NamedList<Object> createCachesNamedList(String... caches) {
		NamedList<Object> namedList = new NamedList<Object>();
		for(String cache:caches) {
		  namedList.add(cache, this.createCacheNamedList());
		}
		return namedList;
	}

	/*
	 * 	<str name="class">org.apache.solr.search.FastLRUCache</str>
	 *	<str name="version">1.0</str>
	 *	−
	 *	<str name="description">
	 *	Concurrent LRU Cache(maxSize=512, initialSize=512, minSize=460, acceptableSize=486, cleanupThread=false)
	 *	</str>
	 *	−
	 *	<str name="srcId">
	 *	$Id: FastLRUCache.java 938708 2010-04-27 22:40:55Z hossman $
	 *	</str>
	 *	−
	 *	<str name="src">
	 *	$URL: http://svn.apache.org/repos/asf/lucene/dev/trunk/solr/src/java/org/apache/solr/search/FastLRUCache.java $
	 *	</str>
	 */
	private NamedList<Object> createCacheNamedList() {
		NamedList<Object> namedList = new NamedList<Object>();
		namedList.add("class", "org.apache.solr.search.FastLRUCache");
		namedList.add("version", "1.0");
		namedList.add("description", "Concurrent LRU Cache(maxSize=512, initialSize=512, minSize=460, acceptableSize=486, cleanupThread=false)");
		namedList.add("srcId", "$URL: http://svn.apache.org/repos/asf/lucene/dev/trunk/solr/src/java/org/apache/solr/search/FastLRUCache.java $");
		namedList.add("src", "$URL: http://svn.apache.org/repos/asf/lucene/dev/trunk/solr/src/java/org/apache/solr/search/FastLRUCache.java $");
		namedList.add("stats", this.createStatsNamedList());
		return namedList ;
	}

	/*
	 *	<lst name="stats">
	 *	<long name="lookups">10</long>
	 *	<long name="hits">5</long>
	 *	<str name="hitratio">0.50</str>
	 *	<long name="inserts">10</long>
	 *	<long name="evictions">0</long>
	 *	<long name="size">50</long>
	 *	<long name="warmupTime">15</long>
	 *	<long name="cumulative_lookups">100</long>
	 *	<long name="cumulative_hits">50</long>
	 *	<str name="cumulative_hitratio">0.50</str>
	 *	<long name="cumulative_inserts">100</long>
	 *	<long name="cumulative_evictions">0</long>
	 *	</lst>
	 */
	private NamedList<Object> createStatsNamedList() {
		NamedList<Object> namedList = new NamedList<Object>();
		namedList.add("lookups", new Long(10));
		namedList.add("hits", new Long(5));
		namedList.add("hitratio", new Float(0.50));
		namedList.add("inserts", new Long(10));
		namedList.add("evictions", new Long(0));
		namedList.add("size", new Long(50));
		namedList.add("warmupTime", new Long(15));
		namedList.add("cumulative_lookups", new Long(100));
		namedList.add("cumulative_hits", new Long(50));
		namedList.add("cumulative_hitratio", new Float(0.50));
		namedList.add("cumulative_inserts", new Long(100));
		namedList.add("cumulative_evictions", new Long(0));
		return namedList;
	}
	
	 public void testMissingCaches() throws MalformedURLException, StatisticConnectionException {
	    SolrServer solrServer = this.createMockSolrServer("filterCache");
		Map<String, SolrServer> serverMap = new HashMap<>();
		serverMap.put(dummyCollection, solrServer);
		RequestHandlerConnection connection = new RequestHandlerConnection(serverMap);
		Map<String, CacheData> data = connection.getData().get(dummyCollection);

	    assertNotNull(connection.getFilterCacheData(data));
	    assertNull(connection.getDocumentCacheData(data));
	    assertNull(connection.getFieldValueCacheData(data));
	    assertNull(connection.getQueryResultCacheData(data));
	    
	    assertNotNull(connection.getCumulativeFilterCacheData(data));
	    assertNull(connection.getCumulativeDocumentCacheData(data));
	    assertNull(connection.getCumulativeFieldValueCacheData(data));
	    assertNull(connection.getCumulativeQueryResultCacheData(data));
	  }

}

