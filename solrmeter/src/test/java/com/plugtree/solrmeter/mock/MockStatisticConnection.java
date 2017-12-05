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
package com.plugtree.solrmeter.mock;

import java.util.HashMap;
import java.util.Map;

import com.plugtree.solrmeter.model.exception.StatisticConnectionException;
import com.plugtree.solrmeter.model.statistic.AbstractStatisticConnection;
import com.plugtree.solrmeter.model.statistic.CacheData;
/**
 * Mock implementation of an Abstract Statistic Connection
 * @author tomas
 *
 */
public class MockStatisticConnection extends AbstractStatisticConnection {
	
	private Map<String, Map<String, CacheData>> data;
	private Map<String, CacheData> internalData;
	
	private String singleCollection = "SINGLE_COLLECTION";
	
	public MockStatisticConnection() {
		super();
		this.data = new HashMap<>();
		this.internalData = new HashMap<>();
		this.data.put(singleCollection, internalData);
	}
	
	public void putData(String key, CacheData cacheData) {
		data.get(singleCollection).put(key, cacheData);
	}

	@Override
	public Map<String, Map<String, CacheData>> getData() throws StatisticConnectionException {
		return data;
	}

}
