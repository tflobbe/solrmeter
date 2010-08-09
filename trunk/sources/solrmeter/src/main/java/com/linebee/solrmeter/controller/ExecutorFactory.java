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
package com.linebee.solrmeter.controller;

import java.util.Map;

import com.google.inject.Inject;
import com.linebee.solrmeter.model.OptimizeExecutor;
import com.linebee.solrmeter.model.QueryExecutor;
import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.model.UpdateExecutor;
/**
 * Factory for Executors. The main idea of this class is to hold the instances of all available executors
 * and knows wich one needs to be used. This functionality can't be implemented using only guice. The instances
 * of the executors are created by guice.
 * @author tflobbe
 *
 */
public class ExecutorFactory {
	
	private Map<String, QueryExecutor> queryExecutors;
	private Map<String, UpdateExecutor> updateExecutors;
	private Map<String, OptimizeExecutor> optimizeExecutors;
	
	@Inject
	public ExecutorFactory(Map<String, QueryExecutor> queryExecutors,
			Map<String, UpdateExecutor> updateExecutors,
			Map<String, OptimizeExecutor> optimizeExecutors) {
		super();
		this.queryExecutors = queryExecutors;
		this.updateExecutors = updateExecutors;
		this.optimizeExecutors = optimizeExecutors;
	}

	public QueryExecutor getQueryExecutor(String name) {
		return queryExecutors.get(name);
	}
	
	public QueryExecutor getCurrentQueryExecutor() {
		return this.getQueryExecutor(SolrMeterConfiguration.getProperty("executor.queryExecutor"));
	}
	
	public UpdateExecutor getUpdateExecutor(String name) {
		return updateExecutors.get(name);
	}
	
	public UpdateExecutor getCurrentUpdateExecutor() {
		return this.getUpdateExecutor(SolrMeterConfiguration.getProperty("executor.updateExecutor"));
	}
	
	public OptimizeExecutor getOptimizeExecutor(String name) {
		return optimizeExecutors.get(name);
	}
	
	public OptimizeExecutor getCurrentOptimizeExecutor() {
		return this.getOptimizeExecutor(SolrMeterConfiguration.getProperty("executor.optimizeExecutor"));
	}
}
