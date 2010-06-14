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

import org.apache.log4j.Logger;

import com.linebee.solrmeter.model.OptimizeExecutor;
import com.linebee.solrmeter.view.OptimizeConsolePanel;

public class OptimizeExecutorController {
	
	private OptimizeExecutor executor;
	
	private OptimizeConsolePanel panel;
	
	public OptimizeExecutorController(
			OptimizeConsolePanel optimizeConsolePanel, OptimizeExecutor executor) {
		this.executor = executor;
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
