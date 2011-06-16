package com.plugtree.solrmeter.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.model.statistic.QueryLogStatistic;
import com.plugtree.solrmeter.model.statistic.QueryLogStatistic.QueryLogValue;

public class FullQueryStatisticControllerTestCase extends BaseTestCase {
	
	private static final String TMP_FILE_PREFIX = 
		"solrmeter-FullQueryStatisticControllerTestCase-testExportOneQuery";
	
	private static final String TMP_FILE_SUFFIX = 
		".tmp";
	
	private static final String LINE_TEMPLATE =
		"\"%b\",\"%s\",\"%s\",\"%s\",\"%d\",\"%d\"";
	
	private File temp;
	
	@Override
	protected void setUp() throws Exception {
		temp = File.createTempFile(TMP_FILE_PREFIX, TMP_FILE_SUFFIX);
	}
	
	@Override
	protected void tearDown() throws Exception {
		if(temp!=null) {
			temp.delete();
		}
	}
	
	public void testExportOneQuery() throws IOException {
		QueryLogStatistic stat = mock(QueryLogStatistic.class);
		LinkedList<QueryLogValue> values = new LinkedList<QueryLogValue>();
		QueryLogValue value = newMockQueryLogValue(false, "queryString", 
				"facetQueryString", "filterQueryString", 123, 456l);
		
		values.add(value);
		
		when(stat.getLastQueries()).thenReturn(values);
		
		FullQueryStatisticController controller = new FullQueryStatisticController(stat);
		controller.writeCSV(temp);
		
		String csv = FileUtils.readFileToString(temp);
		csv = StringUtils.chop(csv);
		
		assertEquals(getLineFor(value), csv);
	}
	
	public void testExportManyQueries() throws IOException {
		QueryLogStatistic stat = mock(QueryLogStatistic.class);
		LinkedList<QueryLogValue> values = new LinkedList<QueryLogValue>();
		
		// generates ten queries
		for(int i=0;i<10;i++) {
			QueryLogValue value = newMockQueryLogValue(
					i%2==0, 
					"queryString" + i,
					"facetQueryString" + i,
					"filterQueryString" + i,
					i*10, 
					i*10+1l);
			values.add(value);
		}
		
		when(stat.getLastQueries()).thenReturn(values);
		
		FullQueryStatisticController controller = new FullQueryStatisticController(stat);
		controller.writeCSV(temp);
		
		String csv = FileUtils.readFileToString(temp);
		String[] records = csv.split("\r|\n");
		
		int i=0;
		for(String record: records) {
			assertEquals(getLineFor(values.get(i++)), record);
		}
	}
	
	public void testExportWithQuotes() throws IOException {
		QueryLogStatistic stat = mock(QueryLogStatistic.class);
		LinkedList<QueryLogValue> values = new LinkedList<QueryLogValue>();
		QueryLogValue value = newMockQueryLogValue(false, "queryString", 
				"facetQueryString", "filterQueryString", 123, 456l);
		
		values.add(value);
		when(stat.getLastQueries()).thenReturn(values);
		
		FullQueryStatisticController controller = new FullQueryStatisticController(stat);
		controller.writeCSV(temp);
		
		String csv = FileUtils.readFileToString(temp);
		csv = StringUtils.chop(csv);
		
		assertEquals(getLineFor(value), csv);
	}
	
	private QueryLogValue newMockQueryLogValue(boolean isError, String queryString,
			String facetQueryString, String filterQueryString, Integer qTime,
			Long results) {
		QueryLogValue value = mock(QueryLogValue.class);
		when(value.isError()).thenReturn(isError);
		when(value.getQueryString()).thenReturn(queryString);
		when(value.getFacetQueryString()).thenReturn(facetQueryString);
		when(value.getFilterQueryString()).thenReturn(filterQueryString);
		when(value.getQTime()).thenReturn(qTime);
		when(value.getResults()).thenReturn(results);
		return value;
	}
	
	private String getLineFor(QueryLogValue value) {
		return String.format(LINE_TEMPLATE, value.isError(), value.getQueryString(),
				value.getFacetQueryString(), value.getFilterQueryString(),
				value.getQTime(), value.getResults());
	}

}
