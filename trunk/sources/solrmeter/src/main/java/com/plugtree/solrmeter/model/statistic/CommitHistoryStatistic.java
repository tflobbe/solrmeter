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

import java.util.Date;

import org.apache.solr.client.solrj.response.UpdateResponse;

import com.google.inject.Inject;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.model.UpdateStatistic;
import com.plugtree.solrmeter.model.exception.CommitException;
import com.plugtree.solrmeter.model.exception.UpdateException;

/**
 * Generate simple statistics for executed commits.
 * @author tflobbe
 *
 */
@StressTestScope
public class CommitHistoryStatistic implements UpdateStatistic {
	
	private Date lastCommitDate;
	
	private int totalCommits;
	
	private int totalAddedDocuments;
	
	private int commitErrorCount;
	
	private int updateErrorCount;
	
	@Inject
	public CommitHistoryStatistic() {
		super();
		totalAddedDocuments = 0;
		lastCommitDate = null;
		totalCommits = 0;
	}

	@Override
	public void onAddedDocument(UpdateResponse response) {
		totalAddedDocuments++;
	}

	@Override
	public void onCommit(UpdateResponse response) {
		lastCommitDate = new Date();
		totalCommits++;
	}

	@Override
	public void onFinishedTest() {
	}

	public Date getLastCommitDate() {
		return lastCommitDate;
	}

	public int getTotalCommits() {
		return totalCommits;
	}

	public int getTotalAddedDocuments() {
		return totalAddedDocuments;
	}

	@Override
	public void onAddError(UpdateException exception) {
		updateErrorCount++;
	}

	@Override
	public void onCommitError(CommitException exception) {
		commitErrorCount++;
		
	}

	public int getCommitErrorCount() {
		return commitErrorCount;
	}

	public int getUpdateErrorCount() {
		return updateErrorCount;
	}
	
	

}
