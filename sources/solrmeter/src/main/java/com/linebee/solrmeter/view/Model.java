/**
 * Copyright Linebee LLC
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
package com.linebee.solrmeter.view;

import java.util.HashMap;
import java.util.Map;

import com.linebee.solrmeter.model.OptimizeExecutor;
import com.linebee.solrmeter.model.OptimizeStatistic;
import com.linebee.solrmeter.model.QueryExecutor;
import com.linebee.solrmeter.model.QueryStatistic;
import com.linebee.solrmeter.model.UpdateExecutor;
import com.linebee.solrmeter.model.UpdateStatistic;


public class Model {
	
	private static Model instance;
	
	private QueryExecutor queryExecutor;
	
	private UpdateExecutor updateExecutor;
	
	private OptimizeExecutor optimizeExecutor;
	
	private Map<String, QueryStatistic> queryStatistics;
	
	private Map<String, UpdateStatistic> updateStatistics;
	
	private Map<String, OptimizeStatistic> optimizeStatistics;
	
	private Model() {
		queryStatistics = new HashMap<String, QueryStatistic>();
		updateStatistics = new HashMap<String, UpdateStatistic>();
		optimizeStatistics = new HashMap<String, OptimizeStatistic>();
	}
	
	public static Model getInstance() {
		if(instance == null) {
			instance = new Model();
		}
		return instance;
	}
	
	public void prepareAll() {
		queryExecutor.prepare();
		updateExecutor.prepare();
		optimizeExecutor.prepare();
	}
	
	public void putOptimizeStatistic(String string, OptimizeStatistic observer) {
		this.optimizeStatistics.put(string, observer);
	}

	public void setOptimizeExecutor(OptimizeExecutor executor) {
		this.optimizeExecutor = executor;
	}
	
	public OptimizeStatistic getOptimizeObserver(String statisticName) {
		return optimizeStatistics.get(statisticName);
	}
	
	public QueryExecutor getCurrentQueryExecutor() {
		return queryExecutor;
	}
	
	public void setQueryExecutor(QueryExecutor queryExecutor) {
		this.queryExecutor = queryExecutor;
	}
	
	public QueryStatistic getQueryStatistic(String statisticName) {
		return queryStatistics.get(statisticName);
	}
	
	public void putQueryStatistic(String statisticName, QueryStatistic statistic) {
		this.queryStatistics.put(statisticName, statistic);
	}
	
	public void putUpdateStatistic(String statisticName, UpdateStatistic statistic) {
		this.updateStatistics.put(statisticName, statistic);
	}
	
	public UpdateStatistic getUpdateStatistic(String statisticName) {
		return updateStatistics.get(statisticName);
	}

	public void setUpdateExecutor(UpdateExecutor executor) {
		this.updateExecutor = executor;
	}

	public UpdateExecutor getCurrentUpdateExecutor() {
		return updateExecutor;
	}

	public OptimizeExecutor getCurrentOptimizeExecutor() {
		return optimizeExecutor;
	}

	public OptimizeStatistic getOptimizeStatistic(String string) {
		return optimizeStatistics.get(string);
	}


}
