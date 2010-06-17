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
package com.linebee.solrmeter.model.statistic;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;

import com.linebee.solrmeter.model.OptimizeStatistic;
import com.linebee.solrmeter.model.QueryStatistic;
import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.model.UpdateStatistic;
import com.linebee.solrmeter.model.exception.CommitException;
import com.linebee.solrmeter.model.exception.OperationException;
import com.linebee.solrmeter.model.exception.OptimizeException;
import com.linebee.solrmeter.model.exception.QueryException;
import com.linebee.solrmeter.model.exception.UpdateException;
/**
 * Keeps statistics of the last ocurred errors and their timestamps
 * @author tflobbe
 *
 */
public class ErrorLogStatistic implements QueryStatistic, UpdateStatistic,
		OptimizeStatistic {
	
	private static int maxStored;
	
	private LinkedList<QueryException> queryExceptions;
	
	private LinkedList<UpdateException> updateExceptions;
	
	private LinkedList<CommitException> commitExceptions;
	
	private LinkedList<OptimizeException> optimizeExceptions;
	
	public ErrorLogStatistic() {
		queryExceptions = new LinkedList<QueryException>();
		updateExceptions = new LinkedList<UpdateException>();
		commitExceptions = new LinkedList<CommitException>();
		optimizeExceptions = new LinkedList<OptimizeException>();
		
	}
	

	@Override
	public void prepare() {
		maxStored = Integer.parseInt(SolrMeterConfiguration.getProperty("solr.errorLogStatistic.maxStored", "400"));
	}

	@Override
	public void onQueryError(QueryException exception) {
		addToList(queryExceptions, exception);

	}

	@Override
	public void onOptimizeError(OptimizeException exception) {
		addToList(optimizeExceptions, exception);
	}

	@Override
	public void onAddError(UpdateException exception) {
		addToList(updateExceptions, exception);
	}

	@Override
	public void onCommitError(CommitException exception) {
		addToList(commitExceptions, exception);
		
	}
	
	@Override
	public void onExecutedQuery(QueryResponse response, long clientTime) {}

	@Override
	public void onFinishedTest() {}

	@Override
	public void onAddedDocument(UpdateResponse response) {}

	@Override
	public void onCommit(UpdateResponse response) {}

	@Override
	public void onOptimizeFinished(long delay) {}

	@Override
	public void onOptimizeStared(long initTime) {}
	
	@SuppressWarnings("unchecked")
	private void addToList(LinkedList list, Object objectToAdd) {
		list.add(objectToAdd);
		if(list.size() > maxStored) {
			list.removeFirst();
		}
	}

	public List<OperationException> getLastErrors(boolean includeCommit, boolean includeOptimize, boolean includeQuery, boolean includeUpdate) {
		List<OperationException> list = new LinkedList<OperationException>();
		if(includeQuery) {
			list.addAll(queryExceptions);
		}
		if(includeUpdate) {
			list.addAll(updateExceptions);
		}
		if(includeCommit) {
			list.addAll(commitExceptions);
		}
		if(includeOptimize) {
			list.addAll(optimizeExceptions);
		}
		Collections.sort(list, new Comparator<OperationException>() {

			@Override
			public int compare(OperationException o1, OperationException o2) {
				return -1 * o1.getDate().compareTo(o2.getDate());
			}
			
		});
		return list;
	}

}
