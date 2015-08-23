package com.plugtree.solrmeter.model.generator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.solr.client.solrj.SolrQuery;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.plugtree.solrmeter.model.FieldExtractor;
import com.plugtree.solrmeter.model.QueryExtractor;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;

public class ComplexQueryGenerator implements QueryGenerator {
  
  
  private static Integer facetMinCount = Integer.valueOf(SolrMeterConfiguration.getProperty("solr.query.facet.minCount", "1"));
  
  private static Integer facetLimit = Integer.valueOf(SolrMeterConfiguration.getProperty("solr.query.facet.limit", "8"));
  
  /**
   * If set, strings are executed adding random felds as facet.
   */
  private String queryType = SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERY_TYPE);
  
  private boolean useFacets = Boolean.valueOf(SolrMeterConfiguration.getProperty("solr.query.useFacets", "true"));
  
  private String facetMethod = SolrMeterConfiguration.getProperty("solr.query.facetMethod");
  
  private boolean useFilterQueries = Boolean.valueOf(SolrMeterConfiguration.getProperty("solr.query.useFilterQueries", "true"));

  private boolean forceEchoParamsAll = Boolean.valueOf(SolrMeterConfiguration.getProperty("solr.query.echoParams", "false"));

  private boolean addRandomExtraParams = Boolean.valueOf(SolrMeterConfiguration.getProperty("solr.query.addRandomExtraParams", "true"));
  
  /**
   * The facet fields extractor
   */
  private FieldExtractor facetFieldExtractor;
  
  /**
   * The filter query extractor
   */
  private QueryExtractor filterQueryExtractor;
  
  /**
   * The standard query extractor
   */
  private QueryExtractor queryExtractor;
  
  private QueryExtractor extraParameterExtractor;
  
  protected Map<String, String> extraParameters;
  
  
  @Inject
  public ComplexQueryGenerator(FieldExtractor facetFieldExtractor,
      @Named("filterQueryExtractor") QueryExtractor filterQueryExtractor,
      @Named("queryExtractor") QueryExtractor queryExtractor,
      @Named("extraParamExtractor")QueryExtractor extraParamExtractor) {
    this.queryExtractor = queryExtractor;
    this.filterQueryExtractor = filterQueryExtractor;
    this.facetFieldExtractor = facetFieldExtractor;
    this.extraParameterExtractor = extraParamExtractor;
    this.loadExtraParameters(SolrMeterConfiguration.getProperty("solr.query.extraParameters", ""));
    
  }
  
  protected ComplexQueryGenerator(){
    
  }
  
  
  protected void loadExtraParameters(String property) {
    extraParameters = new HashMap<String, String>();
    
    if(property == null || "".equals(property.trim())) {
      return;
    }
    
    String[] values;
    try {
      values = CSVUtils.parseLine(property);
      
      for (String val : values) {
        val = StringEscapeUtils.unescapeCsv(val);
        
        int equalSignIndex = val.indexOf("=");
        if(equalSignIndex > 0) {
          extraParameters.put(val.substring(0, equalSignIndex).trim(), val.substring(equalSignIndex + 1).trim());
        }
      }
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  
  @Override
  public SolrQuery generate() {
    SolrQuery query;
    query = new SolrQuery();
    query.setQuery(queryExtractor.getRandomQuery());
    query.setQueryType(queryType);
    this.addExtraParameters(query);
    query.setIncludeScore(true);
    
    if(useFacets) {
      addFacetParameters(query);
    }
    if(useFilterQueries) {
      addFilterQueriesParameters(query);
    }
    if(addRandomExtraParams) {
      this.addRandomExtraParameters(query);
    }
    if(forceEchoParamsAll){
        query.add("echoParams", "all");
    }

    return query;
  }
  
  /**
   * Adds extra (not specific) parameters of query
   * @param query
   */
  private void addExtraParameters(SolrQuery query) {
    for(String paramKey:extraParameters.keySet()) {
      query.add(paramKey, extraParameters.get(paramKey));
    }
  }
  
  /**
   * Adds a random line of the extra parameters extractor
   * @param query
   */
  private void addRandomExtraParameters(SolrQuery query) {
    String randomExtraParam = extraParameterExtractor.getRandomQuery();
    if(randomExtraParam == null || "".equals(randomExtraParam.trim())) {
      return;
    }
    for(String param:randomExtraParam.split("&")) {//TODO parametrize
      int equalSignIndex = param.indexOf("=");
      if(equalSignIndex > 0) {
        query.add(param.substring(0, equalSignIndex).trim(), param.substring(equalSignIndex + 1).trim());
      }
    }
  }
  
  private void addFilterQueriesParameters(SolrQuery query) {
    String filterQString = filterQueryExtractor.getRandomQuery();
    if(!"".equals(filterQString.trim())) {
      query.addFilterQuery(filterQString);
    }
  }
  
  private void addFacetParameters(SolrQuery query) {
    query.setFacet(true);
    query.addFacetField(facetFieldExtractor.getRandomFacetField());
    query.setFacetMinCount(facetMinCount);
    query.setFacetLimit(facetLimit);
    if(facetMethod != null && !"".equals(facetMethod)) {
      query.add("facet.method", facetMethod);
    }
    
  }
  
  public boolean isUseFacets() {
    return useFacets;
  }
  
  public void setUseFacets(boolean useFacets) {
    this.useFacets = useFacets;
  }
}
