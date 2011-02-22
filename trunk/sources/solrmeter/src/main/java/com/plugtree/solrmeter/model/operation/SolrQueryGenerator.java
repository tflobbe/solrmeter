package com.plugtree.solrmeter.model.operation;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;

public class SolrQueryGenerator {

	public List<String> getParamsFrom(String queryString) throws UnsupportedEncodingException{
		List<String> values = new ArrayList<String>();
		for (String element : split(queryString, "&")) {
			if( ! element.isEmpty() && element.contains("=")){
				values.add(element);
			}
		}
		return values;
	}

	public List<String> getKeyValuePair(String queryString) throws UnsupportedEncodingException{
		return split(queryString, "=");
	}
	
	public List<String> split(String queryString, String separator) throws UnsupportedEncodingException {
		queryString = URLDecoder.decode(queryString, "UTF-8");
		String[] strings = queryString.split(separator);
		List<String> params = Arrays.asList(strings);
		return params;
	}

	public SolrQuery fromString(String queryString) {
		SolrQuery query =new SolrQuery();
		try {
			List<String> paramsFrom = getParamsFrom(queryString);
			for(String param : paramsFrom){
				List<String> keyValuePair = getKeyValuePair(param);
				String key = keyValuePair.get(0);
				String val = keyValuePair.get(1);
				query.add(key, val);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return query;
	}
	
	
	
}
