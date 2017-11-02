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

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.NamedList;

import com.google.inject.Inject;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.SolrServerRegistry;
import com.plugtree.solrmeter.model.exception.StatisticConnectionException;

/**
 * Connection with the stats using request handlers
 * @author tflobbe
 *
 */
public class RequestHandlerConnection extends AbstractStatisticConnection {
	private static final String SINGLE_COLLECTION = "SINGLE_COLLECTION";

	private static final String collectionsStr = SolrMeterConfiguration.getProperty("solr.collection.names", null);
	
	private final static Logger logger = Logger.getLogger(RequestHandlerConnection.class);

	private static final List<String> collections;
	
	private Map<String, SolrServer> solrServer;
	
	static {
		List<String> _collections = new ArrayList<>();
		stream(ofNullable(collectionsStr).orElse(SINGLE_COLLECTION).split("\\,")).map(String::trim).forEach(_collections::add);
		collections = Collections.unmodifiableList(_collections);
	}
	
	@Inject
	public RequestHandlerConnection() {
		this(
			new HashMap<String, SolrServer>() {{
				collections.forEach(collectionName -> {
				put(collectionName, 
						SolrServerRegistry.getSolrServer(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.SOLR_SEARCH_URL) 
							+ (collectionName.equals(SINGLE_COLLECTION) ? "" : collectionName)));
				});
			}}
		);
	}
	
	public RequestHandlerConnection(Map<String, SolrServer> solrServer) {
		super();
		this.solrServer = solrServer;
	}
	
	public Map<String, Map<String, CacheData>> getData() throws StatisticConnectionException {
		SolrRequest request = new MBeanRequest("CACHE");
		Map<String, Map<String, CacheData>> cacheData = new HashMap<>();
		
		for (String collectionName : collections) {
			try {
				Map<String, CacheData> collectionCacheData = new HashMap<>();
				NamedList<Object> namedList = solrServer.get(collectionName).request(request);
				collectionCacheData.put(FILTER_CACHE_NAME, getCacheData(namedList, "filterCache"));
				collectionCacheData.put(QUERY_RESULT_CACHE_NAME, getCacheData(namedList, "queryResultCache"));
				collectionCacheData.put(DOCUMENT_CACHE_NAME, getCacheData(namedList, "documentCache"));
				collectionCacheData.put(FIELD_VALUE_CACHE_NAME, getCacheData(namedList, "fieldValueCache"));
				
				collectionCacheData.put(CUMULATIVE_FILTER_CACHE_NAME, getCumulativeCacheData(namedList, "filterCache"));
				collectionCacheData.put(CUMULATIVE_QUERY_RESULT_CACHE_NAME, getCumulativeCacheData(namedList, "queryResultCache"));
				collectionCacheData.put(CUMULATIVE_DOCUMENT_CACHE_NAME, getCumulativeCacheData(namedList, "documentCache"));
				collectionCacheData.put(CUMULATIVE_FIELD_VALUE_CACHE_NAME, getCumulativeCacheData(namedList, "fieldValueCache"));
				
				cacheData.put(collectionName, collectionCacheData);
			} catch (Exception e) {
				throw new StatisticConnectionException(e);
			}
		}
		return cacheData;
	}
		
	@SuppressWarnings("unchecked")
	private CacheData getCacheData(NamedList<Object> namedList, String cacheName) {
	  NamedList<Object> cache = getCacheNamedList(namedList, cacheName);
	   if(cache == null) {
	      return null;
	    }
		NamedList<Object> stats = (NamedList<Object>)cache.get("stats");
		CacheData cd = new CacheData((Long)stats.get("lookups"), (Long)stats.get("hits"), (Float)stats.get("hitratio"), (Long)stats.get("inserts"), (Long)stats.get("evictions"), Long.valueOf(stats.get("size").toString()), (Long)stats.get("warmupTime"));
		return cd;
	}

	@SuppressWarnings("unchecked")
  private NamedList<Object> getCacheNamedList(NamedList<Object> namedList,
      String cacheName) {
    NamedList<Object> cache = ((NamedList<Object>)((NamedList<Object>)((NamedList<Object>)namedList.get("solr-mbeans")).get("CACHE")).get(cacheName));
    logger.trace("Got cache for " + cacheName);
    return cache;
  }
	
	@SuppressWarnings("unchecked")
	private CacheData getCumulativeCacheData(NamedList<Object> namedList, String cacheName) {
    NamedList<Object> cache = getCacheNamedList(namedList, cacheName);

    if(cache == null) {
       return null;
    }

    NamedList<Object> stats = (NamedList<Object>)cache.get("stats");
		CacheData data = new CacheData((Long)stats.get("cumulative_lookups"), (Long)stats.get("cumulative_hits"), (Float)stats.get("cumulative_hitratio"), (Long)stats.get("cumulative_inserts"), (Long)stats.get("cumulative_evictions"));
		return data;
	}
	
	private class MBeanRequest extends SolrRequest {
		
		private static final long serialVersionUID = 1L;
		private ModifiableSolrParams params;
		
		public MBeanRequest(String category, String key) {
			super(METHOD.GET, "/admin/mbeans");
			params = new ModifiableSolrParams();
			params.set("cat", category);
			params.set("stats", "true");
			params.set("key", key);
		}
		
		public MBeanRequest(String category) {
			super(METHOD.GET, "/admin/mbeans");
			params = new ModifiableSolrParams();
			params.set("cat", category);
			params.set("stats", "true");
		}

		@Override
		public SolrResponse process(SolrServer server) throws SolrServerException,
				IOException {
			long startTime = System.currentTimeMillis();
		    SolrPingResponse res = new SolrPingResponse();
		    res.setResponse( server.request( this ) );
		    res.setElapsedTime( System.currentTimeMillis()-startTime );
		    return res;
		}
		
		@Override
		public SolrParams getParams() {
			return params;
		}
		
		@Override
		public Collection<ContentStream> getContentStreams() throws IOException {
			return null;
		}
	}

}
