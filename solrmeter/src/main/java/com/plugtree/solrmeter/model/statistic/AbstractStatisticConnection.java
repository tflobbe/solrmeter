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

import java.util.Map;

import com.plugtree.solrmeter.model.exception.StatisticConnectionException;

/**
 * Superclass for statistic connections with Solr. A StatisticConnection will be used for extracting
 * information from Solr, even for showing it or for analyzing it and extract conclussions.
 * @author tomas
 *
 */
public abstract class AbstractStatisticConnection {
	
	public final static String FILTER_CACHE_NAME = "filterCache";
	public final static String DOCUMENT_CACHE_NAME = "documentCache";
	public final static String FIELD_CACHE_NAME = "fieldCache";
	public final static String FIELD_VALUE_CACHE_NAME = "fieldValueCache";
	public final static String QUERY_RESULT_CACHE_NAME = "queryResultCache";
	
	public final static String CUMULATIVE_FILTER_CACHE_NAME = "cumulativeFilterCache";
	public final static String CUMULATIVE_DOCUMENT_CACHE_NAME = "cumulativeDocumentCache";
	public final static String CUMULATIVE_FIELD_VALUE_CACHE_NAME = "cumulativeFieldValueCache";
	public final static String CUMULATIVE_QUERY_RESULT_CACHE_NAME = "cumulativeQueryResultCache";
	

	public abstract Map<String, CacheData> getData() throws StatisticConnectionException;
	
	public CacheData getFilterCacheData(Map<String, CacheData> map) {
		return map.get(FILTER_CACHE_NAME);
	}
	
	public CacheData getDocumentCacheData(Map<String, CacheData> map) {
		return map.get(DOCUMENT_CACHE_NAME);
	}
	
	public CacheData getFieldValueCacheData(Map<String, CacheData> map) {
		return map.get(FIELD_VALUE_CACHE_NAME);
	}
	
	public CacheData getQueryResultCacheData(Map<String, CacheData> map) {
		return map.get(QUERY_RESULT_CACHE_NAME);
	}
	
	public CacheData getCumulativeFilterCacheData(Map<String, CacheData> map) {
		return map.get(CUMULATIVE_FILTER_CACHE_NAME);
	}
	
	public CacheData getCumulativeDocumentCacheData(Map<String, CacheData> map) {
		return map.get(CUMULATIVE_DOCUMENT_CACHE_NAME);
	}
	
	public CacheData getCumulativeFieldValueCacheData(Map<String, CacheData> map) {
		return map.get(CUMULATIVE_FIELD_VALUE_CACHE_NAME);
	}
	
	public CacheData getCumulativeQueryResultCacheData(Map<String, CacheData> map) {
		return map.get(CUMULATIVE_QUERY_RESULT_CACHE_NAME);
	}
}
