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
package com.plugtree.solrmeter.statistic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.model.statistic.HistogramQueryStatistic;


public class HistogramQueryStatisticTestCase extends BaseTestCase {
	
	public void testOneQuery() {
		HistogramQueryStatistic statistic = new HistogramQueryStatistic();
		statistic.onExecutedQuery(this.createQueryResponse(10), 0);
		statistic.onFinishedTest();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("histogram.csv")));
			assertEquals("0ms - 100ms;1", reader.readLine());
		} catch (FileNotFoundException e) {
			fail(e);
		} catch (IOException e) {
			fail(e);
		}
	}
	
	public void testManyQueries() {
		HistogramQueryStatistic statistic = new HistogramQueryStatistic();
		statistic.onExecutedQuery(this.createQueryResponse(99), 0);
		statistic.onExecutedQuery(this.createQueryResponse(99), 0);
		statistic.onExecutedQuery(this.createQueryResponse(101), 0);
		statistic.onExecutedQuery(this.createQueryResponse(102), 0);
		statistic.onExecutedQuery(this.createQueryResponse(103), 0);
		statistic.onExecutedQuery(this.createQueryResponse(104), 0);
		statistic.onExecutedQuery(this.createQueryResponse(105), 0);
		statistic.onExecutedQuery(this.createQueryResponse(106), 0);
		statistic.onExecutedQuery(this.createQueryResponse(107), 0);
		statistic.onExecutedQuery(this.createQueryResponse(108), 0);
		statistic.onExecutedQuery(this.createQueryResponse(109), 0);
		statistic.onExecutedQuery(this.createQueryResponse(110), 0);
		statistic.onExecutedQuery(this.createQueryResponse(1001), 0);
		statistic.onFinishedTest();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("histogram.csv")));
			assertEquals("0ms - 100ms;2", reader.readLine());
			assertEquals("100ms - 200ms;10", reader.readLine());
			for(int i = 2; i < 10; i++) {
				assertEquals(i*100 + "ms - " + (i+1)*100 + "ms;0", reader.readLine());
			}
			assertEquals("1000ms - 1100ms;1", reader.readLine());
		} catch (FileNotFoundException e) {
			fail(e);
		} catch (IOException e) {
			fail(e);
		}
	}
	
	public void testEmpty() throws InterruptedException {
		File file = new File("histogramEmpty.csv");
		if(file.exists()) {
			file.delete();
		}
		HistogramQueryStatistic statistic = new HistogramQueryStatistic("histogramEmpty.csv");
		statistic.onFinishedTest();
		assertFalse(file.exists());
	}
	
	public void testGetCurrentHistogram() {
		HistogramQueryStatistic statistic = new HistogramQueryStatistic();
		statistic.onExecutedQuery(this.createQueryResponse(99), 0);
		statistic.onExecutedQuery(this.createQueryResponse(99), 0);
		statistic.onExecutedQuery(this.createQueryResponse(101), 0);
		statistic.onExecutedQuery(this.createQueryResponse(102), 0);
		statistic.onExecutedQuery(this.createQueryResponse(103), 0);
		statistic.onExecutedQuery(this.createQueryResponse(104), 0);
		statistic.onExecutedQuery(this.createQueryResponse(105), 0);
		statistic.onExecutedQuery(this.createQueryResponse(106), 0);
		statistic.onExecutedQuery(this.createQueryResponse(107), 0);
		statistic.onExecutedQuery(this.createQueryResponse(108), 0);
		statistic.onExecutedQuery(this.createQueryResponse(109), 0);
		statistic.onExecutedQuery(this.createQueryResponse(110), 0);
		statistic.onExecutedQuery(this.createQueryResponse(1001), 0);
		Map<Integer, Integer> histogram = statistic.getCurrentHisogram();
		assertNotNull(histogram);
		assertEquals(new Integer(2), histogram.get(0));
		assertEquals(new Integer(10), histogram.get(100));
		assertEquals(new Integer(1), histogram.get(1000));
		for(int i = 200; i < 1000; i+=100) {
			assertEquals(new Integer(0), histogram.get(i));
		}
		
	}
	
	@Override
	protected void tearDown() throws Exception {
		File file = new File("histogram.csv");
		if(file.exists()) {
			file.delete();
		}
	}

}
