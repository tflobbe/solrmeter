/**
 * Copyright Linebee. www.linebee.com
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
package com.linebee.solrmeter.view;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.linebee.solrmeter.model.SolrMeterConfiguration;

public class I18n {
	
	private ResourceBundle resources;
	
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
		this.resources = ResourceBundle.getBundle("messages", locale);
	}
	
	public static void onConfigurationChange() {
		instance = new I18n();
	}
	
	public static String get(String key) {
		return instance.resources.getString(key);
	}

}
