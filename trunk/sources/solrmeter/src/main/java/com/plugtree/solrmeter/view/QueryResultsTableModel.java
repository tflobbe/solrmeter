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
package com.plugtree.solrmeter.view;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.solr.client.solrj.response.QueryResponse;

/**
 * Model for the query results panel of the QueryPanel
 * @author tflobbe
 *
 */
public class QueryResultsTableModel extends DefaultTableModel {
	
	private static final long serialVersionUID = -3029329874623226007L;
	private QueryResponse response;

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		if(response == null || response.getResults().size() == 0) {
			return 0;
		}
		return response.getResults().get(0).getFieldNames().size();
	}

	@Override
	public String getColumnName(int columnIndex) {
		if(response == null || response.getResults().size() == 0) {
			return "";
		}
		List<String> list = new LinkedList<String>(response.getResults().get(0).getFieldNames());
		return list.get(columnIndex);
	}

	@Override
	public int getRowCount() {
		if(response == null) {
			return 0;
		}
		return Math.min(Long.valueOf(response.getResults().getNumFound()).intValue(), response.getResults().size());
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String columnName = getColumnName(columnIndex);
		if(response.getHighlighting() != null && response.getHighlighting().get(columnName) != null) {
			System.out.println(response.getHighlighting().get(columnName));
		}
		return response.getResults().get(rowIndex).getFieldValue(columnName);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public QueryResponse getResponse() {
		return response;
	}

	public void setResponse(QueryResponse response) {
		this.response = response;
	}

}
