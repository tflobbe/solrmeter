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
package com.plugtree.solrmeter.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.google.inject.Inject;
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.model.service.QueryService;
import com.plugtree.solrmeter.util.SolrMeterThreadFactory;
import com.plugtree.solrmeter.view.QueryPanel;

/**
 * Controller for the QueryPanel.
 * @author tflobbe
 *
 */
public class QueryPanelController {
    
    private final static Logger logger = Logger.getLogger(QueryPanelController.class);
    
    private final ExecutorService pool = Executors.newCachedThreadPool(new SolrMeterThreadFactory("query-panel-executor"));
	
	private QueryService service;
	
	private QueryPanel view;
	
	@Inject
	public QueryPanelController(QueryService service) {
		this.service = service;
	}
	
	public void executeQuery() {
	    pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
				    logger.info("Executing Query");
					QueryResponse response = service.executeQuery(view.getQ(), 
							view.getFQ(), 
							view.getQT(), 
							view.getHighlight(), 
							view.getFacetFields(), 
							view.getSort(), 
							view.getSortOrder().toString(), 
							view.getRows(),
							view.getStart(),
							view.getOtherParams());
					view.showResults(response);
				} catch (QueryException e) {
					view.showError(e);
				}
			}
		});
		
	}



	public QueryPanel getView() {
		return view;
	}



	public void setView(QueryPanel view) {
		this.view = view;
	}

}
