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
package com.linebee.solrmeter.view.settings;

import javax.swing.BoxLayout;

import com.linebee.solrmeter.controller.SettingsController;
import com.linebee.solrmeter.view.I18n;
import com.linebee.solrmeter.view.SettingsPanel;
import com.linebee.solrmeter.view.component.PropertyPanel;
import com.linebee.solrmeter.view.exception.InvalidPropertyException;
import com.linebee.solrmeter.view.listener.PropertyChangeListener;

public class QuerySettingsPanel extends SettingsPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 2171375149115069311L;
	
	private SettingsController controller;
	
	private boolean editable;
	
	public QuerySettingsPanel(SettingsController controller, boolean editable) {
		super();
		this.editable = editable;
		this.controller = controller;
		this.initGUI();
	}

	private void initGUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(new PropertyPanel(I18n.get("settings.query.solrUrl"), "solr.searchUrl", editable, this));
		this.add(new PropertyPanel(I18n.get("settings.query.useFacets"), "solr.query.useFacets", editable, this));
		this.add(new PropertyPanel(I18n.get("settings.query.fieldsFile"), "solr.documentFieldsFile", editable, this));
		this.add(new PropertyPanel(I18n.get("settings.query.useFilterQueries"), "solr.query.useFilterQueries", editable, this));
		this.add(new PropertyPanel(I18n.get("settings.query.facetMethod"), "solr.query.facetMethod", editable, this));
		this.add(new PropertyPanel(I18n.get("settings.query.filterQueryFile"), "solr.query.filterQueriesFile", editable, this));
		this.add(new PropertyPanel(I18n.get("settings.query.queryFile"), "solr.queriesFiles", editable, this));
		this.add(new PropertyPanel(I18n.get("settings.query.queryType"), "solr.search.queryType", editable, this));
		this.add(new PropertyPanel(I18n.get("settings.query.extraParameters"), "solr.query.extraParameters", editable, this));
		this.add(new PropertyPanel(I18n.get("settings.query.queryExecutor"), "executor.queryExecutor", editable, this));
		this.add(new PropertyPanel(I18n.get("settings.query.addRandomExtraParams"), "solr.query.addRandomExtraParams", editable, this));
		this.add(new PropertyPanel(I18n.get("settings.query.extraParams"), "solr.query.extraParams", editable, this));
		
	}

	@Override
	public String getSettingsName() {
		return I18n.get("settings.query.title");
	}

	@Override
	public void onPropertyChanged(String property, String text)
			throws InvalidPropertyException {
		controller.setProperty(property, text);
		
	}

}
