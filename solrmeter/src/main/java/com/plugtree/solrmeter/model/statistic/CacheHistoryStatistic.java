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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.google.inject.Inject;
import com.plugtree.solrmeter.model.QueryStatistic;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.exception.StatisticConnectionException;
import com.plugtree.solrmeter.view.statistic.CacheHistoryPanel;
import com.plugtree.stressTestScope.StressTestScope;

/**
 * 
 * Model of the Cache statistic. This statistic will show the evolution of the Solr caches
 * @author tflobbe
 *
 */
@StressTestScope
public class CacheHistoryStatistic implements QueryStatistic {
	private static final String SINGLE_COLLECTION = "SINGLE_COLLECTION";
	
	private final static Logger logger = Logger.getLogger(CacheHistoryStatistic.class);

	private static final String collectionsStr = SolrMeterConfiguration.getProperty("solr.collection.names", null);

	private static final List<String> collections;

	/**
	 * Stores the historical data of the filterCache
	 */
	private Map<String, SortedMap<Long, CacheData>> filterCacheData = new HashMap<>();
	
	/**
	 * Stores the historical data of the documentCache
	 */
	private Map<String, SortedMap<Long, CacheData>> documentCacheData;
	
	/**
	 * Stores the historical data of the queryResultCache
	 */
	private Map<String, SortedMap<Long, CacheData>> queryResultCacheData;
	
	/**
	 * Stores the historical data of the fieldValueCache
	 */
	private Map<String, SortedMap<Long, CacheData>> fieldValueCacheData;
	
	/**
	 * Stores the cumulative data of the filterCache
	 */
	private Map<String, CacheData> filterCacheCumulativeData = new HashMap<>();
	
	/**
	 * Stores the cumulative data of the documentCache
	 */
	private Map<String, CacheData> documentCacheCumulativeData = new HashMap<>();
	
	/**
	 * Stores the cumulative data of the queryResultCache
	 */
	private Map<String, CacheData> queryResultCacheCumulativeData = new HashMap<>();
	
	/**
	 * Stores the cumulative data of the fieldValueCache
	 */
	private Map<String, CacheData> fieldValueCacheCumulativeData = new HashMap<>();
	
	/**
	 * Stores the description of the filterCache
	 */
	private Map<String, String> filterCacheDescription = new HashMap<>();
	
	
	
	/**
	 * Connection with Solr statistics
	 */
	private AbstractStatisticConnection connection;
	
	private long initTime;
	
	//TODO @configurable
	private long refreshInterval = 1000;
	
	private StatisticUpdateThread updateThread;
	
	static {
		List<String> _collections = new ArrayList<>();
		stream(ofNullable(collectionsStr).orElse(SINGLE_COLLECTION).split("\\,")).map(String::trim).forEach(_collections::add);
		collections = Collections.unmodifiableList(_collections);
	}
	
	@Inject
	public CacheHistoryStatistic(AbstractStatisticConnection connection) {
		super();
		
		this.connection = connection;
		
		collections.forEach(collection -> {
			filterCacheData = new HashMap<>();
			filterCacheData.put(collection, Collections.synchronizedSortedMap(new TreeMap<Long, CacheData>()));
			
			queryResultCacheData = new HashMap<>();
			queryResultCacheData.put(collection, Collections.synchronizedSortedMap(new TreeMap<Long, CacheData>()));

			documentCacheData = new HashMap<>();
			documentCacheData.put(collection, Collections.synchronizedSortedMap(new TreeMap<Long, CacheData>()));
			
			fieldValueCacheData = new HashMap<>(); 
			fieldValueCacheData.put(collection, Collections.synchronizedSortedMap(new TreeMap<Long, CacheData>()));
			
			filterCacheDescription = new HashMap<>();
			
			this.initTime = System.currentTimeMillis();
		});
	}
	
	
	public void updateData() {
		Long time = System.currentTimeMillis() - initTime;
			
		for (String collection : collections) {
			try {
				Map<String, CacheData> cacheData = connection.getData().get(collection);
				
				Map<Long, CacheData> mappedCacheData = filterCacheData.get(collection);
				if (mappedCacheData == null) {
					filterCacheData.put(collection, Collections.synchronizedSortedMap(new TreeMap<Long, CacheData>()));
				}
				put(time, filterCacheData.get(collection), cacheData, RequestHandlerConnection.FILTER_CACHE_NAME);
				
				mappedCacheData = queryResultCacheData.get(collection);
				if (mappedCacheData == null) {
					queryResultCacheData.put(collection, Collections.synchronizedSortedMap(new TreeMap<Long, CacheData>()));
				}
				put(time, queryResultCacheData.get(collection), cacheData, RequestHandlerConnection.QUERY_RESULT_CACHE_NAME);
				
				mappedCacheData = documentCacheData.get(collection);
				if (mappedCacheData == null) {
					documentCacheData.put(collection, Collections.synchronizedSortedMap(new TreeMap<Long, CacheData>()));
				}
				put(time, documentCacheData.get(collection), cacheData, RequestHandlerConnection.DOCUMENT_CACHE_NAME);

				
				mappedCacheData = fieldValueCacheData.get(collection);
				if (mappedCacheData == null) {
					fieldValueCacheData.put(collection, Collections.synchronizedSortedMap(new TreeMap<Long, CacheData>()));
				}
				put(time, fieldValueCacheData.get(collection), cacheData, RequestHandlerConnection.FIELD_VALUE_CACHE_NAME);
				
				logger.trace("Adding document cumulative cache for " + collection + " collection: " + cacheData.get(RequestHandlerConnection.CUMULATIVE_DOCUMENT_CACHE_NAME));
				
				filterCacheCumulativeData.put(collection, cacheData.get(RequestHandlerConnection.CUMULATIVE_FILTER_CACHE_NAME));
				queryResultCacheCumulativeData.put(collection, cacheData.get(RequestHandlerConnection.CUMULATIVE_QUERY_RESULT_CACHE_NAME));
				documentCacheCumulativeData.put(collection, cacheData.get(RequestHandlerConnection.CUMULATIVE_DOCUMENT_CACHE_NAME));
				fieldValueCacheCumulativeData.put(collection, cacheData.get(RequestHandlerConnection.CUMULATIVE_FIELD_VALUE_CACHE_NAME));
					
			} catch (StatisticConnectionException e) {
				logger.error("Could not update statistic", e);
			}
		}
		
	}
	
	private void put(Long time, SortedMap<Long,CacheData> destDataMap, Map<String, CacheData> cacheData, String cacheName) {
		logger.trace(cacheName);
		logger.trace(cacheData);
		
		CacheData cacheDataValue = cacheData.get(cacheName);
	  if(cacheDataValue != null) {
	    destDataMap.put(time, cacheDataValue);
	  }
  }


  public Map<String, SortedMap<Long, CacheData>> getFilterCacheData() {
		return filterCacheData;
	}
	
	public Map<String, SortedMap<Long, CacheData>> getDocumentCacheData() {
		return documentCacheData;
	}


	public class StatisticUpdateThread extends Thread {
		
		private boolean running = false;
		
		public boolean isRunning() {
			return running;
		}

		@Override
		public synchronized void run() {
			while(running) {
				try {
					this.wait(Long.valueOf(refreshInterval));
					if(running) {
						updateData();
					}
				} catch (InterruptedException e) {
					logger.error("Error on query thread", e);
					throw new RuntimeException(e);
				}
			}
		}
		
		@Override
		public synchronized void start() {
			this.running = true;
			super.start();
		}
		
		public synchronized void wake() {
			this.notify();
		}
		
		@Override
		public void destroy() {
			this.running = false;
		}

	}

	@Override
	public void onFinishedTest() {
		if(updateThread != null) {
			updateThread.destroy();
			updateThread = null;
		}
	}

	public Map<String, SortedMap<Long, CacheData>> getQueryResultCacheData() {
		return queryResultCacheData;
	}


	public Map<String, SortedMap<Long, CacheData>> getFieldValueCacheData() {
		return fieldValueCacheData;
	}


	public Map<String, CacheData> getFilterCacheCumulativeData() {
		return filterCacheCumulativeData;
	}


	public Map<String, CacheData> getDocumentCacheCumulativeData() {
		return documentCacheCumulativeData;
	}


	public Map<String, CacheData> getQueryResultCacheCumulativeData() {
		return queryResultCacheCumulativeData;
	}


	public Map<String, CacheData> getFieldValueCacheCumulativeData() {
		return fieldValueCacheCumulativeData;
	}


	private Map<String, String> getFilterCacheDescription() {
		return filterCacheDescription;
	}


	public long getRefreshInterval() {
		return refreshInterval;
	}


	@Override
	public void onExecutedQuery(QueryResponse response, long clientTime) {
		if(updateThread == null) {
			updateThread = new StatisticUpdateThread();
		}
		if(!updateThread.isRunning()) {
			updateThread.start();
		}
		
	}


	@Override
	public void onQueryError(QueryException exception) {}

}
