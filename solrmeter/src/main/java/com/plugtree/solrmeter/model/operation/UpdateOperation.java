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
package com.plugtree.solrmeter.model.operation;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import com.plugtree.solrmeter.model.InputDocumentExtractor;
import com.plugtree.solrmeter.model.UpdateExecutor;
import com.plugtree.solrmeter.model.exception.UpdateException;
/**
 * Operation that executes a single update
 * @author tflobbe
 *
 */
public class UpdateOperation implements Operation {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private UpdateExecutor executor;
	
	private InputDocumentExtractor documentExtractor;
	
	public UpdateOperation(UpdateExecutor executor, InputDocumentExtractor documentExtractor) {
		super();
		this.executor = executor;
		this.documentExtractor = documentExtractor;
	}

	@Override
	public boolean execute() {
		SolrInputDocument updateDocument = documentExtractor.getRandomDocument();
		try {
			logger.debug("updating document " + updateDocument);
			UpdateResponse response = executor.getSolrServer().add(updateDocument);
			executor.notifyAddedDocument(response);
		} catch (IOException e) {
			logger.error(e);
			executor.notifyUpdateError(new UpdateException(e));
			return false;
		} catch (SolrServerException e) {
			logger.error(e);
			executor.notifyUpdateError(new UpdateException(e));
			return false;
		} catch (RuntimeException e) {
			logger.error(e);
			executor.notifyUpdateError(new UpdateException(e));
			throw e;
		}
		return true;
	}

}
