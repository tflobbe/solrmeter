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
import java.math.BigDecimal;
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
import com.plugtree.solrmeter.controller.QueryExecutorController;
import com.plugtree.solrmeter.model.FileUtils;
import com.plugtree.solrmeter.model.QueryExecutor;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.SolrServerRegistry;
import com.plugtree.solrmeter.model.operation.PingOperation;
import com.plugtree.solrmeter.model.statistic.OperationRateStatistic;
import com.plugtree.solrmeter.model.statistic.SimpleQueryStatistic;
import com.plugtree.solrmeter.view.component.InfoPanel;
import com.plugtree.solrmeter.view.component.RoundedBorderJPanel;
import com.plugtree.solrmeter.view.component.SolrConnectedButton;
import com.plugtree.solrmeter.view.component.SpinnerPanel;

/**
 * Main Queries Panel
 * @author tflobbe
 *
 */
@StressTestScope
public class QueryConsolePanel extends RoundedBorderJPanel implements ConsolePanel {

	private static final long serialVersionUID = 1376883703280500293L;
	private static final int MAX_CONCURRENT_QUERIES = Integer.MAX_VALUE;
	
	private SimpleQueryStatistic simpleQueryStatistic;
	private OperationRateStatistic operationRateStatistic;
	
	private QueryExecutorController controller;
	private QueryExecutor queryExecutor;
	
	private InfoPanel totalQueries;
	private InfoPanel totalQueryTime;
	private InfoPanel averageQueryTime;
	private InfoPanel totalClientTime;
	private InfoPanel averageClientTime;
	private InfoPanel totalErrors;
	private InfoPanel startedAt;
	private InfoPanel actualQueryRate;
	
	private SpinnerPanel concurrentQueries;
	
	private SolrConnectedButton startButton;
	
	private JButton stopButton;
	
	@Inject
	public QueryConsolePanel(QueryExecutorController controller, 
			SimpleQueryStatistic simpleQueryStatistic,
			OperationRateStatistic operationRateStatistic,
			QueryExecutor queryExecutor) {
		super(I18n.get("queryConsolePanel.title"));
		this.simpleQueryStatistic = simpleQueryStatistic;
		this.operationRateStatistic = operationRateStatistic;
		this.controller = controller;
		this.queryExecutor = queryExecutor;
		this.initGUI();
	}

	private void initGUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		totalQueries = new InfoPanel(I18n.get("queryConsolePanel.totalQueries"), "0");
		this.add(totalQueries);
		totalQueryTime = new InfoPanel(I18n.get("queryConsolePanel.totalQueryTime"), "0"); 
		this.add(totalQueryTime);
		averageQueryTime = new InfoPanel(I18n.get("queryConsolePanel.averageQueryTime"), "0"); 
		this.add(averageQueryTime);
		totalClientTime = new InfoPanel(I18n.get("queryConsolePanel.totalClientTime"), "0");
		this.add(totalClientTime);
		averageClientTime = new InfoPanel(I18n.get("queryConsolePanel.averageClientTime"), "0"); 
		this.add(averageClientTime);
		startedAt = new InfoPanel(I18n.get("queryConsolePanel.startedAt"), "-");
		this.add(startedAt);
		totalErrors = new InfoPanel(I18n.get("queryConsolePanel.totalErrors"), "0");
		this.add(totalErrors);
		this.add(this.getCurrentQueriesSpinner());
		actualQueryRate = new InfoPanel(I18n.get("queryConsolePanel.actualQueriesPerMinute"), "-");
		this.add(actualQueryRate);
		
		this.add(Box.createVerticalGlue());
			
		try {
			startButton = new SolrConnectedButton(new ImageIcon(FileUtils.findFileAsResource("./images/play.png")), new ImageIcon(FileUtils.findFileAsResource("./images/play-nc.png")), I18n.get("queryConsolePanel.pingFailing"), this.createPingOperation());
		} catch (FileNotFoundException e1) {
			Logger.getLogger(this.getClass()).error("play.png not found, using text button");
			startButton = new SolrConnectedButton(I18n.get("queryConsolePanel.start"), I18n.get("queryConsolePanel.pingFailing"), this.createPingOperation());
		}
		
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onStart();
			}
		});
		
		try {
			stopButton = new JButton(new ImageIcon(FileUtils.findFileAsResource("./images/stop.png")));
		} catch (FileNotFoundException e1) {
			Logger.getLogger(this.getClass()).error("stop.png not found, using text button");
			stopButton = new JButton(I18n.get("queryConsolePanel.stop"));
		}
		
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
		this.add(auxiliarPanel);
		
		stopped();
	}

	private PingOperation createPingOperation() {
		PingOperation operation = new PingOperation(SolrServerRegistry.getSolrServer(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.SOLR_SEARCH_URL)));
		return operation;
	}
	
	private Component getCurrentQueriesSpinner() {
		concurrentQueries = new SpinnerPanel(1, 1, MAX_CONCURRENT_QUERIES, 1, I18n.get("queryConsolePanel.queriesPerMinute"));
		concurrentQueries.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				controller.onConcurrentQueriesValueChange(concurrentQueries.getValue());
			}
		});
		concurrentQueries.setValue(queryExecutor.getQueriesPerMinute());
		return concurrentQueries;
	}

	public void refreshView() {
		concurrentQueries.setValue(queryExecutor.getQueriesPerMinute());
		totalQueries.setValue(String.valueOf(simpleQueryStatistic.getTotalQueries()));
		totalQueryTime.setValue(String.valueOf(simpleQueryStatistic.getTotalQTime()));
		totalClientTime.setValue(String.valueOf(simpleQueryStatistic.getTotalClientTime()));
		totalErrors.setValue(String.valueOf(simpleQueryStatistic.getTotalErrors()));
		if(simpleQueryStatistic.getTotalQueries() != 0) {
			averageQueryTime.setValue(String.valueOf(simpleQueryStatistic.getAverageQueryTime()));
			averageClientTime.setValue(String.valueOf(simpleQueryStatistic.getAverageClientTime()));
		}
		actualQueryRate.setValue(String.valueOf(operationRateStatistic.getQueryRate()));
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
