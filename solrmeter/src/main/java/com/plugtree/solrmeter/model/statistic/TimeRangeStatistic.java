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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.google.inject.Inject;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.model.QueryStatistic;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.exception.QueryException;

/**
 * 
 * Statistic to divide QTimes in time intervals.
 * @author tflobbe
 *
 */
@StressTestScope
public class TimeRangeStatistic implements QueryStatistic {
	
	private Map<TimeRange, Integer> counter;
	
	private int totalQueries;
	
	@Inject
	public TimeRangeStatistic() {
		counter = Collections.synchronizedMap(new HashMap<TimeRange, Integer>());
		this.addConfiguredRanges();
	}

	public TimeRangeStatistic(boolean useDefault) {
		this();
		if(useDefault) {
			addNewRange(0, 500);
			addNewRange(501, 1000);
			addNewRange(1001, 2000);
			addNewRange(2001, Integer.MAX_VALUE);
		}
	}
	
	private void addConfiguredRanges() {
		List<String> keys = SolrMeterConfiguration.getKeys(Pattern.compile("statistic\\.timeRange\\.range\\d*_\\d*"));
		for(String key:keys) {
			int min = getMinValue(key);
			int max = getMaxValue(key);
			addNewRange(min, max);
		}
		if(this.overlap()) {
			Logger.getLogger(this.getClass()).warn("Warning! the time range statistic ranges overlap");
		}
		
	}

	private int getMaxValue(String property) {
		return Integer.valueOf(property.replaceAll("statistic\\.timeRange\\.range\\d*_", ""));
	}

	private int getMinValue(String property) {
		return Integer.valueOf(property.replaceAll("statistic\\.timeRange\\.range", "").replaceAll("_\\d*", ""));
	}

	@Override
	public void onExecutedQuery(QueryResponse response, long clientTime) {
		synchronized (counter) {
			for(TimeRange range:counter.keySet()) {
				if(range.isIncluded(response.getQTime())) {
					counter.put(range, counter.get(range) + 1);
				}
			}
		}
		totalQueries++;
	}

	@Override
	public void onFinishedTest() {
		//does nothing
	}

	/**
	 * 
	 * @return Returns all the configured timeRanges and the percentage of strings on 
	 * each one of them.
	 */
	public Map<TimeRange, Integer> getActualPercentage() {
		Map<TimeRange, Integer> map = new HashMap<TimeRange, Integer>();
		if(totalQueries == 0) {
			return map;
		}
		synchronized (counter) {
			for(TimeRange range:counter.keySet()) {
				map.put(range, (counter.get(range) * 100) / totalQueries);
			}
		}
		return map;
	}
	
	@Override
	public void onQueryError(QueryException exception) {
		
		
	}
	
	/**
	 * Sets all counters to 0.
	 */
	public void restartCounter() {
		synchronized (counter) {
			for(TimeRange range:counter.keySet()) {
				counter.put(range, 0);
			}
		}
		totalQueries = 0;
	}
	
	public void addNewRange(int init, int end) {
		counter.put(new TimeRange(init, end), 0);
		SolrMeterConfiguration.setProperty(getPropertyKey(init, end), "true");
	}

	private String getPropertyKey(int init, int end) {
		return "statistic.timeRange.range" + init + "_" + end;
	}
	
	public void removeRange(int init, int end) {
		if(counter.remove(new TimeRange(init, end)) == null) {
			throw new RuntimeException();
		}
		SolrMeterConfiguration.removeProperty(getPropertyKey(init, end));
	}
	
	public boolean overlap() {
		LinkedList<TimeRange> ranges = new LinkedList<TimeRange>(counter.keySet());
		Collections.sort(ranges, new Comparator<TimeRange>() {

			@Override
			public int compare(TimeRange arg0, TimeRange arg1) {
				if(arg0.getMinTime() > arg1.getMinTime()) {
					return 1;
				}
				if(arg0.getMinTime() < arg1.getMinTime()) {
					return -1;
				}
				return 0;
			}
			
		});
		for(int i = 0; i < (ranges.size() - 1); i++) {
			if(ranges.get(i).getMaxTime() >= ranges.get(i + 1).getMinTime()) {
				return true;
			}
		}
		return false;
	}
	
	public int getCounterCount() {
		return counter.size();
	}
	
	public Collection<TimeRange> getActualRanges() {
		return counter.keySet();
	}

	public void removeAllRanges() {
		List<TimeRange> ranges = new LinkedList<TimeRange>(getActualRanges());
		for(TimeRange range:ranges) {
			this.removeRange(range.getMinTime(), range.getMaxTime());
		}
		
	}

}
