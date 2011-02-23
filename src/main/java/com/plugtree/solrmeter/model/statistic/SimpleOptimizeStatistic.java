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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.model.OptimizeStatistic;
import com.plugtree.solrmeter.model.exception.OptimizeException;

/**
 * This implementation of OptimizeStatistic is the main one. The data maintained
 * by this is the one shown on the OptimizeConsolePanel.
 * @author tflobbe
 *
 */
@StressTestScope
public class SimpleOptimizeStatistic implements OptimizeStatistic {
	
	private List<String> optimizeErrors;
	
	private int optimizationCount;
	
	private long lastOptimizationTime;
	
	private long totalOptimizationTime;
	
	private OptimizationResult lastOptimizationResult;
	
	@Inject
	public SimpleOptimizeStatistic() {
		super();
		optimizeErrors = Collections.synchronizedList(new LinkedList<String>());
		optimizationCount = 0;
	}
	
	@Override
	public void onOptimizeError(OptimizeException exception) {
		optimizeErrors.add(exception.getMessage());
		lastOptimizationResult = OptimizationResult.FAILED;
	}

	@Override
	public void onOptimizeFinished(long delay) {
		optimizationCount++;
		lastOptimizationTime=delay;
		totalOptimizationTime+=delay;
		lastOptimizationResult = OptimizationResult.OK;

	}

	@Override
	public void onOptimizeStared(long initTime) {

	}

	public int getOptimizeErrorCount() {
		return optimizeErrors.size();
	}

	public int getOptimizationCount() {
		return optimizationCount;
	}

	public long getLastOptimizationTime() {
		return lastOptimizationTime;
	}

	public long getTotalOptimizationTime() {
		return totalOptimizationTime;
	}
	
	public long getAverageOptimizationTime() {
		if(optimizationCount == 0) {
			return 0;
		}
		return totalOptimizationTime / optimizationCount;
	}

	public OptimizationResult getLastOptimizationResult() {
		return lastOptimizationResult;
	}
	

}
