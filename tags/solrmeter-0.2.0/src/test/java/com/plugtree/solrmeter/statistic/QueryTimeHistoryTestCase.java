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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.model.statistic.QueryTimeHistoryStatistic;
import com.plugtree.solrmeter.utils.QueryTimeHistoryStatisticTest;


public class QueryTimeHistoryTestCase extends BaseTestCase {
	
	public void testOneQuery() {
		QueryTimeHistoryStatisticTest statistic = new QueryTimeHistoryStatisticTest();
		Date date = new Date();
		statistic.setDateEvent(date);
		statistic.onExecutedQuery(this.createQueryResponse(10), 0);
		statistic.onFinishedTest();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("queryTime.csv")));
			assertEquals("0sec - 10sec;10", reader.readLine());
		} catch (FileNotFoundException e) {
			fail(e);
		} catch (IOException e) {
			fail(e);
		}
	}
	
	public void testManyQueriesEqualTimes() {
		QueryTimeHistoryStatisticTest statistic = new QueryTimeHistoryStatisticTest();
		Date date = new Date();
		statistic.setDateEvent(date);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onFinishedTest();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("queryTime.csv")));
			assertEquals("0sec - 10sec;100", reader.readLine());
		} catch (FileNotFoundException e) {
			fail(e);
		} catch (IOException e) {
			fail(e);
		}
	}
	
	public void testManyQueriesDiferentTimes() {
		QueryTimeHistoryStatisticTest statistic = new QueryTimeHistoryStatisticTest();
		Date date = new Date();
		statistic.setDateEvent(date);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(100), 0);
		statistic.onExecutedQuery(this.createQueryResponse(1000), 0);
		statistic.onExecutedQuery(this.createQueryResponse(1000), 0);
		statistic.onExecutedQuery(this.createQueryResponse(1000), 0);
		statistic.onExecutedQuery(this.createQueryResponse(1000), 0);
		statistic.onFinishedTest();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("queryTime.csv")));
			assertEquals("0sec - 10sec;550", reader.readLine());
		} catch (FileNotFoundException e) {
			fail(e);
		} catch (IOException e) {
			fail(e);
		}
	}
	
	public void testManyQueriesDiferentDates() {
		QueryTimeHistoryStatisticTest statistic = new QueryTimeHistoryStatisticTest();
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(0), 0);
		calendar.roll(Calendar.SECOND, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(1), 0);
		calendar.roll(Calendar.SECOND, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(2), 0);
		calendar.roll(Calendar.SECOND, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(3), 0);
		calendar.roll(Calendar.SECOND, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(4), 0);
		calendar.roll(Calendar.SECOND, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(6), 0);
		calendar.roll(Calendar.SECOND, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(7), 0);
		calendar.roll(Calendar.SECOND, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(8), 0);
		calendar.roll(Calendar.SECOND, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(9), 0);
		calendar.roll(Calendar.SECOND, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(10), 0);
		calendar.roll(Calendar.SECOND, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(10), 0);
		statistic.onFinishedTest();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("queryTime.csv")));
			assertEquals("0sec - 10sec;5", reader.readLine());
			assertEquals("10sec - 20sec;10", reader.readLine());
		} catch (FileNotFoundException e) {
			fail(e);
		} catch (IOException e) {
			fail(e);
		}
	}
	
	public void testManyQueriesDiferentDates2() {
		QueryTimeHistoryStatisticTest statistic = new QueryTimeHistoryStatisticTest();
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(0), 0);
		calendar.roll(Calendar.MINUTE, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(1), 0);
		calendar.roll(Calendar.MINUTE, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(2), 0);
		calendar.roll(Calendar.MINUTE, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(3), 0);
		calendar.roll(Calendar.MINUTE, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(4), 0);
		calendar.roll(Calendar.MINUTE, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(5), 0);
		calendar.roll(Calendar.MINUTE, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(6), 0);
		calendar.roll(Calendar.MINUTE, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(7), 0);
		calendar.roll(Calendar.MINUTE, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(8), 0);
		calendar.roll(Calendar.MINUTE, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(9), 0);
		calendar.roll(Calendar.MINUTE, true);
		statistic.setDateEvent(calendar.getTime());
		statistic.onExecutedQuery(this.createQueryResponse(10), 0);
		statistic.onFinishedTest();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("queryTime.csv")));
			for(int i = 0; i <= 600; i+=10) {
				if(i%60==0) {
					assertEquals("Error with i: " + i, i + "sec - " + (i+10) + "sec;" + ((int)i/60), reader.readLine());
				}else {
					assertEquals("Error with i: " + i, i + "sec - " + (i+10) + "sec;0", reader.readLine());
				}
			}
		} catch (FileNotFoundException e) {
			fail(e);
		} catch (IOException e) {
			fail(e);
		}
	}

	public void testEmpty() throws InterruptedException {
		File file = new File("queryTimeEmpty.csv");
		if(file.exists()) {
			file.delete();
		}
		QueryTimeHistoryStatistic statistic = new QueryTimeHistoryStatistic("queryTimeEmpty.csv");
		statistic.onFinishedTest();
		assertFalse(file.exists());
	}
	
	@Override
	protected void tearDown() throws Exception {
		File file = new File("queryTime.csv");
		if(file.exists()) {
			file.delete();
		}
	}

}
