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
package com.linebee.solrmeter.controller;

import java.awt.Window;
import java.util.Properties;

import com.linebee.solrmeter.SolrMeterMain;
import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.view.SettingsPanelContainer;

public class SettingsController {
	
	private Properties changedProps;
	
	private SettingsPanelContainer panel;
	
	private Window window;
	
	public SettingsController(SettingsPanelContainer settingsPanelContainer,
			Window parent) {
		super();
		changedProps = new Properties();
		panel = settingsPanelContainer;
		window = parent;
	}

	public void cancel() {
		changedProps = null;
		window.dispose();
	}
	
	public void acceptChanges() {
		applyChanges();
		window.dispose();
	}
	
	public void applyChanges() {
		for(String propertyKey:changedProps.stringPropertyNames()) {
			SolrMeterConfiguration.setProperty(propertyKey, changedProps.getProperty(propertyKey));
		}
		if(!changedProps.isEmpty()) {
			SolrMeterMain.restartApplication();
		}
		changedProps = new Properties();
		panel.hasChangedValues(false);
	}

	public void setProperty(String property, String value) {
		changedProps.put(property, value);
		panel.hasChangedValues(true);
	}

}
