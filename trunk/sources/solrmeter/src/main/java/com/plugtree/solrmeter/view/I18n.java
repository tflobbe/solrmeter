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
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.plugtree.solrmeter.model.SolrMeterConfiguration;

public class I18n {
	
	private List<ResourceBundle> resources;
	
	private Locale locale;
	
	private static I18n instance = new I18n();
	
	private I18n() {
		Locale locale;
		if(SolrMeterConfiguration.getProperty("view.locale.country") != null
				&& SolrMeterConfiguration.getProperty("view.locale.language") != null) {
			locale = new Locale(SolrMeterConfiguration.getProperty("view.locale.language"), SolrMeterConfiguration.getProperty("view.locale.country"));
		}else if(SolrMeterConfiguration.getProperty("view.locale.language") != null) {
			locale = new Locale(SolrMeterConfiguration.getProperty("view.locale.language"));
		}else {
			locale = Locale.getDefault();
		}
		Logger.getLogger(this.getClass()).info("Using Locale " + locale);
		this.locale = locale;
//		this.resources = ResourceBundle.getBundle("messages", locale); Is this not working??
//		http://bugs.sun.com/bugdatabase/view_bug.do;jsessionid=d7adaa31e312a97d6d0854a3fc241?bug_id=4303146
		this.resources = this.getResources(locale);
	}
	
	private List<ResourceBundle> getResources(Locale locale) {
		List<ResourceBundle> list = new LinkedList<ResourceBundle>();
		if(this.getClass().getClassLoader().getResource(("messages_" + locale.getLanguage() + "_" + locale.getCountry() + ".properties")) != null) {
			list.add(ResourceBundle.getBundle("messages_" + locale.getLanguage() + "_" + locale.getCountry()));
		}
		if(this.getClass().getClassLoader().getResource(("messages_" + locale.getLanguage() + ".properties")) != null) {
			list.add(ResourceBundle.getBundle("messages_" + locale.getLanguage()));
		}
		list.add(ResourceBundle.getBundle("messages", new Locale("", "")));
		return list;
	}

	public static void onConfigurationChange() {
		instance = new I18n();
	}
	
	public static String get(String key) {
		for(ResourceBundle resourceBundle:instance.resources) {
			if(resourceBundle.containsKey(key)) {
				return resourceBundle.getString(key);
			}
		}
		throw new RuntimeException("No resource value for key " + key);
	}
	
	public static Locale getLocale() {
		return instance.locale;
	}

}
