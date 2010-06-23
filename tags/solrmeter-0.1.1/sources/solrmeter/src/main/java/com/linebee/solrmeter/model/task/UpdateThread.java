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

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import com.linebee.solrmeter.model.UpdateExecutor;
import com.linebee.solrmeter.model.exception.UpdateException;


public class UpdateThread extends AbstractOperationThread {
	
	private UpdateExecutor executor;
	
	public UpdateThread(UpdateExecutor executor, long updateInterval) {
		super(updateInterval);
		this.executor = executor;
	}

	@Override
	protected void executeOperation() {
		SolrInputDocument updateDocument = executor.getNextDocument();
		try {
			logger.debug("updating document " + updateDocument);
			UpdateResponse response = executor.getSolrServer().add(updateDocument);
			executor.notifyAddedDocument(response);
		} catch (IOException e) {
			logger.error(e);
			executor.notifyUpdateError(new UpdateException(e));
		} catch (SolrServerException e) {
			logger.error(e);
			executor.notifyUpdateError(new UpdateException(e));
		} catch (RuntimeException e) {
			logger.error(e);
			executor.notifyUpdateError(new UpdateException(e));
			throw e;
		}
	}

}
