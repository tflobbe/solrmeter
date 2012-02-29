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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
	
	private SolrServer solrServer;
	
	@Inject
	public RequestHandlerConnection() {
		this(SolrServerRegistry.getSolrServer(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.SOLR_SEARCH_URL)));
	}
	
	public RequestHandlerConnection(SolrServer solrServer) {
		super();
		this.solrServer = solrServer;
	}
	
	public Map<String, CacheData> getData() throws StatisticConnectionException {
		SolrRequest request = new MBeanRequest("CACHE");
		Map<String, CacheData> cacheData = new HashMap<String, CacheData>();
		try {
			NamedList<Object> namedList = solrServer.request(request);
			cacheData.put(FILTER_CACHE_NAME, getCacheData(namedList, "filterCache"));
			cacheData.put(QUERY_RESULT_CACHE_NAME, getCacheData(namedList, "queryResultCache"));
			cacheData.put(DOCUMENT_CACHE_NAME, getCacheData(namedList, "documentCache"));
			cacheData.put(FIELD_VALUE_CACHE_NAME, getCacheData(namedList, "fieldValueCache"));
			
			cacheData.put(CUMULATIVE_FILTER_CACHE_NAME, getCumulativeCacheData(namedList, "filterCache"));
			cacheData.put(CUMULATIVE_QUERY_RESULT_CACHE_NAME, getCumulativeCacheData(namedList, "queryResultCache"));
			cacheData.put(CUMULATIVE_DOCUMENT_CACHE_NAME, getCumulativeCacheData(namedList, "documentCache"));
			cacheData.put(CUMULATIVE_FIELD_VALUE_CACHE_NAME, getCumulativeCacheData(namedList, "fieldValueCache"));
		} catch (Exception e) {
			throw new StatisticConnectionException(e);
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
		return new CacheData((Long)stats.get("lookups"), (Long)stats.get("hits"), Float.valueOf((String)stats.get("hitratio")), (Long)stats.get("inserts"), (Long)stats.get("evictions"), Long.valueOf(stats.get("size").toString()), (Long)stats.get("warmupTime"));
	}

	@SuppressWarnings("unchecked")
  private NamedList<Object> getCacheNamedList(NamedList<Object> namedList,
      String cacheName) {
    NamedList<Object> cache = ((NamedList<Object>)((NamedList<Object>)((NamedList<Object>)namedList.get("solr-mbeans")).get("CACHE")).get(cacheName));
    return cache;
  }
	
	@SuppressWarnings("unchecked")
	private CacheData getCumulativeCacheData(NamedList<Object> namedList, String cacheName) {
    NamedList<Object> cache = getCacheNamedList(namedList, cacheName);
    if(cache == null) {
       return null;
     }
   NamedList<Object> stats = (NamedList<Object>)cache.get("stats");
		return new CacheData((Long)stats.get("cumulative_lookups"), (Long)stats.get("cumulative_hits"), Float.valueOf((String)stats.get("cumulative_hitratio")), (Long)stats.get("cumulative_inserts"), (Long)stats.get("cumulative_evictions"));
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
