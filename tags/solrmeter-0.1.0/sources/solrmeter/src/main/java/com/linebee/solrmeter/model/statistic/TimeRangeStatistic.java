/**
 * Copyright Linebee. www.linebee.com
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
package com.linebee.solrmeter.model.statistic;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;

import com.linebee.solrmeter.model.QueryStatistic;
import com.linebee.solrmeter.model.exception.QueryException;

/**
 * 
 * Statistic to divide QTimes in time intervals.
 * @author Tomás
 *
 */
public class TimeRangeStatistic implements QueryStatistic {
	
	private Map<TimeRange, Integer> counter;
	
	private int totalQueries;

	@Override
	public void onExecutedQuery(QueryResponse response, long clientTime) {
		for(TimeRange range:counter.keySet()) {
			if(range.isIncluded(response.getQTime())) {
				counter.put(range, counter.get(range) + 1);
			}
		}
		totalQueries++;
	}

	@Override
	public void onFinishedTest() {
		//does nothing
	}

	@Override
	public void prepare() {
		counter = new HashMap<TimeRange, Integer>();
		//TODO parameterize
		counter.put(new TimeRange(0, 500), 0);
		counter.put(new TimeRange(501, 1000), 0);
		counter.put(new TimeRange(1001, 2000), 0);
		counter.put(new TimeRange(2001), 0);
	}
	
	/**
	 * 
	 * @return Returns all the configured timeRanges and the percentage of queries on 
	 * each one of them.
	 */
	public Map<TimeRange, Integer> getActualPercentage() {
		Map<TimeRange, Integer> map = new HashMap<TimeRange, Integer>();
		if(totalQueries == 0) {
			return map;
		}
		for(TimeRange range:counter.keySet()) {
			map.put(range, (counter.get(range) * 100) / totalQueries);
		}
		return map;
	}
	
	@Override
	public void onQueryError(QueryException exception) {
		
		
	}

}
