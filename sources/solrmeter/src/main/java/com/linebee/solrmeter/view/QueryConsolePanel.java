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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.inject.Inject;
import com.linebee.solrmeter.controller.QueryExecutorController;
import com.linebee.solrmeter.model.QueryExecutor;
import com.linebee.solrmeter.model.statistic.SimpleQueryStatistic;
import com.linebee.solrmeter.view.component.InfoPanel;
import com.linebee.solrmeter.view.component.RoundedBorderJPanel;
import com.linebee.stressTestScope.StressTestScope;

/**
 * Main Queries Panel
 * @author tflobbe
 *
 */
@StressTestScope
public class QueryConsolePanel extends RoundedBorderJPanel implements ConsolePanel {

	private static final long serialVersionUID = 1376883703280500293L;
	private static final double MAX_CONCURRENT_QUERIES = 999999999;
	private static final int paddingSize = 1;
	
	private SimpleQueryStatistic simpleQueryStatistic;
	
	private QueryExecutorController controller;
	private QueryExecutor queryExecutor;
	
	private InfoPanel totalQueries;
	private InfoPanel totalQueryTime;
	private InfoPanel averageQueryTime;
	private InfoPanel totalClientTime;
	private InfoPanel averageClientTime;
	private InfoPanel totalErrors;
	private InfoPanel startedAt;
	
	private JSpinner concurrentQueries;
	
	private JButton startButton;
	
	private JButton stopButton;
	
	@Inject
	public QueryConsolePanel(QueryExecutorController controller, 
			SimpleQueryStatistic simpleQueryStatistic,
			QueryExecutor queryExecutor) {
		super(I18n.get("queryConsolePanel.title"));
		this.simpleQueryStatistic = simpleQueryStatistic;
		this.controller = controller;
		this.queryExecutor = queryExecutor;
		this.initGUI();
	}

	private void initGUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		totalQueries = new InfoPanel(I18n.get("queryConsolePanel.totalQueries"), "0");
		this.addAndPadd(totalQueries);
		totalQueryTime = new InfoPanel(I18n.get("queryConsolePanel.totalQueryTime"), "0"); 
		this.addAndPadd(totalQueryTime);
		averageQueryTime = new InfoPanel(I18n.get("queryConsolePanel.averageQueryTime"), "0"); 
		this.addAndPadd(averageQueryTime);
		totalClientTime = new InfoPanel(I18n.get("queryConsolePanel.totalClientTime"), "0");
		this.addAndPadd(totalClientTime);
		averageClientTime = new InfoPanel(I18n.get("queryConsolePanel.averageClientTime"), "0"); 
		this.addAndPadd(averageClientTime);
		startedAt = new InfoPanel(I18n.get("queryConsolePanel.startedAt"));
		this.addAndPadd(startedAt);
		totalErrors = new InfoPanel(I18n.get("queryConsolePanel.totalErrors"), "0");
		this.addAndPadd(totalErrors);
		this.addAndPadd(this.getCurrentQueriesSpinner());
		startButton = new JButton(I18n.get("queryConsolePanel.start"));
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onStart();
			}
		});
		
		stopButton = new JButton(I18n.get("queryConsolePanel.stop"));
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onStop();
			}
		});
		JPanel auxiliarPanel = new JPanel();
		auxiliarPanel.setLayout(new BoxLayout(auxiliarPanel, BoxLayout.X_AXIS));
		auxiliarPanel.add(startButton);
		auxiliarPanel.add(stopButton);
		this.addAndPadd(auxiliarPanel);
		stopped();
	}

	private void addAndPadd(Component component) {
		this.add(component);
		this.add(Box.createRigidArea(new Dimension(paddingSize, paddingSize)));
	}
	
	private Component getCurrentQueriesSpinner() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		concurrentQueries = new JSpinner(new SpinnerNumberModel(1, 1, MAX_CONCURRENT_QUERIES, 1));
		((JSpinner.DefaultEditor)concurrentQueries.getEditor()).getTextField().setEditable(false);
		concurrentQueries.setSize(new Dimension(20, 20));
		concurrentQueries.setMaximumSize(new Dimension(40, 20));
		concurrentQueries.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				controller.onConcurrentQueriesValueChange(((Double)concurrentQueries.getValue()).intValue());
			}
		});
		
		panel.add(new JLabel(I18n.get("queryConsolePanel.queriesPerMinute")));
		panel.add(Box.createHorizontalGlue());
		panel.add(concurrentQueries);
		concurrentQueries.setValue(new Double(queryExecutor.getQueriesPerMinute()));
		return panel;
	}

	public void refreshView() {
		concurrentQueries.setValue(new Double(queryExecutor.getQueriesPerMinute()));
		totalQueries.setValue(String.valueOf(simpleQueryStatistic.getTotalQueries()));
		totalQueryTime.setValue(String.valueOf(simpleQueryStatistic.getTotalQTime()));
		totalClientTime.setValue(String.valueOf(simpleQueryStatistic.getTotalClientTime()));
		totalErrors.setValue(String.valueOf(simpleQueryStatistic.getTotalErrors()));
		if(simpleQueryStatistic.getTotalQueries() != 0) {
			averageQueryTime.setValue(String.valueOf(simpleQueryStatistic.getTotalQTime() / simpleQueryStatistic.getTotalQueries()));
			averageClientTime.setValue(String.valueOf(simpleQueryStatistic.getTotalClientTime() / simpleQueryStatistic.getTotalQueries()));
		}
	}

	public void started() {
		stopButton.setEnabled(true);
		startButton.setEnabled(false);
		startedAt.setValue(SimpleDateFormat.getInstance().format(new Date()));
	}

	public void stopped() {
		stopButton.setEnabled(false);
		startButton.setEnabled(true);
		
	}

	@Inject
	public void setController(QueryExecutorController controller) {
		this.controller = controller;
	}
	
	

}
