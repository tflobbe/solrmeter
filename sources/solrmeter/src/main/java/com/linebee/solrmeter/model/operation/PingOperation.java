package com.linebee.solrmeter.model.operation;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.SolrPingResponse;

/**
 * Operation that executes one ping to a SolrServer
 * @author tflobbe
 *
 */
public class PingOperation implements Operation {
	
	private SolrServer server;
	
	public PingOperation(SolrServer server) {
		this.server = server;
	}

	@Override
	public boolean execute() {
		try {
			SolrPingResponse response = server.ping();
			if(response.getStatus() == 0) {
				return true;
			}
		} catch (SolrServerException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return false;
	}

}
