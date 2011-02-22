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

import java.math.BigDecimal;
import java.util.Date;

import org.apache.log4j.Logger;

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.statistic.FullQueryStatistic;

public class FullQueryStatisticTestCase extends BaseTestCase {

	public void testLastErrorDate() throws InterruptedException {
		FullQueryStatistic statistic = new FullQueryStatistic();
		QueryException exception = new QueryException();
		statistic.onQueryError(exception);
		Thread.sleep(200);
		assertEquals(exception.getDate(), statistic.getLastErrorTime());
	}
	
	public void testEmptyStatistic() {
		FullQueryStatistic statistic = new FullQueryStatistic();
		assertEquals(0.0, statistic.getMedian());
		assertEquals(0.0, statistic.getVariance());
		assertEquals(new Integer(-1), statistic.getMode());
		assertEquals(new Integer(-1), statistic.getTotaAverage());
	}
	/**
	 * 1, 2, 3. Median should be 2
	 */
	public void testMedian1() {
		FullQueryStatistic statistic = new FullQueryStatistic();
		statistic.onExecutedQuery(this.createQueryResponse(1),1);
		statistic.onExecutedQuery(this.createQueryResponse(2),1);
		statistic.onExecutedQuery(this.createQueryResponse(3),1);
		assertEquals(2.0, statistic.getMedian());
	}
	
	/**
	 * 3, 2, 1. Median should be 2
	 */
	public void testMedian2() {
		FullQueryStatistic statistic = new FullQueryStatistic();
		statistic.onExecutedQuery(this.createQueryResponse(3),1);
		statistic.onExecutedQuery(this.createQueryResponse(2),1);
		statistic.onExecutedQuery(this.createQueryResponse(1),1);
		assertEquals(2.0, statistic.getMedian());
	}
	
	public void testMedian3() {
		FullQueryStatistic statistic = new FullQueryStatistic();
		statistic.onExecutedQuery(this.createQueryResponse(10),1);
		statistic.onExecutedQuery(this.createQueryResponse(20),1);
		statistic.onExecutedQuery(this.createQueryResponse(60),1);
		assertEquals(20.0, statistic.getMedian());
	}
	
	public void testMedian4() {
		FullQueryStatistic statistic = new FullQueryStatistic();
		statistic.onExecutedQuery(this.createQueryResponse(10),1);
		statistic.onExecutedQuery(this.createQueryResponse(20),1);
		assertEquals(15.0, statistic.getMedian());
	}
	
	public void testMedian5() {
		FullQueryStatistic statistic = new FullQueryStatistic();
		statistic.onExecutedQuery(this.createQueryResponse(1),1);
		statistic.onExecutedQuery(this.createQueryResponse(2),1);
		statistic.onExecutedQuery(this.createQueryResponse(3),1);
		statistic.onExecutedQuery(this.createQueryResponse(4),1);
		statistic.onExecutedQuery(this.createQueryResponse(5),1);
		statistic.onExecutedQuery(this.createQueryResponse(6),1);
		statistic.onExecutedQuery(this.createQueryResponse(7),1);
		statistic.onExecutedQuery(this.createQueryResponse(8),1);
		assertEquals(4.5, statistic.getMedian());
	}
	
	public void testMedian6() {
		FullQueryStatistic statistic = new FullQueryStatistic();
		statistic.onExecutedQuery(this.createQueryResponse(1),1);
		statistic.onExecutedQuery(this.createQueryResponse(1),1);
		statistic.onExecutedQuery(this.createQueryResponse(1),1);
		statistic.onExecutedQuery(this.createQueryResponse(1),1);
		statistic.onExecutedQuery(this.createQueryResponse(10),1);
		statistic.onExecutedQuery(this.createQueryResponse(11),1);
		statistic.onExecutedQuery(this.createQueryResponse(12),1);
		statistic.onExecutedQuery(this.createQueryResponse(13),1);
		assertEquals(5.5, statistic.getMedian());
	}
	
	/**
	 * This is not actually a test that will ever fail. It's just to try median algorithm speed
	 */
	public void testMedianManyValues() {
		medianWith(1000);
		medianWith(10000);
		medianWith(100000);
		medianWith(1000000);
	}
	
	private void medianWith(int cantItems) {
		FullQueryStatistic statistic = new FullQueryStatistic();
		for(int i = 0; i < cantItems; i++) {
			statistic.onExecutedQuery(this.createQueryResponse((int)(Math.random() * 500)),1);
		}
		long init = new Date().getTime();
		statistic.getMedian();
		Logger.getLogger(this.getClass()).info("Obtained the Median of " + String.valueOf(cantItems) + " values in " + String.valueOf(new Date().getTime() - init) + "ms");
		init = new Date().getTime();
		statistic.getMedian();
		Logger.getLogger(this.getClass()).info("Obtained the Median of " + String.valueOf(cantItems) + " values for second time in " + String.valueOf(new Date().getTime() - init) + "ms");
		
	}
	
	public void testMode1() {
		FullQueryStatistic statistic = new FullQueryStatistic();
		statistic.onExecutedQuery(this.createQueryResponse(1),1);
		statistic.onExecutedQuery(this.createQueryResponse(1),1);
		statistic.onExecutedQuery(this.createQueryResponse(2),1);
		statistic.onExecutedQuery(this.createQueryResponse(3),1);
		statistic.onExecutedQuery(this.createQueryResponse(4),1);
		assertEquals(new Integer(1), statistic.getMode());
		statistic.onExecutedQuery(this.createQueryResponse(2),1);
		assertEquals(new Integer(1), statistic.getMode());
		statistic.onExecutedQuery(this.createQueryResponse(2),1);
		assertEquals(new Integer(2), statistic.getMode());
		for (int i = 0; i < 5; i++) {
			statistic.onExecutedQuery(this.createQueryResponse(12),1);
		}
		assertEquals(new Integer(12), statistic.getMode());
	}
	
	public void testMode2() {
		FullQueryStatistic statistic = new FullQueryStatistic();
		statistic.onExecutedQuery(this.createQueryResponse(4),1);
		statistic.onExecutedQuery(this.createQueryResponse(4),1);
		statistic.onExecutedQuery(this.createQueryResponse(3),1);
		statistic.onExecutedQuery(this.createQueryResponse(2),1);
		statistic.onExecutedQuery(this.createQueryResponse(1),1);
		assertEquals(new Integer(4), statistic.getMode());
		statistic.onExecutedQuery(this.createQueryResponse(2),1);
		assertEquals(new Integer(2), statistic.getMode());
		statistic.onExecutedQuery(this.createQueryResponse(2),1);
		assertEquals(new Integer(2), statistic.getMode());
		for (int i = 0; i < 5; i++) {
			statistic.onExecutedQuery(this.createQueryResponse(12),1);
		}
		assertEquals(new Integer(12), statistic.getMode());
	}
	
	public void testMultipleMode() {
		doTestMode(new int[]{1, 2, 2, 3, 4, 7, 9}, 2);
		doTestMode(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 9, 8, 7, 6, 5, 4, 3, 2, 2, 2, 3}, 2);
	}
	
	public void testModeSpeed() {
		modeWith(1000);
		modeWith(10000);
		modeWith(100000);
		modeWith(1000000);
	}
	
	private void doTestMode(int[] nums, int mode) {
		FullQueryStatistic statistic = new FullQueryStatistic();
		for(int num:nums) {
			statistic.onExecutedQuery(this.createQueryResponse(num),1);
		}
		assertEquals(new Integer(mode), statistic.getMode());
	}
	
	private void modeWith(int cantItems) {
		FullQueryStatistic statistic = new FullQueryStatistic();
		for(int i = 0; i < cantItems; i++) {
			statistic.onExecutedQuery(this.createQueryResponse((int)(Math.random() * 500)),1);
		}
		long init = new Date().getTime();
		statistic.getMode();
		Logger.getLogger(this.getClass()).info("Obtained the Mode of " + String.valueOf(cantItems) + " values in " + String.valueOf(new Date().getTime() - init) + "ms");
		init = new Date().getTime();
		statistic.getMode();
		Logger.getLogger(this.getClass()).info("Obtained the Mode of " + String.valueOf(cantItems) + " values for second time in " + String.valueOf(new Date().getTime() - init) + "ms");
	}
	
	public void testVariance() {
		assertEquals(0.0, getVariance(new int[]{0}));
		assertEquals(1.0, getVariance(new int[]{0, 2}));
		assertEquals(0.25, getVariance(new int[]{0, 1}));
		assertEquals(0.13, new BigDecimal(getVariance(new int[]{0, 1, 1, 1, 1, 1})).setScale(2,BigDecimal.ROUND_FLOOR).doubleValue());
		assertEquals(0.66, new BigDecimal(getVariance(new int[]{0, 1, 2})).setScale(2,BigDecimal.ROUND_FLOOR).doubleValue());
		assertEquals(2.91, new BigDecimal(getVariance(new int[]{1, 2, 3, 4, 5, 6})).setScale(2,BigDecimal.ROUND_FLOOR).doubleValue());
	}
	
	public void testVarianceSpeed() {
		varianceWith(1000);
		varianceWith(10000);
		varianceWith(100000);
		varianceWith(1000000);
	}
	
	private Double getVariance(int[] nums) {
		FullQueryStatistic statistic = new FullQueryStatistic();
		for(int num:nums) {
			statistic.onExecutedQuery(this.createQueryResponse(num),1);
		}
		return statistic.getVariance();
	}
	
	private void varianceWith(int cantItems) {
		FullQueryStatistic statistic = new FullQueryStatistic();
		for(int i = 0; i < cantItems; i++) {
			statistic.onExecutedQuery(this.createQueryResponse((int)(Math.random() * 500)),1);
		}
		long init = new Date().getTime();
		statistic.getVariance();
		Logger.getLogger(this.getClass()).info("Obtained the Variance of " + String.valueOf(cantItems) + " values in " + String.valueOf(new Date().getTime() - init) + "ms");
		init = new Date().getTime();
		statistic.getVariance();
		Logger.getLogger(this.getClass()).info("Obtained the Variance of " + String.valueOf(cantItems) + " values for second time in " + String.valueOf(new Date().getTime() - init) + "ms");
	}
	
	public void testStandardDeviation() {
		assertEquals(0.0, getStandardDeviation(new int[]{0}));
		assertEquals(1.0, getStandardDeviation(new int[]{0, 2}));
		assertEquals(0.5, getStandardDeviation(new int[]{0, 1}));
		assertEquals(0.37, new BigDecimal(getStandardDeviation(new int[]{0, 1, 1, 1, 1, 1})).setScale(2,BigDecimal.ROUND_FLOOR).doubleValue());
		assertEquals(0.81, new BigDecimal(getStandardDeviation(new int[]{0, 1, 2})).setScale(2,BigDecimal.ROUND_FLOOR).doubleValue());
		assertEquals(1.7, new BigDecimal(getStandardDeviation(new int[]{1, 2, 3, 4, 5, 6})).setScale(2,BigDecimal.ROUND_FLOOR).doubleValue());
	}
	
	private Double getStandardDeviation(int[] nums) {
		FullQueryStatistic statistic = new FullQueryStatistic();
		for(int num:nums) {
			statistic.onExecutedQuery(this.createQueryResponse(num),1);
		}
		return statistic.getStandardDeviation();
	}
	
	public void testTotalAverage() {
		assertEquals(new Integer(0), getTotalAverage(new int[]{0}));
		assertEquals(new Integer(1), getTotalAverage(new int[]{1}));
		assertEquals(new Integer(5), getTotalAverage(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
		assertEquals(new Integer(200), getTotalAverage(new int[]{100, 200, 300}));
	}
	
	private Integer getTotalAverage(int[] nums) {
		FullQueryStatistic statistic = new FullQueryStatistic();
		for(int num:nums) {
			statistic.onExecutedQuery(this.createQueryResponse(num),1);
		}
		return statistic.getTotaAverage();
	}
	
	public void testAverageSince() throws InterruptedException {
		FullQueryStatistic statistic = new FullQueryStatistic();
		Date initDate = new Date();
		statistic.onExecutedQuery(this.createQueryResponse(100), 1);
		Thread.sleep(200);
		Date secondDate = new Date();
		statistic.onExecutedQuery(this.createQueryResponse(10), 1);
		Thread.sleep(200);
		Date thirdDate = new Date();
		statistic.onExecutedQuery(this.createQueryResponse(2), 1);
		
		assertEquals(statistic.getTotaAverage(), statistic.getAverageSince(initDate));
		assertEquals(new Integer(37), statistic.getAverageSince(initDate));
		assertEquals(new Integer(6), statistic.getAverageSince(secondDate));
		assertEquals(new Integer(2), statistic.getAverageSince(thirdDate));
		Thread.sleep(200);
		assertEquals(new Integer(-1), statistic.getAverageSince(new Date()));
	}
}
