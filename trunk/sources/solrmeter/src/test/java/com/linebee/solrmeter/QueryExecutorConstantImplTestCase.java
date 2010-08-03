package com.linebee.solrmeter;

import com.linebee.solrmeter.mock.MockFieldExtractor;
import com.linebee.solrmeter.mock.MockQueryExtractor;
import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.model.executor.QueryExecutorConstantImpl;

public class QueryExecutorConstantImplTestCase extends BaseTestCase {
	
	public void testLoadExtraParameters() {
		SolrMeterConfiguration.setProperty("solr.query.extraParameters", "indent=true, facet=false");
		QueryExecutorConstantImpl executor = new QueryExecutorConstantImpl(
				new MockFieldExtractor(), 
				new MockQueryExtractor(),
				new MockQueryExtractor());
		assertEquals(2, executor.getExtraParameters().size());
		assertEquals("true", executor.getExtraParameters().get("indent"));
		assertEquals("false", executor.getExtraParameters().get("facet"));
		
		SolrMeterConfiguration.setProperty("solr.query.extraParameters", "");
		executor = new QueryExecutorConstantImpl(
				new MockFieldExtractor(), 
				new MockQueryExtractor(),
				new MockQueryExtractor());
		assertEquals(0, executor.getExtraParameters().size());
	}
	
	@Override
	protected void tearDown() throws Exception {
		SolrMeterConfiguration.loadDefatultConfiguration();
	}

}
