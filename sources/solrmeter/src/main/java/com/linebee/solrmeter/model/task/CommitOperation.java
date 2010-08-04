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
package com.linebee.solrmeter.model.task;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;

import com.linebee.solrmeter.model.UpdateExecutor;
import com.linebee.solrmeter.model.exception.CommitException;

/**
 * Operation that executes a single commit
 * @author tflobbe
 *
 */
public class CommitOperation implements Operation {
	
	private UpdateExecutor executor;
	
	public CommitOperation(UpdateExecutor executor) {
		this.executor = executor;
	}

	@Override
	public void execute() {
		Logger.getLogger(this.getClass()).info("commiting");
		try {
			UpdateResponse response = executor.getSolrServer().commit();
			Logger.getLogger(this.getClass()).info("Commit OK");
			executor.notifyCommitSuccessfull(response);
		} catch (SolrServerException e) {
			Logger.getLogger(this.getClass()).error("Error on commiter thread", e);
			executor.notifyCommitError(new CommitException(e));
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error("Error on commiter thread", e);
			executor.notifyCommitError(new CommitException(e));
		}
	}

}
