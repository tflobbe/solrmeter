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
import com.plugtree.solrmeter.mock.OperationTimeHistorySpy;
import com.plugtree.solrmeter.model.statistic.OperationTimeHistory;

public class OperationTimeHistoryTestCase extends BaseTestCase {

	public void testAdd() throws InterruptedException {
		OperationTimeHistory statistic = new OperationTimeHistory();
		assertTrue(statistic.getCommitTime().isEmpty());
		assertTrue(statistic.getUpdatesTime().isEmpty());
		assertTrue(statistic.getOptimizeTime().isEmpty());
		assertTrue(statistic.getQueriesTime().isEmpty());
		
		statistic.onExecutedQuery(this.createQueryResponse(100), 150L);
		assertEquals(1, statistic.getQueriesTime().size());
		Thread.sleep(100);
		statistic.onExecutedQuery(this.createQueryResponse(200), 200L);
		assertEquals(2, statistic.getQueriesTime().size());
	}
	
	public void testAddSameKey() throws InterruptedException {
		OperationTimeHistory statistic = new OperationTimeHistorySpy();
		assertTrue(statistic.getCommitTime().isEmpty());
		assertTrue(statistic.getUpdatesTime().isEmpty());
		assertTrue(statistic.getOptimizeTime().isEmpty());
		assertTrue(statistic.getQueriesTime().isEmpty());
		
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		assertEquals(1, statistic.getQueriesTime().size());
		assertEquals(Long.valueOf(100), (Long)statistic.getQueriesTime().get(100L));
		statistic.onExecutedQuery(this.createQueryResponse(200), 0);
		assertEquals(1, statistic.getQueriesTime().size());
		assertEquals(Long.valueOf(150), (Long)statistic.getQueriesTime().get(100L));
		statistic.onExecutedQuery(this.createQueryResponse(300), 0);
		assertEquals(1, statistic.getQueriesTime().size());
		assertEquals(Long.valueOf(200), (Long)statistic.getQueriesTime().get(100L));
		statistic.onExecutedQuery(this.createQueryResponse(600), 0);
		assertEquals(1, statistic.getQueriesTime().size());
		assertEquals(Long.valueOf(300), (Long)statistic.getQueriesTime().get(100L));
	}
	
	
}
