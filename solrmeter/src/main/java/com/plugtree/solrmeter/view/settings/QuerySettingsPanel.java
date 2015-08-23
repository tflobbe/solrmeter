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
package com.plugtree.solrmeter.view.settings;

import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;

import com.plugtree.solrmeter.controller.SettingsController;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.SettingsPanel;
import com.plugtree.solrmeter.view.component.*;
import com.plugtree.solrmeter.view.exception.InvalidPropertyException;
import com.plugtree.solrmeter.view.listener.PropertyChangeListener;

public class QuerySettingsPanel extends SettingsPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 2171375149115069311L;
	
	private boolean editable;
	
	public QuerySettingsPanel(SettingsController controller, boolean editable) {
		super(controller);
		this.editable = editable;
		this.initGUI();
	}

	private void initGUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		QueryModeComboBoxPropertyPanel comboBoxPropertyPanel = new QueryModeComboBoxPropertyPanel(I18n.get("settings.query.chooseQueryMode"), "solr.query.queryMode", editable, this);
		final PropertyPanel solrSearchUrl = new TextPropertyPanel(I18n.get("settings.query.solrUrl"), "solr.searchUrl", editable, this);
		final PropertyPanel useFacetComponent = new BooleanPropertyPanel(I18n.get("settings.query.useFacets"), "solr.query.useFacets", editable, this);
		final PropertyPanel fieldsFilePath = new FilePropertyPanel(I18n.get("settings.query.fieldsFile"), "solr.documentFieldsFile", editable, this);
		final PropertyPanel useFilterQueries = new BooleanPropertyPanel(I18n.get("settings.query.useFilterQueries"), "solr.query.useFilterQueries", editable, this);
		final PropertyPanel facetMethod = new ComboPropertyPanel(I18n.get("settings.query.facetMethod"), "solr.query.facetMethod", editable, this, new String[]{"fc", "enum"}, false);
		final PropertyPanel filterQueryFilePath = new FilePropertyPanel(I18n.get("settings.query.filterQueryFile"), "solr.query.filterQueriesFile", editable, this);
		final PropertyPanel queryFilePath = new FilePropertyPanel(I18n.get("settings.query.queryFile"), "solr.queriesFiles", editable, this);
		final PropertyPanel queryType = new TextPropertyPanel(I18n.get("settings.query.queryType"), "solr.search.queryType", editable, this);
        final PropertyPanel echoParams = new BooleanPropertyPanel(I18n.get("settings.query.forceEchoParams"), "solr.query.echoParams", editable, this);
        //	final PropertyPanel useExtraParameters = new TextPropertyPanel(I18n.get("settings.query.extraParameters"), "solr.query.extraParameters", editable, this);
		final PropertyPanel useExtraParameters = new TablePropertyPanel(I18n.get("settings.query.extraParameters"), "solr.query.extraParameters", editable, this);
		final PropertyPanel queryExecutor = new ComboPropertyPanel(I18n.get("settings.query.queryExecutor"), "executor.queryExecutor", editable, this, new String[]{"random", "constant"}, true);
		final PropertyPanel addExternalRandomParameters = new BooleanPropertyPanel(I18n.get("settings.query.addRandomExtraParams"), "solr.query.addRandomExtraParams", editable, this);
		final PropertyPanel extraParameters = new FilePropertyPanel(I18n.get("settings.query.extraParams"), "solr.query.extraParams", editable, this);


		this.add(comboBoxPropertyPanel);
		this.add(solrSearchUrl);
		this.add(useFacetComponent);
		this.add(fieldsFilePath);
		this.add(useFilterQueries);
		this.add(facetMethod);
		this.add(filterQueryFilePath);
		this.add(queryFilePath);
		this.add(queryType);
        this.add(echoParams);
		this.add(useExtraParameters);
		this.add(queryExecutor);
		this.add(addExternalRandomParameters);
		this.add(extraParameters);
		this.add(Box.createVerticalGlue());

		comboBoxPropertyPanel.setDependantProperties(Arrays.asList(useFacetComponent, fieldsFilePath, useFilterQueries, facetMethod, filterQueryFilePath, queryType, useExtraParameters, addExternalRandomParameters, extraParameters ));
		
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
