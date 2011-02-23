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
package com.plugtree.solrmeter.view;

import java.awt.Component;
import java.text.SimpleDateFormat;

import javax.swing.BoxLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.inject.Inject;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.controller.UpdateExecutorController;
import com.plugtree.solrmeter.model.UpdateExecutor;
import com.plugtree.solrmeter.model.statistic.CommitHistoryStatistic;
import com.plugtree.solrmeter.model.statistic.OperationRateStatistic;
import com.plugtree.solrmeter.view.component.InfoPanel;
import com.plugtree.solrmeter.view.component.RoundedBorderJPanel;
import com.plugtree.solrmeter.view.component.SpinnerPanel;

@StressTestScope
public class CommitConsolePanel extends RoundedBorderJPanel implements ConsolePanel {
	
	private static final int TIME_SPINNER_STEP = 500;
	private static final long serialVersionUID = 7795898682164203946L;
	
	private UpdateExecutor updateExecutor;
	private CommitHistoryStatistic commitHistoryStatistic;
	
	private InfoPanel panelPerformCommits;
	private InfoPanel panelNotCommitedDocuments;
	private InfoPanel panelLastCommit;
	private InfoPanel panelNumberOfCommits;
	private InfoPanel panelErrorsOnCommit;
	
	private SpinnerPanel docsBeforeCommitSpinnerPanel;
	private SpinnerPanel maxTimeBeforeCommitSpinnerPanel;
	
	private UpdateExecutorController controller;
	
	@Inject
	public CommitConsolePanel(UpdateExecutor updateExecutor, 
			UpdateExecutorController controller,
			CommitHistoryStatistic commitHistoryStatistic,
			OperationRateStatistic operationRatestatistic) {
		super(I18n.get("commitConsolePanel.title"));
		this.updateExecutor = updateExecutor;
		this.controller = controller;
		this.controller.addObserver(this);
		this.commitHistoryStatistic = commitHistoryStatistic;
		this.initGUI();
		stopped();
	}

	private void initGUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		panelPerformCommits = new InfoPanel(I18n.get("commitConsolePanel.performCommits"), String.valueOf(updateExecutor.isAutocommit()));
		this.add(panelPerformCommits);
		
		if(!updateExecutor.isAutocommit()) {
			this.add(getNumberOfDocsBeforeCommit());
			this.add(getMaxTimeBeforeCommit());
		}
		panelNotCommitedDocuments = new InfoPanel(I18n.get("commitConsolePanel.notCommitedDocs"), "0");
		this.add(panelNotCommitedDocuments);
		
		panelLastCommit = new InfoPanel(I18n.get("commitConsolePanel.lastCommit"), "-");
		this.add(panelLastCommit);
		
		panelNumberOfCommits = new InfoPanel(I18n.get("commitConsolePanel.numberOfCommits"), "0");
		this.add(panelNumberOfCommits);
		
		panelErrorsOnCommit = new InfoPanel(I18n.get("commitConsolePanel.errorsOnCommit"), "");
		this.add(panelErrorsOnCommit);
	}

	private Component getMaxTimeBeforeCommit() {
		maxTimeBeforeCommitSpinnerPanel = new SpinnerPanel(updateExecutor.getMaxTimeBeforeCommit(), 1, Integer.MAX_VALUE, TIME_SPINNER_STEP, I18n.get("commitConsolePanel.timeBeforeCommit"));
		maxTimeBeforeCommitSpinnerPanel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				controller.onTimeBeforeCommitValueChange(maxTimeBeforeCommitSpinnerPanel.getValue());
			}
		});
		return maxTimeBeforeCommitSpinnerPanel;
	}

	private Component getNumberOfDocsBeforeCommit() {
		docsBeforeCommitSpinnerPanel = new SpinnerPanel(updateExecutor.getNumberOfDocumentsBeforeCommit(), I18n.get("commitConsolePanel.commitsBeforeCommit"));
		docsBeforeCommitSpinnerPanel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				controller.onDocsBeforeCommitValueChange(docsBeforeCommitSpinnerPanel.getValue());
			}
		});
		return docsBeforeCommitSpinnerPanel;
	}

	public void refreshView() {
		panelNotCommitedDocuments.setValue(String.valueOf(updateExecutor.getNotCommitedDocuments()));
		if(commitHistoryStatistic.getLastCommitDate() != null) {
			panelLastCommit.setValue(SimpleDateFormat.getInstance().format(commitHistoryStatistic.getLastCommitDate()));
		}
		panelNumberOfCommits.setValue(String.valueOf(commitHistoryStatistic.getTotalCommits()));
		panelErrorsOnCommit.setValue(String.valueOf(commitHistoryStatistic.getCommitErrorCount()));
	}
	
	public void started() {
		
	}

	public void stopped() {
		
	}

}
