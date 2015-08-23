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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.google.inject.Inject;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.model.QueryStatistic;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.exception.QueryException;

@StressTestScope
public class QueryTimeHistoryStatistic implements QueryStatistic {
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	private Map<Long, Integer> timePerInterval;
	
	private Map<Long, Integer> queriesPerInterval;
	
	private String queryTimeFilePath = null;
	
	/**
	 * Precision of this statistic in seconds.
	 */
	private long precision = 10;
	
	@Inject
	public QueryTimeHistoryStatistic() {
		super();
		timePerInterval = Collections.synchronizedMap(new HashMap<Long, Integer>());
		queriesPerInterval = Collections.synchronizedMap(new HashMap<Long, Integer>());
	}
	
	public QueryTimeHistoryStatistic(String filePath) {
		this();
		this.queryTimeFilePath = filePath;
	}

	@Override
	public void onExecutedQuery(QueryResponse response, long clientTime) {
		int qTime = response.getQTime();
		Date date = getNewDate();
		long second = (date.getTime()/1000L)/precision;
		if(queriesPerInterval.containsKey(second)) {
			queriesPerInterval.put(second, queriesPerInterval.get(second) + 1);
			timePerInterval.put(second, timePerInterval.get(second) + qTime);
		} else {
			queriesPerInterval.put(second, 1);
			timePerInterval.put(second, qTime);
		}
	}

	protected Date getNewDate() {
		return new Date();
	}

	@Override
	public void onFinishedTest() {
		printQueriesTime();
	}

	public Map<Integer, Integer> getCurrentHistory() {
		Map<Integer, Integer> history = new HashMap<Integer, Integer>();
		if(timePerInterval.isEmpty()) {
			return history;
		}
		long maxValue = Collections.max(timePerInterval.keySet());
		long minValue = Collections.min(timePerInterval.keySet());
		for(long i = minValue; i <=maxValue; i++) {
			if(queriesPerInterval.containsKey(i)) {
				history.put(new Long((i-minValue)*precision).intValue(), (timePerInterval.get(i) / queriesPerInterval.get(i)));
			}else {
				history.put(new Long((i-minValue)*precision).intValue(), 0);
			}
		}
		return history;
	}
	
	private void printQueriesTime() {
		if(timePerInterval.isEmpty()) {
			logger.warn("No data to build statistic");
			return;
		}
		if(queryTimeFilePath == null || "".equals(queryTimeFilePath)) {
			queryTimeFilePath = SolrMeterConfiguration.getProperty("solr.queryTime.filePath", "queryTime.csv");
		}
		logger.info("--------------Q TIME----------------------------");
		try {
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(queryTimeFilePath)));
            printQueriesTimeToStream(outputStream);
			outputStream.close();
		} catch (IOException e) {
			logger.error(e);
		}
		logger.info("--------------Q TIME----------------------------");
	}
	
	private void print(BufferedOutputStream outputStream, String range, Integer value) throws IOException {
		logger.info(range + ": " + value);
		String fileLine = range + ";" + value + "\n";
		outputStream.write(fileLine.getBytes());
	}

	@Override
	public void onQueryError(QueryException exception) {
		// TODO Auto-generated method stub
		
	}

    public void printQueriesTimeToStream(BufferedOutputStream outputStream) throws IOException {
        long maxValue = Collections.max(timePerInterval.keySet());
        long minValue = Collections.min(timePerInterval.keySet());
        for(long i = minValue; i <=maxValue; i++) {
            if(queriesPerInterval.containsKey(i)) {
                print(outputStream, ((i-minValue)*precision) + "sec - " + (((i-minValue)+1) * precision) + "sec", (timePerInterval.get(i) / queriesPerInterval.get(i)));
            }else {
                print(outputStream, ((i-minValue)*precision) + "sec - " + (((i-minValue)+1) * precision) + "sec", 0);
            }
        }
    }

}
