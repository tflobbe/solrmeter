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

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.model.OptimizeExecutor;
import com.plugtree.solrmeter.view.Refreshable;

@StressTestScope
public class OptimizeExecutorController {
	
	private OptimizeExecutor executor;
	
	private Refreshable panel;
	
	@Inject
	public OptimizeExecutorController(
			@Named("optimizeConsolePanel")Refreshable optimizeConsolePanel, 
			OptimizeExecutor optimizeExecutor) {
		this.executor = optimizeExecutor;
		this.panel = optimizeConsolePanel;
	}

	public void onOptimize() {
		Logger.getLogger(this.getClass()).info("Optimizing...");
		Thread thread = new Thread() {
			
			@Override
			public void run() {
				executor.execute();
				try {
					Thread.sleep(100);
					while(executor.isOptimizing()) {
						panel.refreshView();
					}
					panel.refreshView();
				} catch (InterruptedException e) {
					Logger.getLogger(this.getClass()).info(e);
				}
			}
		};
		thread.start();
		
	}

}
