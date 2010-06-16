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

import com.linebee.solrmeter.SolrMeterMain;
import com.linebee.solrmeter.model.exception.OperationException;
import com.linebee.solrmeter.view.JDialogStackTrace;
import com.linebee.solrmeter.view.statistic.ErrorLogPanel;

public class ErrorLogController {
	
	private ErrorLogPanel view;

	public ErrorLogController(ErrorLogPanel errorLogPanel) {
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
				view.refresh();
			}
		};
		thread.start();
		
	}
}
