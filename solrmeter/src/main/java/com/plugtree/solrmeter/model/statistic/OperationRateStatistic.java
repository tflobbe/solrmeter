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
package com.plugtree.solrmeter.model.statistic;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;

import com.plugtree.solrmeter.model.QueryStatistic;
import com.plugtree.solrmeter.model.UpdateStatistic;
import com.plugtree.solrmeter.model.exception.CommitException;
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.exception.UpdateException;

public class OperationRateStatistic implements UpdateStatistic, QueryStatistic {
	
	/**
	 * A queue that contains the times when the last queries were executed. 
	 * This queue is used to measure how many queries were executed during
	 * the last {@code period} seconds.
	 * 
	 * @see #getQueryRate()
	 */
	private Queue<Calendar> lastQueries = new LinkedList<Calendar>();
	
	/**
	 * A queue that contains the times when the last updates were executed. 
	 * This queue is used to measure how many updates were executed during
	 * the last {@code period} seconds.
	 * 
	 * @see #getUpdateRate()
	 */
	private Queue<Calendar> lastUpdates = new LinkedList<Calendar>();
	
	/**
	 * {@link #getQueryRate()} will return the number of queries executed in the last
	 * {@code period} seconds.
	 */
	private int period = 10;

	@Override
	public synchronized void onExecutedQuery(QueryResponse response, long clientTime) {
		this.lastQueries.add(new GregorianCalendar());
		this.flushOld(lastQueries);
	}

	@Override
	public synchronized void onQueryError(QueryException exception) {
		this.lastQueries.add(new GregorianCalendar());
		this.flushOld(lastQueries);
	}

	@Override
	public synchronized void onAddedDocument(UpdateResponse response) {
		this.lastUpdates.add(new GregorianCalendar());
		this.flushOld(lastUpdates);
	}

	@Override
	public void onCommit(UpdateResponse response) {

	}

	@Override
	public void onFinishedTest() {

	}

	@Override
	public void onCommitError(CommitException exception) {

	}

	@Override
	public synchronized void onAddError(UpdateException exception) {
		this.lastUpdates.add(new GregorianCalendar());
		this.flushOld(lastUpdates);
	}
	
	public void setPeriod(int period) {
		this.period = period;
	}
	
	/**
	 * Get the query rate in minutes
	 * 
	 * @return the number of queries executed during the last {@code period}
	 * seconds, divided by {@code period} and multiplied by {@code 60}.
	 */
	public synchronized double getQueryRate() {
		flushOld(lastQueries);
		return lastQueries.size()*60.0/period;
	}
	
	/**
	 * Get the update rate in minutes
	 * 
	 * @return the number of updates executed during the last {@code period}
	 * seconds, divided by {@code period} and multiplied by {@code 60}.
	 */
	public synchronized double getUpdateRate() {
		flushOld(lastUpdates);
		return lastUpdates.size()*60.0/period;
	}
	
	/**
	 * This method filters {@code q}, leaving only the items
	 * that were inserted during the last {@code period} seconds.
	 */
	private void flushOld(Queue<Calendar> q) {
		GregorianCalendar threshold = new GregorianCalendar();
		threshold.add(GregorianCalendar.SECOND, -period);
		
		while(q.peek()!=null && q.peek().before(threshold)) {
			q.poll();
		}
	}

}
