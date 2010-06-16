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
package com.linebee.solrmeter.model.statistic;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.linebee.solrmeter.model.QueryStatistic;
import com.linebee.solrmeter.model.exception.QueryException;

public class SimpleQueryStatistic implements QueryStatistic {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * The sum of QTime of all executed queries
	 */
	private long totalQTime;
	
	/**
	 * The number of executed Queries
	 */
	private long totalQueries;
	
	/**
	 * The sum of client time of all executed queries. Client time is the time measured from
	 * this appication (and not Solr QTime).
	 */
	private long totalClientTime;
	
	/**
	 * The number of errors ocurred on queries.
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

	@Override
	public void prepare() {
		

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

	@Override
	public void onQueryError(QueryException exception) {
		totalErrors++;
	}
	
	public int getTotalErrors() {
		return totalErrors;
	}

}
