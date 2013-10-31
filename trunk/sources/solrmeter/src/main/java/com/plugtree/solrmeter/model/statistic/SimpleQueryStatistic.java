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

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.model.QueryStatistic;
import com.plugtree.solrmeter.model.exception.QueryException;

import java.math.BigDecimal;

/**
 * Statistic that will show basic information about queries 
 * @author tflobbe
 *
 */
@StressTestScope
public class SimpleQueryStatistic implements QueryStatistic {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * The sum of QTime of all executed strings
	 */
	private long totalQTime;
	
	/**
	 * The number of executed Queries
	 */
	private long totalQueries;
	
	/**
	 * The sum of client time of all executed strings. Client time is the time measured from
	 * this appication (and not Solr QTime).
	 */
	private long totalClientTime;
	
	/**
	 * The number of errors ocurred on strings.
	 */
	private int totalErrors;
	
	@Override
	public void onExecutedQuery(QueryResponse response, long clientTime) {
		long qTime = response.getQTime();
		this.totalQTime+=qTime;
		this.totalClientTime+=clientTime;
		this.totalQueries++;
	}

	@Override
	public void onFinishedTest() {
		logger.info("Executed Queries: " + totalQueries);
		logger.info("Sum Query Time: " + totalQTime);
		logger.info("Total Client Time: " + totalClientTime);
		if(totalQueries != 0) {
			logger.info("Query average QTime: " + (totalQTime/totalQueries));
			logger.info("Client average QTime: " + (totalClientTime/totalQueries));
		}
	}

	public long getTotalQTime() {
		return totalQTime;
	}

	public long getTotalQueries() {
		return totalQueries;
	}

	public long getTotalClientTime() {
		return totalClientTime;
	}

    public long getAverageQueryTime() {
        return BigDecimal.valueOf(getTotalQTime()).divide(BigDecimal.valueOf(getTotalQueries()), 2, BigDecimal.ROUND_HALF_UP).longValue();
    }

    public long getAverageClientTime() {
        return BigDecimal.valueOf(getTotalClientTime()).divide(BigDecimal.valueOf(getTotalQueries()), 2, BigDecimal.ROUND_HALF_UP).longValue();
    }

	@Override
	public void onQueryError(QueryException exception) {
		totalErrors++;
	}
	
	public int getTotalErrors() {
		return totalErrors;
	}

}
