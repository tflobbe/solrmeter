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
package com.linebee.solrmeter.model.extractor;

import java.util.List;

import com.linebee.solrmeter.model.FileUtils;
import com.linebee.solrmeter.model.QueryExtractor;
import com.linebee.solrmeter.model.SolrMeterConfiguration;

/**
 * A QueryExtractor that extract the possible queries from a text file
 * @author Tomás
 *
 */
public class FileQueryExtractor implements QueryExtractor {
	
	/**
	 * List of available Queries. The Queries are extracted from the queries file.
	 */
	protected List<String> queries;
	
	public FileQueryExtractor() {
		this(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.QUERIES_FILE_PATH));
	}
	
	public FileQueryExtractor(String filePath) {
		super();
		loadQueries(filePath);
	}
	
	/**
	 * Load all queries from the queries file.
	 */
	protected void loadQueries(String filePath) {
		queries = FileUtils.loadStringsFromFile(filePath);
	}

	@Override
	public String getRandomQuery() {
		return (String)FileUtils.getNextRandomObject(queries);
	}
	
}
