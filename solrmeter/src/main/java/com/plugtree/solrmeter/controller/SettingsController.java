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

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;
import com.plugtree.solrmeter.SolrMeterMain;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.SolrServerRegistry;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.SettingsPanelContainer;
import com.plugtree.solrmeter.view.SolrPropertyObserver;

@Singleton
public class SettingsController {
	
	private Properties changedProps;
		
	private SettingsPanelContainer panel;
	
	private Map<String, Set<SolrPropertyObserver>> observerMap;
	
	private Window window;
	
	public SettingsController(SettingsPanelContainer settingsPanelContainer,
			Window parent) {
		super();
		changedProps = new Properties();
		panel = settingsPanelContainer;
		window = parent;
		observerMap = new HashMap<String, Set<SolrPropertyObserver>>();
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
			SolrServerRegistry.invalidate();
			SolrMeterMain.restartApplication();
		}
		changedProps = new Properties();
		panel.hasChangedValues(false);
	}

	public void setProperty(String property, String value) {
		changedProps.put(property, value);
		notifyObservers(property, value);
		panel.hasChangedValues(true);
	}

	public void okAndSetDefault() {
		applyChanges();
		
		int optionResultPane = JOptionPane.showConfirmDialog(SolrMeterMain.mainFrame, 
				I18n.get("settings.file.export.overrideDefault.text"), 
				I18n.get("settings.file.export.overrideDefault.title"),
				JOptionPane.WARNING_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
		if(optionResultPane == JOptionPane.OK_OPTION) {
			try {
				this.doSetDefaultExport(new File("solrmeter.smc.xml"));
				window.dispose();
			} catch (IOException e) {
				Logger.getLogger(this.getClass()).error("Error exporting configuration", e);
				JOptionPane.showMessageDialog(SolrMeterMain.mainFrame, 
						I18n.get("settings.file.export.overrideDefault.error.message") + e.getMessage(), 
						I18n.get("settings.file.export.overrideDefault.error.title"), 
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
		
	}

	private void doSetDefaultExport(File file) throws IOException {
		SolrMeterConfiguration.exportConfiguration(file);
	}
	
	public void advancedSettings(){
		panel.getAdvancedSettingsDialog().setVisible(true);
	}
	
	public void addPropertyObserver(String property, SolrPropertyObserver observer){
		Set<SolrPropertyObserver> observers = observerMap.get(property);
		if(observers == null){
			observers = new HashSet<SolrPropertyObserver>();
			observerMap.put(property, observers);
		}
		observers.add(observer);
	}
	
	protected void notifyObservers(String property, String value){
		Set<SolrPropertyObserver> observers = observerMap.get(property);
		if(observers != null){
			for(SolrPropertyObserver observer: observers){
				observer.solrPropertyChanged(property, value);
			}
		}
	}

}
