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
package com.plugtree.solrmeter.view.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.table.JTableHeader;

import org.apache.commons.csv.CSVUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.view.exception.InvalidPropertyException;
import com.plugtree.solrmeter.view.listener.PropertyChangeListener;

public class TablePropertyPanel extends PropertyPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6827283001774133026L;
	protected PropertiesTable table;
	protected JPanel pane;

	public TablePropertyPanel(String text, String property, boolean editable, PropertyChangeListener listener) {
		super(text, property, editable, listener);
		
		this.table = new PropertiesTable(new InnerTableListener(this), new HashMap<String, String>());
		this.table.setSorted(false);
		
		this.pane = new JPanel();
		
		JTableHeader header = this.table.getTableHeader();
		pane.setLayout(new BorderLayout());
		this.pane.add(header, BorderLayout.NORTH);
		this.pane.add(this.table, BorderLayout.CENTER);
		
		this.initGUI(text);
	}
	
	protected class InnerTableListener implements PropertyChangeListener {
		
		private TablePropertyPanel tablePanel;
		
		public InnerTableListener(TablePropertyPanel t){
			tablePanel = t;
		}
		
		@Override
		public void onPropertyChanged(String property, String text)
				throws InvalidPropertyException {
			tablePanel.notifyObservers();			
		}
		
	}
	
	@Override
	protected String getSelectedValue() {
		Map<String, String> properties = this.table.getProperties();
		List<String> propList = new ArrayList<String>(properties.size());
		
		for(String prop: this.table.getPropertiesNames()){
			String newProp = prop + "=" + (properties.get(prop)!=null?properties.get(prop):"");
			newProp = StringEscapeUtils.escapeCsv(newProp);
			propList.add(newProp);
		}
		
		return StringUtils.join(propList, ',');
	}

	@Override
	protected void setSelectedValue(String value) {
		this.table.removeAll();

		String[] values;
		try {
			values = CSVUtils.parseLine(value);

			for (String val : values) {
				val = StringEscapeUtils.unescapeCsv(val);
				String[] pair = val.split("=");
				if (pair.length == 2) {
					this.table.setProperty(pair[0].trim(), pair[1].trim());
				} else if (pair.length == 1) {
					this.table.setProperty(pair[0].trim(), "");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected Component getVisualComponent() {
		this.setSelectedValue(SolrMeterConfiguration.getProperty(property));
		return this.pane;
	}
	
	
}
