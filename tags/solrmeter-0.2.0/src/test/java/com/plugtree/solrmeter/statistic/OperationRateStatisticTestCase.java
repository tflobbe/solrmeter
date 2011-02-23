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
import com.plugtree.solrmeter.model.exception.UpdateException;
import com.plugtree.solrmeter.model.statistic.OperationRateStatistic;

public class OperationRateStatisticTestCase extends BaseTestCase {

	public void testQueryRate() {
		OperationRateStatistic statistic = new OperationRateStatistic();
		statistic.setPeriod(1);
		
		assertEquals(0.0, statistic.getQueryRate());
		
		statistic.onExecutedQuery(this.createQueryResponse(1), 1);
		assertEquals(1*60.0, statistic.getQueryRate());
		
		statistic.onQueryError(new QueryException());
		assertEquals(2*60.0, statistic.getQueryRate());
		
		sleep(600);
		assertEquals(2*60.0, statistic.getQueryRate());
		
		statistic.onExecutedQuery(this.createQueryResponse(1), 1);
		assertEquals(3*60.0, statistic.getQueryRate());
		
		sleep(600);
		assertEquals(1*60.0, statistic.getQueryRate());
		
		statistic.onExecutedQuery(this.createQueryResponse(1), 1);
		assertEquals(2*60.0, statistic.getQueryRate());
		
		sleep(600);
		assertEquals(1*60.0, statistic.getQueryRate());
		
		sleep(600);
		assertEquals(0*60.0, statistic.getQueryRate());
	}
	
	public void testUpdateRate() {
		OperationRateStatistic statistic = new OperationRateStatistic();
		statistic.setPeriod(1);
		
		assertEquals(0*60.0, statistic.getUpdateRate());
		
		statistic.onAddedDocument(this.createUpdateResponse(1));
		assertEquals(1*60.0, statistic.getUpdateRate());
		
		statistic.onAddError(new UpdateException());
		assertEquals(2*60.0, statistic.getUpdateRate());
		
		sleep(600);
		assertEquals(2*60.0, statistic.getUpdateRate());
		
		statistic.onAddedDocument(this.createUpdateResponse(1));
		assertEquals(3*60.0, statistic.getUpdateRate());
		
		sleep(600);
		assertEquals(1*60.0, statistic.getUpdateRate());
		
		statistic.onAddedDocument(this.createUpdateResponse(1));
		assertEquals(2*60.0, statistic.getUpdateRate());
		
		sleep(600);
		assertEquals(1*60.0, statistic.getUpdateRate());
		
		sleep(600);
		assertEquals(0*60.0, statistic.getUpdateRate());
	}
}
