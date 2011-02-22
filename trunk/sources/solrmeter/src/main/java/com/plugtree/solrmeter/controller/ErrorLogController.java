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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.SolrMeterMain;
import com.plugtree.solrmeter.model.exception.OperationException;
import com.plugtree.solrmeter.view.JDialogStackTrace;
import com.plugtree.solrmeter.view.Refreshable;

@StressTestScope
public class ErrorLogController {
	
	private Refreshable view;

	@Inject
	public ErrorLogController(@Named("errorLogPanel")Refreshable errorLogPanel) {
		view = errorLogPanel;
	}

	public void onErrorDoubleClick(OperationException exception) {
		JDialogStackTrace dialog = new JDialogStackTrace(SolrMeterMain.mainFrame, exception);
		dialog.setVisible(true);
	}

	public void onErrorsToShowChaned() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				view.refreshView();
			}
		};
		thread.start();
		
	}
}
