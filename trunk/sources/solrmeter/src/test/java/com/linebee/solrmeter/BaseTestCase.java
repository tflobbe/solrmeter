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
package com.linebee.solrmeter;

import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.linebee.stressTestScope.StressTestRegistry;
import com.linebee.stressTestScope.StressTestScopeModule;

public abstract class BaseTestCase extends TestCase {
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	protected Injector injector;
	
	public BaseTestCase() {
		Properties props = new Properties();
		try {
			props.load(this.getClass().getClassLoader().getResourceAsStream("log4j-solrmeter-test.properties"));
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		PropertyConfigurator.configure(props);
		logger = Logger.getLogger(this.getClass());
		injector = createInjector();
	}
	
	private static Injector createInjector() {
		Injector injector = Guice.createInjector(
				Modules.override(new StatisticsModule()).with(new ModelTestModule()), 
				new ModelModule(),
				new StressTestScopeModule());
		StressTestRegistry.start();
		return injector;
	}
	
	protected void fail(Exception e) {
		logger.error(e);
		fail(e.getMessage());
	}
	
	/**
	 * Creates a query response
	 * @param qTime
	 * @return
	 */
	protected QueryResponse createQueryResponse(int qTime) {
		QueryResponse response = new QueryResponse();
		NamedList<Object> headerNamedList = new NamedList<Object>();
		headerNamedList.add("QTime", qTime);
		NamedList<Object> responseNamedList = new NamedList<Object>();
		responseNamedList.add("responseHeader", headerNamedList);
		
		NamedList<Object> paramsNamedList = new NamedList<Object>();
		paramsNamedList.add("q", "query");
		paramsNamedList.add("fq", "field:value");
		headerNamedList.add("params", paramsNamedList);
		
		SolrDocumentList resultsNamedList = new SolrDocumentList();
		resultsNamedList.add(new SolrDocument());
		resultsNamedList.add(new SolrDocument());
		resultsNamedList.add(new SolrDocument());
		resultsNamedList.setNumFound(10);
		responseNamedList.add("response", resultsNamedList);
		response.setResponse(responseNamedList);
		return response;
	}
	
	
	protected UpdateResponse createUpdateResponse(int qTime) {
		UpdateResponse response = new UpdateResponse();
		NamedList<Object> headerNamedList = new NamedList<Object>();
		headerNamedList.add("QTime", qTime);
		NamedList<Object> responseNamedList = new NamedList<Object>();
		responseNamedList.add("responseHeader", headerNamedList);
		response.setResponse(responseNamedList);
		return response;
	}
	
}
