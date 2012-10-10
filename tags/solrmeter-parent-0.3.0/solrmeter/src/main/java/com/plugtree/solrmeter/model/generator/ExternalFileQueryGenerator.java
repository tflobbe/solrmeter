package com.plugtree.solrmeter.model.generator;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.plugtree.solrmeter.model.QueryExtractor;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import org.apache.solr.client.solrj.SolrQuery;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExternalFileQueryGenerator implements QueryGenerator {

  private QueryExtractor queryExtractor;

    private boolean forceEchoParamsAll = Boolean.valueOf(SolrMeterConfiguration.getProperty("solr.query.echoParams", "false"));

  @Inject
  public ExternalFileQueryGenerator(@Named("queryExtractor") QueryExtractor queryExtractor) {
    this.queryExtractor = queryExtractor;
  }

  protected ExternalFileQueryGenerator() {
  }

  @Override
  public SolrQuery generate() {
    String randomQuery = queryExtractor.getRandomQuery();
    return fromString(randomQuery);
  }


  protected List<String> getParamsFrom(String queryString) throws UnsupportedEncodingException{
    List<String> values = new ArrayList<String>();
    for (String element : split(queryString, "&")) {
      if( ! element.isEmpty() && element.contains("=")){
        values.add(element);
      }
    }
    return values;
  }

  public List<String> getKeyValuePair(String queryString) throws UnsupportedEncodingException{
    queryString = URLDecoder.decode(queryString, "UTF-8");
    List<String> params = new ArrayList<String>(2);
    int index= queryString.indexOf("=");
    if(index < 1) {
      params.add(queryString);
    } else {
      params.add(queryString.substring(0, index));
      params.add(queryString.substring(index + 1 , queryString.length()));
    }
    return params;
  }

  protected List<String> split(String queryString, String separator) throws UnsupportedEncodingException {
    queryString = URLDecoder.decode(queryString, "UTF-8");
    String[] strings = queryString.split(separator);
    List<String> params = Arrays.asList(strings);
    return params;
  }

  protected  SolrQuery fromString(String queryString) {
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

    if(forceEchoParamsAll){
      query.remove("echoParams");
      query.add("echoParams", "all");
    }

    return query;
  }




}
