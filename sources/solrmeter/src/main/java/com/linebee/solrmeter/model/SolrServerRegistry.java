/**
 * Copyright Linebee. www.linebee.com
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
package com.linebee.solrmeter.model;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;

public class SolrServerRegistry {
	
	protected static Logger logger = Logger.getLogger(SolrServerRegistry.class);
	
	private static Map<String, CommonsHttpSolrServer> servers = new HashMap<String, CommonsHttpSolrServer>();

	public static synchronized CommonsHttpSolrServer getSolrServer(String url) {
		CommonsHttpSolrServer server = servers.get(url);
		if(server == null) {
			try {
				//TODO parametrize
				logger.info("Connecting to Solr: " + url);
				server = new CommonsHttpSolrServer(url);
				server.setSoTimeout(60000); // socket read timeout
				server.setConnectionTimeout(60000);
				server.setDefaultMaxConnectionsPerHost(100000);
				server.setMaxTotalConnections(1000000);
				server.setFollowRedirects(false); // defaults to false
				// allowCompression defaults to false.
				// Server side must support gzip or deflate for this to have any
				// effect.
				server.setAllowCompression(true);
				server.setMaxRetries(1); // defaults to 0. > 1 not recommended.
				logger.info("Connected OK");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return server;
	}
}
