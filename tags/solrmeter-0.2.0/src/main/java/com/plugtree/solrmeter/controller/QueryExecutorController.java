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

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.model.QueryExecutor;
import com.plugtree.solrmeter.view.ConsolePanel;

@StressTestScope
public class QueryExecutorController {
	
	private ConsolePanel panel;
	
	private QueryExecutor executor;
	
	private Timer timer = null;

	@Inject
	public QueryExecutorController(@Named("queryConsolePanel") ConsolePanel queryConsolePanel,
			QueryExecutor queryExecutor) {
		this.panel = queryConsolePanel;
		this.executor = queryExecutor;
	}

	public void onStart() {
		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				panel.refreshView();
			}
		};
		timer.schedule(task, new Date(), 1000);
		getExecutor().prepare();
		getExecutor().start();
		panel.started();
	}

	public void onStop() {
		timer.cancel();
		getExecutor().stop();
		panel.stopped();
	}

	public void onConcurrentQueriesValueChange(Integer value) {
		if(getExecutor().getQueriesPerMinute() > value) {
			decrementQueriesPerMinute(value);
		}else {
			incrementQueriesPerMinute(value);
		}
	}

	private void incrementQueriesPerMinute(Integer value) {
		Logger.getLogger(this.getClass()).debug("Incrementing");
		while(getExecutor().getQueriesPerMinute() < value) {
			getExecutor().incrementOperationsPerMinute();
		}
	}

	private void decrementQueriesPerMinute(Integer value) {
		Logger.getLogger(this.getClass()).debug("Decrementing");
		while(getExecutor().getQueriesPerMinute() > value) {
			getExecutor().decrementOperationsPerMinute();
		}
		
	}
	
	private QueryExecutor getExecutor() {
		return executor;
	}

}
