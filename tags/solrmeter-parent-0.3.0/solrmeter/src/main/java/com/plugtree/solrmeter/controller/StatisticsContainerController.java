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

import java.util.Timer;
import java.util.TimerTask;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.view.Refreshable;

@Singleton
public class StatisticsContainerController {

	private Refreshable container;
	
	private Timer timer = null;
	
	@Inject
	public StatisticsContainerController(@Named("statisticsContainer") Refreshable view) {
		this.container = view;
		timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				container.refreshView();
			}
			
		};
		timer.schedule(task, Long.valueOf(SolrMeterConfiguration.getProperty("statistic.refreshTime")), 
				Long.valueOf(SolrMeterConfiguration.getProperty("statistic.refreshTime")));
	}
	
	public void onTabChanged() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				container.refreshView();
			}
		};
		thread.start();
	}
}
