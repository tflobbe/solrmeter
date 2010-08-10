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

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.linebee.solrmeter.model.UpdateExecutor;
import com.linebee.solrmeter.view.ConsolePanel;
import com.linebee.stressTestScope.StressTestScope;

@StressTestScope
public class UpdateExecutorController {

	private ConsolePanel panel;
	
	private UpdateExecutor executor;
	
	private Timer timer = null;

	@Inject
	public UpdateExecutorController(@Named("updateConsolePanel")ConsolePanel panel, UpdateExecutor executor) {
		this.panel = panel;
		this.executor = executor; 
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
		executor.start();
		panel.started();
	}

	public void onStop() {
		timer.cancel();
		executor.stop();
		panel.stopped();
	}

	public void onConcurrentQueriesValueChange(Integer value) {
		if(executor.getUpdatesPerMinute() > value) {
			decrementQueriesPerMinute(value);
		}else {
			incrementQueriesPerMinute(value);
		}
	}

	private void incrementQueriesPerMinute(Integer value) {
		Logger.getLogger(this.getClass()).debug("Incrementing");
		while(executor.getUpdatesPerMinute() < value) {
			executor.incrementOperationsPerMinute();
		}
	}

	private void decrementQueriesPerMinute(Integer value) {
		Logger.getLogger(this.getClass()).debug("Decrementing");
		while(executor.getUpdatesPerMinute() > value) {
			executor.decrementOperationsPerMinute();
		}
	}

	public void onDocsBeforeCommitValueChange(Integer value) {
		if(value > executor.getNumberOfDocumentsBeforeCommit()) {
			executor.incrementNumberOfDocumentsBeforeCommit();
		}else {
			executor.decrementNumberOfDocumentsBeforeCommit();
		}
	}

	public void onTimeBeforeCommitValueChange(Integer value) {
		executor.setMaxTimeBeforeCommit(value);
	}
}
