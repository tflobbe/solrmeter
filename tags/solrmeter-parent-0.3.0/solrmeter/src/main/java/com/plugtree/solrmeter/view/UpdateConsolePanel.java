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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.controller.UpdateExecutorController;
import com.plugtree.solrmeter.model.FileUtils;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.SolrServerRegistry;
import com.plugtree.solrmeter.model.UpdateExecutor;
import com.plugtree.solrmeter.model.operation.PingOperation;
import com.plugtree.solrmeter.model.statistic.CommitHistoryStatistic;
import com.plugtree.solrmeter.model.statistic.OperationRateStatistic;
import com.plugtree.solrmeter.view.component.InfoPanel;
import com.plugtree.solrmeter.view.component.RoundedBorderJPanel;
import com.plugtree.solrmeter.view.component.SolrConnectedButton;
import com.plugtree.solrmeter.view.component.SpinnerPanel;

@StressTestScope
public class UpdateConsolePanel extends RoundedBorderJPanel implements ConsolePanel {
	
	private static final int MAX_CONCURRENT_UPDATES = Integer.MAX_VALUE;
	private static final long serialVersionUID = 7795898682164203946L;
	
	private UpdateExecutor updateExecutor;
	private CommitHistoryStatistic commitHistoryStatistic;
	private OperationRateStatistic operationRateStatistic;
	private SolrConnectedButton jButtonStart;
	private JButton jButtonStop;
	
	private InfoPanel panelStartTest;
	private InfoPanel panelErrorsOnUpdate;
	private InfoPanel panelAddedDocuments;
	private InfoPanel panelUpdateRate;
	
	private SpinnerPanel concurrentUpdatesSpinnerPanel;
	
	private UpdateExecutorController controller;
	
	@Inject
	public UpdateConsolePanel(UpdateExecutor updateExecutor, 
			UpdateExecutorController controller,
			CommitHistoryStatistic commitHistoryStatistic,
			OperationRateStatistic operationRatestatistic) {
		super(I18n.get("updateConsolePanel.title"));
		this.updateExecutor = updateExecutor;
		this.controller = controller;
		this.controller.addObserver(this);
		this.commitHistoryStatistic = commitHistoryStatistic;
		this.operationRateStatistic = operationRatestatistic;
		this.initGUI();
		stopped();
	}

	private void initGUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		panelAddedDocuments = new InfoPanel(I18n.get("updateConsolePanel.addedDocs"), "0");
		this.add(panelAddedDocuments);
		
		panelStartTest = new InfoPanel(I18n.get("updateConsolePanel.startedAt"), "-");
		this.add(panelStartTest);
		
		panelErrorsOnUpdate = new InfoPanel(I18n.get("updateConsolePanel.errorsOnUpdate"), "0");
		this.add(panelErrorsOnUpdate);

		this.add(getConcurrentUpdatesSpinner());
		
		panelUpdateRate = new InfoPanel(I18n.get("updateConsolePanel.actualUpdateRate"), "-");
		this.add(panelUpdateRate);
		
		this.add(Box.createVerticalGlue());
		
		try {
			jButtonStart = new SolrConnectedButton(new ImageIcon(FileUtils.findFileAsResource("./images/play.png")),new ImageIcon(FileUtils.findFileAsResource("./images/play-nc.png")), I18n.get("updateConsolePanel.pingFailing"), this.createPingOperation());
		} catch (FileNotFoundException e1) {
			Logger.getLogger(this.getClass()).error("play.png not found, using text button");
			jButtonStart = new SolrConnectedButton(I18n.get("updateConsolePanel.start"), I18n.get("updateConsolePanel.pingFailing"), this.createPingOperation());
		}

		jButtonStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onStart();
			}
			
		});
		
		try {
			jButtonStop = new JButton(new ImageIcon(FileUtils.findFileAsResource("./images/stop.png")));
		} catch (FileNotFoundException e1) {
			Logger.getLogger(this.getClass()).error("stop.png not found, using text button");
			jButtonStop = new JButton(I18n.get("updateConsolePanel.stop"));
		}
		
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
		this.add(auxiliarJPanel);
	}

	private PingOperation createPingOperation() {
		return new PingOperation(SolrServerRegistry.getSolrServer(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.SOLR_ADD_URL)));
	}

	public void refreshView() {
		panelAddedDocuments.setValue(String.valueOf(commitHistoryStatistic.getTotalAddedDocuments()));
		panelErrorsOnUpdate.setValue(String.valueOf(commitHistoryStatistic.getUpdateErrorCount()));
		panelUpdateRate.setValue(String.valueOf(operationRateStatistic.getUpdateRate()));
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
	
	private Component getConcurrentUpdatesSpinner() {
		concurrentUpdatesSpinnerPanel = new SpinnerPanel(updateExecutor.getUpdatesPerMinute(), 1, MAX_CONCURRENT_UPDATES, 1, I18n.get("updateConsolePanel.updatesPerMinute"));
		concurrentUpdatesSpinnerPanel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				controller.onConcurrentQueriesValueChange(concurrentUpdatesSpinnerPanel.getValue());
			}
		});
		return concurrentUpdatesSpinnerPanel;
	}

}
