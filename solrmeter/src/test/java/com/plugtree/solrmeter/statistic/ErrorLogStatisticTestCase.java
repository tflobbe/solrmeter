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

import java.util.List;

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.model.exception.CommitException;
import com.plugtree.solrmeter.model.exception.OperationException;
import com.plugtree.solrmeter.model.exception.OptimizeException;
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.exception.UpdateException;
import com.plugtree.solrmeter.model.statistic.ErrorLogStatistic;

public class ErrorLogStatisticTestCase extends BaseTestCase {
	
	public void testErrorCount() {
		ErrorLogStatistic statistic = new ErrorLogStatistic();
		assertEquals(0, statistic.getLastErrors(true, true, true, true).size());
		statistic.onAddError(new UpdateException());
		assertEquals(1, statistic.getLastErrors(true, true, true, true).size());
		statistic.onAddError(new UpdateException());
		assertEquals(2, statistic.getLastErrors(true, true, true, true).size());
		assertEquals(2, statistic.getLastErrors(false, true, true, true).size());
		assertEquals(2, statistic.getLastErrors(true, false, true, true).size());
		assertEquals(2, statistic.getLastErrors(true, true, false, true).size());
		assertEquals(0, statistic.getLastErrors(true, true, true, false).size());
		
		statistic.onQueryError(new QueryException());
		statistic.onQueryError(new QueryException());
		assertEquals(4, statistic.getLastErrors(true, true, true, true).size());
		assertEquals(4, statistic.getLastErrors(false, true, true, true).size());
		assertEquals(4, statistic.getLastErrors(true, false, true, true).size());
		assertEquals(2, statistic.getLastErrors(true, true, false, true).size());
		assertEquals(2, statistic.getLastErrors(true, true, true, false).size());
		
		statistic.onCommitError(new CommitException());
		statistic.onCommitError(new CommitException());
		statistic.onOptimizeError(new OptimizeException());
		statistic.onOptimizeError(new OptimizeException());
		
		assertEquals(8, statistic.getLastErrors(true, true, true, true).size());
		assertEquals(6, statistic.getLastErrors(false, true, true, true).size());
		assertEquals(6, statistic.getLastErrors(true, false, true, true).size());
		assertEquals(6, statistic.getLastErrors(true, true, false, true).size());
		assertEquals(6, statistic.getLastErrors(true, true, true, false).size());
		assertEquals(0, statistic.getLastErrors(false, false, false, false).size());
		assertEquals(4, statistic.getLastErrors(true, true, false, false).size());
		assertEquals(4, statistic.getLastErrors(false, false, true, true).size());
	}
	
	public void testMaxSoredErrors() {
		ErrorLogStatistic statistic = new ErrorLogStatistic();
		for(int i = 0; i < 400; i++) {//using max number of stored strings = 400
			statistic.onAddError(new UpdateException());
		}
		assertEquals(400, statistic.getLastErrors(true, true, true, true).size());
		for(int i = 0; i < 10; i++) {
			statistic.onAddError(new UpdateException());
		}
		assertEquals(400, statistic.getLastErrors(true, true, true, true).size());
		for(int i = 0; i < 400; i++) {//using max number of stored strings = 400
			statistic.onQueryError(new QueryException());
		}
		assertEquals(800, statistic.getLastErrors(true, true, true, true).size());
		for(int i = 0; i < 10; i++) {
			statistic.onQueryError(new QueryException());
		}
		assertEquals(800, statistic.getLastErrors(true, true, true, true).size());
	}
	
	public void testSort() throws InterruptedException {
		ErrorLogStatistic statistic = new ErrorLogStatistic();
		for(int i = 0; i < 10; i++) {//using max number of stored strings = 400
			statistic.onAddError(new UpdateException(String.valueOf(i)));
			statistic.onQueryError(new QueryException(String.valueOf(i)));
			Thread.sleep(50);
		}
		List<OperationException> operations = statistic.getLastErrors(true, true, true, true);
		OperationException lastOperation = null;
		for(OperationException operation:operations) {
			if(lastOperation != null) {
				assertTrue(Integer.valueOf(operation.getMessage()).compareTo(Integer.valueOf(lastOperation.getMessage())) <= 0);
			}
			lastOperation = operation;
		}
		
	}

}
