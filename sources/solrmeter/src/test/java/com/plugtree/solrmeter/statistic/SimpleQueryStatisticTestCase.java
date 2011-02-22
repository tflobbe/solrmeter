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
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.statistic.SimpleQueryStatistic;

public class SimpleQueryStatisticTestCase extends BaseTestCase {

	public void testExecutedQueries() {
		SimpleQueryStatistic statistic = new SimpleQueryStatistic();
		assertEquals(0, statistic.getTotalClientTime());
		assertEquals(0, statistic.getTotalQTime());
		assertEquals(0, statistic.getTotalErrors());
		assertEquals(0, statistic.getTotalQueries());
		statistic.onExecutedQuery(this.createQueryResponse(100), 150);
		assertEquals(150, statistic.getTotalClientTime());
		assertEquals(100, statistic.getTotalQTime());
		assertEquals(0, statistic.getTotalErrors());
		assertEquals(1, statistic.getTotalQueries());
		
		for(int i = 2; i < 20; i++) {
			statistic.onExecutedQuery(this.createQueryResponse(100), 150);
			assertEquals(i*150, statistic.getTotalClientTime());
			assertEquals(i*100, statistic.getTotalQTime());
			assertEquals(0, statistic.getTotalErrors());
			assertEquals(i, statistic.getTotalQueries());
		}
		
		statistic.onQueryError(new QueryException(""));
		assertEquals(1, statistic.getTotalErrors());
		assertEquals(19, statistic.getTotalQueries());
	}
}
