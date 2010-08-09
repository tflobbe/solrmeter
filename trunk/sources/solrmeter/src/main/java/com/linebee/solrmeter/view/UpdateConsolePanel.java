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
package com.linebee.solrmeter.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.inject.Inject;
import com.linebee.solrmeter.controller.ExecutorFactory;
import com.linebee.solrmeter.controller.UpdateExecutorController;
import com.linebee.solrmeter.model.UpdateExecutor;
import com.linebee.solrmeter.model.statistic.CommitHistoryStatistic;
import com.linebee.solrmeter.view.component.InfoPanel;
import com.linebee.solrmeter.view.component.RoundedBorderJPanel;
import com.linebee.solrmeter.view.component.SpinnerPanel;
import com.linebee.stressTestScope.StressTestScope;

@StressTestScope
public class UpdateConsolePanel extends RoundedBorderJPanel implements ConsolePanel {
	
	private static final int MAX_CONCURRENT_UPDATES = Integer.MAX_VALUE;
	private static final int TIME_SPINNER_STEP = 500;
	private static final long serialVersionUID = 7795898682164203946L;
	private static final int paddingSize = 1;
	
	private UpdateExecutor updateExecutor;
	private CommitHistoryStatistic commitHistoryStatistic;
	private JButton jButtonStart;
	private JButton jButtonStop;
	
	private InfoPanel panelNotCommitedDocuments;
	private InfoPanel panelLastCommit;
	private InfoPanel panelNumberOfCommits;
	private InfoPanel panelStartTest;
	private InfoPanel panelErrorsOnUpdate;
	private InfoPanel panelErrorsOnCommit;
	private InfoPanel panelAddedDocuments;
	
	private SpinnerPanel concurrentUpdatesSpinnerPanel;
	private SpinnerPanel docsBeforeCommitSpinnerPanel;
	private SpinnerPanel maxTimeBeforeCommitSpinnerPanel;
	
	private UpdateExecutorController controller;
	
	@Inject
	public UpdateConsolePanel(ExecutorFactory factory, 
			UpdateExecutorController controller,
			CommitHistoryStatistic commitHistoryStatistic) {
		super(I18n.get("updateConsolePanel.title"));
		this.updateExecutor = factory.getCurrentUpdateExecutor();
		this.controller = controller;
		this.commitHistoryStatistic = commitHistoryStatistic;
		this.initGUI();
		stopped();
	}

	private void initGUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.addAndPadd(new InfoPanel(I18n.get("updateConsolePanel.performCommits"), String.valueOf(updateExecutor.isAutocommit())));
		if(!updateExecutor.isAutocommit()) {
			this.addAndPadd(getNumberOfDocsBeforeCommit());
			this.addAndPadd(getMaxTimeBeforeCommit());
//			panelMaxTimeBeforeCommit = new InfoPanel(I18n.get("updateConsolePanel.timeBeforeCommit"), String.valueOf(updateExecutor.getMaxTimeBeforeCommit()));
//			this.addAndPadd(panelMaxTimeBeforeCommit);
		}
		panelNotCommitedDocuments = new InfoPanel(I18n.get("updateConsolePanel.notCommitedDocs"), "0");
		this.addAndPadd(panelNotCommitedDocuments);
		
		panelLastCommit = new InfoPanel(I18n.get("updateConsolePanel.lastCommit"), "-");
		this.addAndPadd(panelLastCommit);
		
		panelNumberOfCommits = new InfoPanel(I18n.get("updateConsolePanel.numberOfCommits"), "0");
		this.addAndPadd(panelNumberOfCommits);
		
		panelAddedDocuments = new InfoPanel(I18n.get("updateConsolePanel.addedDocs"));
		this.addAndPadd(panelAddedDocuments);
		
		panelStartTest = new InfoPanel(I18n.get("updateConsolePanel.startedAt"), "");
		this.addAndPadd(panelStartTest);
		
		panelErrorsOnUpdate = new InfoPanel(I18n.get("updateConsolePanel.errorsOnUpdate"), "");
		this.addAndPadd(panelErrorsOnUpdate);
		
		panelErrorsOnCommit = new InfoPanel(I18n.get("updateConsolePanel.errorsOnCommit"), "");
		this.addAndPadd(panelErrorsOnCommit);
		
		this.addAndPadd(getCurrentUpdatesSpinner());
		
		jButtonStart = new JButton(I18n.get("updateConsolePanel.start"));
		jButtonStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onStart();
			}
			
		});
		jButtonStop = new JButton(I18n.get("updateConsolePanel.stop"));
		jButtonStop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onStop();
			}
			
		});
		JPanel auxiliarJPanel = new JPanel();
		auxiliarJPanel.setLayout(new BoxLayout(auxiliarJPanel, BoxLayout.X_AXIS));
		auxiliarJPanel.add(jButtonStart);
		auxiliarJPanel.add(jButtonStop);
		this.addAndPadd(auxiliarJPanel);
	}

	private Component getMaxTimeBeforeCommit() {
		maxTimeBeforeCommitSpinnerPanel = new SpinnerPanel(updateExecutor.getMaxTimeBeforeCommit(), 1, Integer.MAX_VALUE, TIME_SPINNER_STEP, I18n.get("updateConsolePanel.timeBeforeCommit"));
		maxTimeBeforeCommitSpinnerPanel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				controller.onTimeBeforeCommitValueChange(maxTimeBeforeCommitSpinnerPanel.getValue());
			}
		});
		return maxTimeBeforeCommitSpinnerPanel;
	}

	private Component getNumberOfDocsBeforeCommit() {
		docsBeforeCommitSpinnerPanel = new SpinnerPanel(updateExecutor.getNumberOfDocumentsBeforeCommit(), I18n.get("updateConsolePanel.commitsBeforeCommit"));
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
		panelAddedDocuments.setValue(String.valueOf(commitHistoryStatistic.getTotalAddedDocuments()));
		panelErrorsOnCommit.setValue(String.valueOf(commitHistoryStatistic.getCommitErrorCount()));
		panelErrorsOnUpdate.setValue(String.valueOf(commitHistoryStatistic.getUpdateErrorCount()));
	}

	public void started() {
		jButtonStop.setEnabled(true);
		jButtonStart.setEnabled(false);
		panelStartTest.setValue(SimpleDateFormat.getInstance().format(new Date()));
		
	}

	public void stopped() {
		jButtonStop.setEnabled(false);
		jButtonStart.setEnabled(true);
		
	}
	
	private Component getCurrentUpdatesSpinner() {
		concurrentUpdatesSpinnerPanel = new SpinnerPanel(updateExecutor.getUpdatesPerMinute(), 1, MAX_CONCURRENT_UPDATES, 1, I18n.get("updateConsolePanel.updatesPerMinute"));
		concurrentUpdatesSpinnerPanel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				controller.onConcurrentQueriesValueChange(concurrentUpdatesSpinnerPanel.getValue());
			}
		});
		return concurrentUpdatesSpinnerPanel;
	}
	
	private void addAndPadd(Component component) {
		this.add(component);
		this.add(Box.createRigidArea(new Dimension(paddingSize, paddingSize)));
	}

}
