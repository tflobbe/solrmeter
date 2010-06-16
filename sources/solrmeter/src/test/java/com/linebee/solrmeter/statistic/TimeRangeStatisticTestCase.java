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
package com.linebee.solrmeter.statistic;

import com.linebee.solrmeter.BaseTestCase;
import com.linebee.solrmeter.model.statistic.TimeRange;
import com.linebee.solrmeter.model.statistic.TimeRangeStatistic;

public class TimeRangeStatisticTestCase extends BaseTestCase {
	
	private TimeRange range0_500;
	
	private TimeRange range501_1000;
	
	private TimeRange range1001_2000;
	
	private TimeRange rangeMoreThan2000;
	
	@Override
	protected void setUp() throws Exception {
		range0_500 = new TimeRange(0, 500);
		range501_1000 = new TimeRange(501, 1000);
		range1001_2000 = new TimeRange(1001, 2000);
		rangeMoreThan2000 = new TimeRange(2001);
	}

	public void test() {
		TimeRangeStatistic statistic = new TimeRangeStatistic();
		statistic.prepare();
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
}
