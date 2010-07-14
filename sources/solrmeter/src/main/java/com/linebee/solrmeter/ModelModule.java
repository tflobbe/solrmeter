package com.linebee.solrmeter;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.linebee.solrmeter.model.FieldExtractor;
import com.linebee.solrmeter.model.InputDocumentExtractor;
import com.linebee.solrmeter.model.OptimizeExecutor;
import com.linebee.solrmeter.model.QueryExecutor;
import com.linebee.solrmeter.model.QueryExtractor;
import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.model.UpdateExecutor;
import com.linebee.solrmeter.model.extractor.FileFieldExtractor;
import com.linebee.solrmeter.model.extractor.FileInputDocumentExtractor;
import com.linebee.solrmeter.model.extractor.FileQueryExtractor;

public class ModelModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(QueryExecutor.class);
		bind(UpdateExecutor.class);
		bind(OptimizeExecutor.class);
	}
	
	@Provides @Named("queryExtractor")
	public QueryExtractor createQueryExtractor() {
		return new FileQueryExtractor(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERIES_FILE_PATH));
	}
	
	@Provides @Named("filterQueryExtractor")
	public QueryExtractor createFilterQueryExtractor() {
		return new FileQueryExtractor(SolrMeterConfiguration.getProperty("solr.query.filterQueriesFile"));
	}
	
	@Provides
	public FieldExtractor createFieldExtractor() {
		return new FileFieldExtractor(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.FIELDS_FILE_PATH));
	}
	
	@Provides @Named("updateExtractor")
	public InputDocumentExtractor createInputExtractor() {
		return new FileInputDocumentExtractor(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.UPDATES_FILE_PATH));
	}

}
