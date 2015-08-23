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
package com.plugtree.solrmeter.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
/**
 * This registry holds all the created solr servers. It will be one for each different url 
 * and it wont change between tests.
 * @author tflobbe
 *
 */
public class SolrServerRegistry {

	protected static final Logger logger = Logger.getLogger(SolrServerRegistry.class);

	private static final Map<String, SolrServer> servers = new HashMap<String, SolrServer>();

	public static synchronized SolrServer getSolrServer(String url) {
		SolrServer server = servers.get(url);
		if(server == null) {
			logger.info("Connecting to Solr: " + url);
			HttpSolrServer httpServer = new HttpSolrServer(url);
			httpServer.setSoTimeout(Integer.parseInt(SolrMeterConfiguration.getProperty("solr.server.configuration.soTimeout", "60000"))); // socket read timeout
			httpServer.setConnectionTimeout(Integer.parseInt(SolrMeterConfiguration.getProperty("solr.server.configuration.connectionTimeout", "60000")));
			httpServer.setDefaultMaxConnectionsPerHost(Integer.parseInt(SolrMeterConfiguration.getProperty("solr.server.configuration.defaultMaxConnectionsPerHost", "100000")));
			httpServer.setMaxTotalConnections(Integer.parseInt(SolrMeterConfiguration.getProperty("solr.server.configuration.maxTotalConnections", "1000000")));
			httpServer.setFollowRedirects(Boolean.parseBoolean(SolrMeterConfiguration.getProperty("solr.server.configuration.followRedirect", "false"))); // defaults to false
			httpServer.setAllowCompression(Boolean.parseBoolean(SolrMeterConfiguration.getProperty("solr.server.configuration.allowCompression", "true")));
			httpServer.setMaxRetries(Integer.parseInt(SolrMeterConfiguration.getProperty("solr.server.configuration.maxRetries", "1"))); // defaults to 0. > 1 not recommended.
			setAuthentication(httpServer);
			servers.put(url, httpServer);
			return httpServer;

		}
		return server;
	}

	private static void setAuthentication(HttpSolrServer httpServer) {
		String user = SolrMeterConfiguration.getProperty("solr.server.configuration.httpAuthUser");
		String pass = SolrMeterConfiguration.getProperty("solr.server.configuration.httpAuthPass");
		if(user != null && !user.isEmpty() && pass != null && !pass.isEmpty()) {
			AbstractHttpClient client = (AbstractHttpClient) httpServer.getHttpClient();
			client.addRequestInterceptor(new PreEmptiveBasicAuthenticator(user, pass));
		}
	}

	/**
	 * Drops all existing SolrServers
	 */
	public static void invalidate() {
		for(SolrServer server:servers.values()) {
			if(server instanceof HttpSolrServer) {
				((HttpSolrServer) server).shutdown();
			}
		}
		servers.clear();
	}
}
