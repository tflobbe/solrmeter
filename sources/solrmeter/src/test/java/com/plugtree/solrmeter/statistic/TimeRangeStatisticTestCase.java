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

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.statistic.TimeRange;
import com.plugtree.solrmeter.model.statistic.TimeRangeStatistic;

public class TimeRangeStatisticTestCase extends BaseTestCase {
	
	private TimeRange range0_500;
	
	private TimeRange range501_1000;
	
	private TimeRange range1001_2000;
	
	private TimeRange rangeMoreThan2000;
	
	@Override
	protected void setUp() throws Exception {
		SolrMeterConfiguration.loadDefaultConfiguration();
		range0_500 = new TimeRange(0, 500);
		range501_1000 = new TimeRange(501, 1000);
		range1001_2000 = new TimeRange(1001, 2000);
		rangeMoreThan2000 = new TimeRange(2001);
	}

	public void test() {
		TimeRangeStatistic statistic = new TimeRangeStatistic(true);
		assertTrue(statistic.getActualPercentage().isEmpty());
		statistic.onExecutedQuery(createQueryResponse(0), 0);
		this.assertAll(statistic, 100, 0, 0, 0);
		statistic.onExecutedQuery(createQueryResponse(502), 0);
		this.assertAll(statistic, 50, 50, 0, 0);
		statistic.onExecutedQuery(createQueryResponse(0), 0);
		this.assertAll(statistic, 66, 33, 0, 0);
		statistic.onExecutedQuery(createQueryResponse(502), 0);
		this.assertAll(statistic, 50, 50, 0, 0);
		statistic.onExecutedQuery(createQueryResponse(1002), 0);
		statistic.onExecutedQuery(createQueryResponse(1002), 0);
		statistic.onExecutedQuery(createQueryResponse(2002), 0);
		statistic.onExecutedQuery(createQueryResponse(2002), 0);
		this.assertAll(statistic, 25, 25, 25, 25);
	}

	private void assertAll(TimeRangeStatistic statistic, int value0_500, int value5001_1000, int value1001_2000, int valueMoreThan2000) {
		assertEquals(new Integer(value0_500), statistic.getActualPercentage().get(range0_500));
		assertEquals(new Integer(value5001_1000), statistic.getActualPercentage().get(range501_1000));
		assertEquals(new Integer(value1001_2000), statistic.getActualPercentage().get(range1001_2000));
		assertEquals(new Integer(valueMoreThan2000), statistic.getActualPercentage().get(rangeMoreThan2000));
	}
	
	public void testAddRange() {
		TimeRangeStatistic statistic = new TimeRangeStatistic();
		assertEquals(0, statistic.getCounterCount());
		statistic.addNewRange(0, 100);
		assertEquals(1, statistic.getCounterCount());
		statistic.addNewRange(101, 200);
		assertEquals(2, statistic.getCounterCount());
		statistic.addNewRange(201, Integer.MAX_VALUE);
		assertEquals(3, statistic.getCounterCount());
		assertEquals("true", SolrMeterConfiguration.getProperty("statistic.timeRange.range0_100"));
		assertEquals("true", SolrMeterConfiguration.getProperty("statistic.timeRange.range101_200"));
		assertEquals("true", SolrMeterConfiguration.getProperty("statistic.timeRange.range201_" + Integer.MAX_VALUE));
	}
	
	public void testRemoveRange() {
		TimeRangeStatistic statistic = new TimeRangeStatistic(true);
		assertEquals(4, statistic.getCounterCount());
		assertEquals("true", SolrMeterConfiguration.getProperty("statistic.timeRange.range0_500"));
		assertEquals("true", SolrMeterConfiguration.getProperty("statistic.timeRange.range501_1000"));
		assertEquals("true", SolrMeterConfiguration.getProperty("statistic.timeRange.range1001_2000"));
		assertEquals("true", SolrMeterConfiguration.getProperty("statistic.timeRange.range2001_" + Integer.MAX_VALUE));
		statistic.removeRange(0, 500);
		assertNull(SolrMeterConfiguration.getProperty("statistic.timeRange.range0_500"));
		assertEquals(3, statistic.getCounterCount());
		
		statistic.removeRange(1001, 2000);
		assertNull(SolrMeterConfiguration.getProperty("statistic.timeRange.range1001_2000"));
		assertEquals(2, statistic.getCounterCount());
		
		statistic.removeRange(501, 1000);
		assertNull(SolrMeterConfiguration.getProperty("statistic.timeRange.range501_1000"));
		assertEquals(1, statistic.getCounterCount());
		
		statistic.removeRange(2001, Integer.MAX_VALUE);
		assertNull(SolrMeterConfiguration.getProperty("statistic.timeRange.range2001_" + Integer.MAX_VALUE));
		assertEquals(0, statistic.getCounterCount());
		
		try {
			statistic.removeRange(0, 1);
			fail("Exception expected");
		}catch(RuntimeException expectedException) {
			//expected
		}
		
	}
	
	public void testOverlap() {
		TimeRangeStatistic statistic = new TimeRangeStatistic(true);
		assertFalse(statistic.overlap());
		statistic.addNewRange(0, Integer.MAX_VALUE);
		assertTrue(statistic.overlap());
		statistic.removeRange(0, 500);
		statistic.removeRange(501, 1000);
		statistic.removeRange(1001, 2000);
		statistic.removeRange(2001, Integer.MAX_VALUE);
		assertFalse(statistic.overlap());
		statistic.removeRange(0, Integer.MAX_VALUE);
		assertFalse(statistic.overlap());
		statistic.addNewRange(0, 100);
		statistic.addNewRange(101, 200);
		statistic.addNewRange(201, Integer.MAX_VALUE);
		assertFalse(statistic.overlap());
		statistic.removeRange(201, Integer.MAX_VALUE);
		assertFalse(statistic.overlap());
		statistic.addNewRange(200, Integer.MAX_VALUE);
		assertTrue(statistic.overlap());
		
	}
	
	public void testRestart() {
		TimeRangeStatistic statistic = new TimeRangeStatistic(true);
		assertTrue(statistic.getActualPercentage().isEmpty());
		statistic.onExecutedQuery(createQueryResponse(0), 0);
		statistic.onExecutedQuery(createQueryResponse(502), 0);
		statistic.onExecutedQuery(createQueryResponse(0), 0);
		statistic.onExecutedQuery(createQueryResponse(502), 0);
		statistic.onExecutedQuery(createQueryResponse(1002), 0);
		statistic.onExecutedQuery(createQueryResponse(1002), 0);
		statistic.onExecutedQuery(createQueryResponse(2002), 0);
		statistic.onExecutedQuery(createQueryResponse(2002), 0);
		this.assertAll(statistic, 25, 25, 25, 25);
		statistic.restartCounter();
		statistic.onExecutedQuery(createQueryResponse(0), 0);
		this.assertAll(statistic, 100, 0, 0, 0);
	}
	
	public void testAddConfigurationRanges() {
		SolrMeterConfiguration.setProperty("statistic.timeRange.range0_500", "true");
		SolrMeterConfiguration.setProperty("statistic.timeRange.range501_1000", "true");
		SolrMeterConfiguration.setProperty("statistic.timeRange.range2001_2000", "true");
		SolrMeterConfiguration.setProperty("statistic.timeRange.range2001_" + Integer.MAX_VALUE, "true");
		TimeRangeStatistic statistic = new TimeRangeStatistic();
		assertEquals(4, statistic.getCounterCount());
		
	}
	
	@Override
	protected void tearDown() throws Exception {
		SolrMeterConfiguration.loadConfiguration();
	}
	
}
