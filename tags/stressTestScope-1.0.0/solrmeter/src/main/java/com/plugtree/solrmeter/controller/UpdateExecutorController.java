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

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.model.UpdateExecutor;
import com.plugtree.solrmeter.view.ConsolePanel;

@StressTestScope
public class UpdateExecutorController {
	
	private Collection<ConsolePanel> observers;
	
	private UpdateExecutor executor;
	
	private Timer timer = null;

	@Inject
	public UpdateExecutorController(UpdateExecutor executor) {
		this.executor = executor;
		this.observers = new LinkedList<ConsolePanel>();
	}
	
	public void addObserver(ConsolePanel obs) {
		this.observers.add(obs);
	}

	public void onStart() {
		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				for(ConsolePanel obs: observers) {
					obs.refreshView();
				}
			}
		};
		timer.schedule(task, new Date(), 1000);
		executor.start();
		for(ConsolePanel obs: observers) {
			obs.started();
		}
	}

	public void onStop() {
		timer.cancel();
		executor.stop();
		for(ConsolePanel obs: observers) {
			obs.stopped();
		}
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
