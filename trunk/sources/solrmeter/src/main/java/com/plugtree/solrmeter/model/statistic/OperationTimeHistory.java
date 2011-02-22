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

import java.util.Date;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;

import com.google.inject.Inject;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.model.OptimizeStatistic;
import com.plugtree.solrmeter.model.QueryStatistic;
import com.plugtree.solrmeter.model.UpdateStatistic;
import com.plugtree.solrmeter.model.exception.CommitException;
import com.plugtree.solrmeter.model.exception.OptimizeException;
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.exception.UpdateException;

/**
 * 
 * Statistic to keep time of all operations executed. It hast a milisecond precision.
 * 
 * @author tflobbe
 *
 */
@StressTestScope
public class OperationTimeHistory implements QueryStatistic, UpdateStatistic,
		OptimizeStatistic {
	
	private SortedMap<Long, Long> queriesTime;
	
	private SortedMap<Long, Long> updatesTime;
	
	private SortedMap<Long, Long> commitTime;
	
	private SortedMap<Long, Long> optimizeTime;
	
	private long precision = 1;
	
	private long addedQueriesInLastInterval;
	
	private long addedUpdatesInLastInterval;
	
	private long addedCommitsInLastInterval;
	
	private long addedOptimizesInLastInterval;
	
	private long initTime;
	
	@Inject
	public OperationTimeHistory() {
		queriesTime = Collections.synchronizedSortedMap(new TreeMap<Long, Long>());
		updatesTime = Collections.synchronizedSortedMap(new TreeMap<Long, Long>());
		commitTime = Collections.synchronizedSortedMap(new TreeMap<Long, Long>());
		optimizeTime = Collections.synchronizedSortedMap(new TreeMap<Long, Long>());
		initTime = new Date().getTime();
	}

	@Override
	public void onExecutedQuery(QueryResponse response, long clientTime) {
		addedQueriesInLastInterval = addTime(queriesTime, (long)response.getQTime(), addedQueriesInLastInterval);
	}

	/**
	 * Adds a value to the passed Map. It uses the method "getMapKey" to obtain the key to use.
	 * If there is already a value with the same key, then, using the parameter "addedValuesOnLastInterval"
	 * calculate the average of the added Objects and then add the result to the map
	 * @param map the map to add the value
	 * @param time the value to add to the map
	 * @param addedValuesOnLastInterval The number of added items to the last time interval.
	 * @return Return the added values in the last interval. 
	 */
	private long addTime(Map<Long, Long> map, long time,
			long addedValuesOnLastInterval) {
		long mapKey = getMapKey();
		if(map.containsKey(mapKey)) {
			map.put(mapKey, 
					(time + (map.get(mapKey) * addedValuesOnLastInterval))/(addedValuesOnLastInterval + 1));
			addedValuesOnLastInterval++;
		}else {
			map.put(mapKey, time);
			addedValuesOnLastInterval = 1;
		}
		return addedValuesOnLastInterval;
	}

	/**
	 * Generates the map key, using the actual time and the statistic precision
	 * @return
	 */
	protected long getMapKey() {
		return (new Date().getTime() - initTime) / precision;
	}

	@Override
	public void onFinishedTest() {}

	@Override
	public void onQueryError(QueryException exception) {}
	
	@Override
	public void onAddError(UpdateException exception) {}

	@Override
	public void onAddedDocument(UpdateResponse response) {
		addedUpdatesInLastInterval = addTime(updatesTime, (long)response.getQTime(), addedUpdatesInLastInterval);
	}

	@Override
	public void onCommit(UpdateResponse response) {
		addedCommitsInLastInterval = addTime(commitTime, (long)response.getQTime(), addedCommitsInLastInterval);
	}

	@Override
	public void onCommitError(CommitException exception) {}

	@Override
	public void onOptimizeError(OptimizeException exception) {}

	@Override
	public void onOptimizeFinished(long delay) {
		addedOptimizesInLastInterval = addTime(optimizeTime, delay, addedOptimizesInLastInterval);
	}

	@Override
	public void onOptimizeStared(long initTime) {}

	public Map<Long, Long> getQueriesTime() {
		return queriesTime;
	}

	public Map<Long, Long> getUpdatesTime() {
		return updatesTime;
	}

	public Map<Long, Long> getCommitTime() {
		return commitTime;
	}

	public Map<Long, Long> getOptimizeTime() {
		return optimizeTime;
	}

}
