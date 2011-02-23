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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JScrollPane;

import com.plugtree.solrmeter.controller.SettingsController;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.SettingsPanel;
import com.plugtree.solrmeter.view.component.PropertiesTable;
import com.plugtree.solrmeter.view.exception.InvalidPropertyException;
import com.plugtree.solrmeter.view.listener.PropertyChangeListener;

public class AdvancedSettingsPanel extends SettingsPanel implements PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7819886877217110778L;

	public AdvancedSettingsPanel(SettingsController settingsController, boolean editable) {
		super(settingsController);
		this.initGUI();
	}
	
	@Override
	public void onPropertyChanged(String property, String text)
			throws InvalidPropertyException {
		controller.setProperty(property, text);		
	}

	private void initGUI() {	
		PropertiesTable propertiesTable = new PropertiesTable(this);
		
		for(String property: propertiesTable){
			controller.addPropertyObserver(property, propertiesTable);			
		}
				
		propertiesTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
		propertiesTable.setFillsViewportHeight(true);

		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		
		int row = 0;
		constraints.gridx = 0;
		constraints.gridy = row;
		constraints.weighty = 1.0;
		constraints.weightx = 1.0;
		this.add(new JScrollPane(propertiesTable), constraints);
		row++;
				
	}
	
	@Override
	public String getSettingsName() {
		return I18n.get("settings.advanced.title");
	}

}
