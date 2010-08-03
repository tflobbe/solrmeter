package com.linebee.solrmeter.model.task;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import com.linebee.solrmeter.model.UpdateExecutor;
import com.linebee.solrmeter.model.exception.UpdateException;

public class UpdateOperation implements Operation {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private UpdateExecutor executor;
	
	public UpdateOperation(UpdateExecutor executor) {
		super();
		this.executor = executor;
	}

	@Override
	public void execute() {
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
