package com.plugtree.solrmeter.model.operations;



import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;

import junit.framework.TestCase;

import com.plugtree.solrmeter.model.operation.SolrQueryGenerator;

public class SolrQueryGeneratorTest extends TestCase{

	public void testShouldBreakTheStringInKeyValue() throws Exception {
		SolrQueryGenerator generator = new SolrQueryGenerator();
		
		String queryString = "formatter=html&hl.fragsize=0&q=*:*&h1.requireFieldMatch=true&hl.simple.pre=%3Cem+class%3D%22yellow_highlight%22%3E&hl.fl=content&hl=true&rows=5";
		List<String> params = generator.getParamsFrom(queryString);
		assertEquals(8, params.size());
		
		String secondQueryString = "formatter%3Dhtml%26hl.fragsize%3D0%26q%3D*%3A*%26h1.requireFieldMatch%3Dtrue%26hl.simple.pre%3D%253Cem%2Bclass%253D%2522yellow_highlight%2522%253E%26hl.fl%3Dcontent%26hl%3Dtrue%26rows%3D5";
		List<String> params2 = generator.getParamsFrom(secondQueryString);
		assertEquals(8, params2.size());
	}
	
	
	public void testShouldRemoveEmptyStringsOrWrongKeyValueFromParameterList() throws Exception {
		String queryString = "qt=/duplicate&&shards.qt=/duplicate";
		SolrQueryGenerator generator = new SolrQueryGenerator();
		List<String> list = generator.getParamsFrom(queryString);
		assertEquals(2, list.size());
	}
	
	public void testShouldBreakTheStringUsingASpecifiedChar() throws Exception {
		SolrQueryGenerator generator = new SolrQueryGenerator();
		String string = "formatter=html&hl.fragsize=0&sort=harvestDate desc";
		List<String> values = generator.split(string, "&");
		assertEquals(3, values.size());
		
		String string2 = "formatter=html";
		List<String> keyVal = generator.getKeyValuePair(string2);
		assertEquals(2, keyVal.size());
	}
	
	public void testShouldCreateASolrQueryStartingFromAString() throws Exception {
		String q= "q=*:*&rows=5";
		SolrQueryGenerator generator = new SolrQueryGenerator();
		SolrQuery query = generator.fromString(q);
		SolrQuery expected = new SolrQuery();
		expected.add("q", "*:*");
		expected.add("rows", "5");
		assertEquals(query.toString(), expected.toString());
		
	}
	
	
}
