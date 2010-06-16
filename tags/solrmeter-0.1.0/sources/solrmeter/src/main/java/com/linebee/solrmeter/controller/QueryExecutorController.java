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
package com.linebee.solrmeter.controller;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.linebee.solrmeter.model.QueryExecutor;
import com.linebee.solrmeter.view.Model;
import com.linebee.solrmeter.view.QueryConsolePanel;

public class QueryExecutorController {
	
	private QueryConsolePanel panel;
	
	private Timer timer = null;

	public QueryExecutorController(QueryConsolePanel panel) {
		this.panel = panel;
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
			getExecutor().incrementConcurrentOperations();
		}
	}

	private void decrementQueriesPerMinute(Integer value) {
		Logger.getLogger(this.getClass()).debug("Decrementing");
		while(getExecutor().getQueriesPerMinute() > value) {
			getExecutor().decrementConcurrentQueries();
		}
		
	}
	
	private QueryExecutor getExecutor() {
		return Model.getInstance().getCurrentQueryExecutor();
	}

}