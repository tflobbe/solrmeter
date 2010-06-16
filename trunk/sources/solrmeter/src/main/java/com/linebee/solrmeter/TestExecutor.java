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
package com.linebee.solrmeter;

import com.linebee.solrmeter.model.QueryExecutor;
import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.model.statistic.HistogramQueryStatistic;
import com.linebee.solrmeter.model.statistic.QueryTimeHistoryStatistic;



public class TestExecutor {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		QueryExecutor queryExecutor = new QueryExecutor();
		queryExecutor.addStatistic(new HistogramQueryStatistic());
		queryExecutor.addStatistic(new QueryTimeHistoryStatistic());
		queryExecutor.prepare();
//		UpdateExecutor updateExecutor = new UpdateExecutor();
//		updateExecutor.prepare();
//		updateExecutor.start();
		queryExecutor.start();
		
		try {
			Thread.sleep(Integer.valueOf(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.TEST_TIME)) * 60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		queryExecutor.stop();
//		updateExecutor.stop();
	}

}
