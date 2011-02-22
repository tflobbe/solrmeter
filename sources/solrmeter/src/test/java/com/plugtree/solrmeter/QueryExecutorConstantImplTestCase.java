package com.plugtree.solrmeter;

import com.plugtree.solrmeter.mock.MockFieldExtractor;
import com.plugtree.solrmeter.mock.MockQueryExtractor;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.executor.QueryExecutorConstantImpl;

public class QueryExecutorConstantImplTestCase extends BaseTestCase {
	
	public void testLoadExtraParameters() {
		SolrMeterConfiguration.setProperty("solr.query.extraParameters", "indent=true, facet=false");
		QueryExecutorConstantImpl executor = new QueryExecutorConstantImpl(
				new MockFieldExtractor(), 
				new MockQueryExtractor(),
				new MockQueryExtractor(),
				new MockQueryExtractor());
		assertEquals(2, executor.getExtraParameters().size());
		assertEquals("true", executor.getExtraParameters().get("indent"));
		assertEquals("false", executor.getExtraParameters().get("facet"));
		
		SolrMeterConfiguration.setProperty("solr.query.extraParameters", "");
		executor = new QueryExecutorConstantImpl(
				new MockFieldExtractor(), 
				new MockQueryExtractor(),
				new MockQueryExtractor(),
				new MockQueryExtractor());
		assertEquals(0, executor.getExtraParameters().size());
	}
	
	@Override
	protected void tearDown() throws Exception {
		SolrMeterConfiguration.loadConfiguration();
	}

}
