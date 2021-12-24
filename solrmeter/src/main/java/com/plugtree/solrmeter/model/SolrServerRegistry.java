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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
/**
 * This registry holds all the created solr servers. It will be one for each different url
 * and it wont change between tests.
 * @author tflobbe
 *
 */
public class SolrServerRegistry {

	protected static final Logger logger = Logger.getLogger(SolrServerRegistry.class);

	private static final Map<String, HttpSolrClient> servers = new HashMap<String, HttpSolrClient>();

	public static synchronized SolrClient getSolrServer(String url) {
		SolrClient server = servers.get(url);
		if(server == null) {
			logger.info("Connecting to Solr: " + url);
			String user = SolrMeterConfiguration.getProperty("solr.server.configuration.httpAuthUser");
			String pass = SolrMeterConfiguration.getProperty("solr.server.configuration.httpAuthPass");
			HttpSolrClient client;

			HttpClientBuilder builder = HttpClientBuilder.create();
			if (StringUtils.isNotEmpty(user) && StringUtils.isNotEmpty(pass)) {
				UsernamePasswordCredentials creds = new UsernamePasswordCredentials(user, pass);
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(
						AuthScope.ANY,
						creds);

				builder.addInterceptorFirst(new PreemptiveAuthInterceptor());
				builder.setDefaultCredentialsProvider(credsProvider);
			}

			CloseableHttpClient httpClient = builder.build();

			client = new HttpSolrClient.Builder().withBaseSolrUrl(url)
					.withHttpClient(httpClient)
					.build();

			servers.put(url, client);
			return client;

		}
		return server;
	}

	private static void setAuthentication(HttpSolrClient httpServer) {
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
		for(SolrClient server:servers.values()) {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		servers.clear();
	}
}
