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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.google.inject.Inject;
import com.linebee.solrmeter.model.QueryStatistic;
import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.model.exception.QueryException;
import com.linebee.stressTestScope.StressTestScope;

@StressTestScope
public class HistogramQueryStatistic implements QueryStatistic {
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	private Map<Long, Integer> histogram;
	
	private String histogramFilePath = null;
	
	private long histogramInterval = 100L;
	
	@Inject
	public HistogramQueryStatistic() {
		super();
		histogram = new HashMap<Long, Integer>();
	}
	
	public HistogramQueryStatistic(String filePath) {
		this();
		this.histogramFilePath = filePath;
	}
	
	@Override
	public void onExecutedQuery(QueryResponse response, long clientTime) {
		long time = response.getQTime() / histogramInterval;
		int cant;
		if(histogram.containsKey(time)) {
			cant = histogram.get(time);
			cant++;
		}else {
			cant = 1;
		}
		histogram.put(time, cant);

	}

	@Override
	public void onFinishedTest() {
		printHistogram();
	}

	@Override
	public void prepare() {

	}
	
	public Map<Integer, Integer> getCurrentHisogram() {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		if(histogram.isEmpty()) {
			return map;
		}
		long maxValue = Collections.max(histogram.keySet());
		if(maxValue == 0)  {
			maxValue = 10;
		}
		for(long i = 0; i <=maxValue; i++) {
			if(histogram.containsKey(i)) {
				map.put(new Long(i*histogramInterval).intValue(), histogram.get(i));
			}else {
				map.put(new Long(i*histogramInterval).intValue(), 0);
			}
		}
		return map;
	}
	
	private void printHistogram() {
		if(histogram.isEmpty()) {
			logger.warn("No data to build histogram.");
			return;
		}
		try {
			if(histogramFilePath == null || "".equals(histogramFilePath)) {
				histogramFilePath = SolrMeterConfiguration.getProperty("solr.histogram.filePath", "histogram.csv");
			}
			File histogramFile = new File(histogramFilePath);
			if(histogramFile.exists()) {
				histogramFile.delete();
			}
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(histogramFilePath)));
			logger.info("------------------ Histogram --------------------");
			long maxValue = Collections.max(histogram.keySet());
			for(long i = 0; i <=maxValue; i++) {
				if(histogram.containsKey(i)) {
					print(outputStream, (i*histogramInterval) + "ms - " + ((i+1) * histogramInterval) + "ms", histogram.get(i));
				}else {
					print(outputStream, (i*histogramInterval) + "ms - " + ((i+1) * histogramInterval) + "ms", 0);
				}
			}
			outputStream.close();
			logger.info("---------------- End Histogram -------------------");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onQueryError(QueryException exception) {
		
		
	}

	private void print(BufferedOutputStream outputStream, String range, Integer value) throws IOException {
		logger.info(range + ": " + value);
		String fileLine = range + ";" + value + "\n";
		outputStream.write(fileLine.getBytes());
	}

}
