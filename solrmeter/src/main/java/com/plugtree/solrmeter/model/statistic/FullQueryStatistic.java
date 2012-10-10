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

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.solr.client.solrj.response.QueryResponse;

import com.google.inject.Inject;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.model.QueryStatistic;
import com.plugtree.solrmeter.model.exception.QueryException;
/**
 * 
 * @author tflobbe
 *
 */
@StressTestScope
public class FullQueryStatistic implements QueryStatistic {
	
	/**
	 * Stores the execution time as key, and the sumation of strings times on the same instant.
	 */
	private SortedMap<Long, Integer> queryTimeByDate;
	
	/**
	 * This list will not always be sorted. If some method need it to be sorted, it has so sort it first.
	 * The idea of always sorting the same collection is tha it's faster to sort a "pretty much sorted"
	 * collection than a completly unsorted one.
	 */
	private List<Integer> sortedQueryTimes;
	
	private Date lastErrorDate;
	
	private int totalQTime;
	
	@Inject
	public FullQueryStatistic() {
		lastErrorDate = null;
		queryTimeByDate = Collections.synchronizedSortedMap(new TreeMap<Long, Integer>());
		sortedQueryTimes = Collections.synchronizedList(new LinkedList<Integer>());
		totalQTime = 0;
	}
	
	@Override
	public void onExecutedQuery(QueryResponse response, long clientTime) {
		sortedQueryTimes.add(response.getQTime());
		totalQTime+=response.getQTime();
		long mapKey = new Date().getTime();
		Integer actualValue = queryTimeByDate.get(mapKey);
		Integer newValue;
		if(actualValue == null) {
			newValue = response.getQTime();
		}else {
			newValue = actualValue + response.getQTime();
		}
		queryTimeByDate.put(mapKey, newValue);
	}

	@Override
	public void onFinishedTest() {
	}

	@Override
	public void onQueryError(QueryException exception) {
		lastErrorDate = exception.getDate();
	}
	
	public Double getMedian() {
		if(sortedQueryTimes.isEmpty()) {
			return new Double(0);
		}
		synchronized (sortedQueryTimes) {
			Collections.sort(sortedQueryTimes);
			if((sortedQueryTimes.size()&1) ==0) {
				int firstIndex = sortedQueryTimes.size()/2 - 1;
				int secondIndex = sortedQueryTimes.size()/2 ;
				return new Double(((double)sortedQueryTimes.get(firstIndex) + (double)sortedQueryTimes.get(secondIndex))/ 2);
			}else {
				return new Double(sortedQueryTimes.get(sortedQueryTimes.size()/2));
			}
		}
	}
	
	public Integer getMode() {
		int actualValue = -1;
		int actualValueCount = 0;
		int maxValueCount = -1;
		int actualMode = -1;
		synchronized (sortedQueryTimes) {
			Collections.sort(sortedQueryTimes);
			for(Integer integer:sortedQueryTimes) {
				if(integer == actualValue) {
					actualValueCount++;
				}else {
					if(actualValueCount > maxValueCount) {
						maxValueCount = actualValueCount;
						actualMode = actualValue;
					}
					actualValue = integer;
					actualValueCount = 1;
				}
			}
		}
		if(actualValueCount > maxValueCount) {
			maxValueCount = actualValueCount;
			actualMode = actualValue;
		}
		return actualMode;
	}
	
	public Double getVariance() {
		if(sortedQueryTimes.size() == 0) {
			return new Double(0);
		}
		double average = (double)totalQTime/sortedQueryTimes.size();
		double sumation = 0;
		synchronized (sortedQueryTimes) {
			for(Integer value:sortedQueryTimes) {
				sumation+= Math.pow((double)(value - average), 2.0);
			}
			return sumation / sortedQueryTimes.size();
		}
	}
	
	public Double getStandardDeviation() {
		return Math.sqrt(getVariance());
	}
	
	public Integer getTotaAverage() {
		if(sortedQueryTimes.size() == 0) {
			return -1;
		}
		return totalQTime/sortedQueryTimes.size();
	}
	
	public Integer getLastMinuteAverage() {
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.MINUTE, -1);
		return getAverageSince(calendar.getTime());
	}
	
	public Integer getLastTenMinutesAverage() {
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.MINUTE, -10);
		return getAverageSince(calendar.getTime());
	}
	
	public Integer getAverageSince(Date sinceDate) {
		int sumation = 0;
		int cantItems = 0;
		long minDate = sinceDate.getTime();
		synchronized (queryTimeByDate) {
			for(Long value:queryTimeByDate.keySet()) {
				if(value >= minDate) {
					sumation+=queryTimeByDate.get(value);
					cantItems++;
				}
			}
		}
		if(cantItems != 0) {
			return sumation/cantItems;
		}else {
			return -1;
		}
	}
	
	public Date getLastErrorTime() {
		return lastErrorDate;
	}

}
