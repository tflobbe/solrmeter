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
package com.plugtree.solrmeter;

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
import com.plugtree.stressTestScope.StressTestRegistry;
import com.plugtree.stressTestScope.StressTestScopeModule;
import com.plugtree.solrmeter.ModelModule;
import com.plugtree.solrmeter.StandalonePresentationModule;
import com.plugtree.solrmeter.StatisticsModule;

public abstract class BaseTestCase extends TestCase {
	
	protected final Logger logger = Logger.getLogger(this.getClass());
	
	protected Injector injector;
	
	public BaseTestCase() {
		Properties props = new Properties();
		try {
			props.load(this.getClass().getClassLoader().getResourceAsStream("log4j-solrmeter-test.properties"));
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		PropertyConfigurator.configure(props);
		injector = createInjector();
	}
	
	private static Injector createInjector() {
		Injector injector = Guice.createInjector(
				Modules.override(new StatisticsModule()).with(new ModelTestModule()), 
				new ModelModule(),
				new StandalonePresentationModule(),
				new StressTestScopeModule());
		StressTestRegistry.start();
		return injector;
	}
	
	protected void fail(Exception e) {
		logger.error(e.getMessage(), e);
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
	
	protected void assertEquals(String[] array1, String[] array2) {
		if(array1.length != array2.length) {
			fail("Array1 has " + array1.length + " objects and array 2 has " + array2.length);
		}
		for(int i = 0; i < array1.length;i++) {
			if(!array1[i].equals(array2[i])) {
				fail("Element " + i + " is '" + array1[i] + "' for array1 and '" + array2[i] + "' for array 2.");
			}
		}
	}
	
	protected void sleep(long delay) {
		long now = System.currentTimeMillis();
		long wakeUp = now + delay;
		while(now<wakeUp) {
			try {
				Thread.sleep(wakeUp - now);
			} catch(InterruptedException ex) {
				
			}
			now = System.currentTimeMillis();
		}
	}
	
}
