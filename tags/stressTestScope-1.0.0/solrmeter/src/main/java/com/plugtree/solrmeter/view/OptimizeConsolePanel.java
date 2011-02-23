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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;

import com.google.inject.Inject;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.controller.OptimizeExecutorController;
import com.plugtree.solrmeter.model.OptimizeExecutor;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.SolrServerRegistry;
import com.plugtree.solrmeter.model.operation.PingOperation;
import com.plugtree.solrmeter.model.statistic.SimpleOptimizeStatistic;
import com.plugtree.solrmeter.view.component.InfoPanel;
import com.plugtree.solrmeter.view.component.RoundedBorderJPanel;
import com.plugtree.solrmeter.view.component.SolrConnectedButton;

@StressTestScope
public class OptimizeConsolePanel extends RoundedBorderJPanel implements Refreshable {
	
	private static final long serialVersionUID = -1971290718269938970L;

	private OptimizeExecutor executor;
	
	private SimpleOptimizeStatistic optimizeStatistic;
	
	private OptimizeExecutorController controller;
	
	private InfoPanel optimizing;
	
	private InfoPanel totalOptimizeCount;
	
	private InfoPanel lastOptimizationTime;
	
	private InfoPanel totalOptimizationTime;
	
	private InfoPanel lastOptimizationResult;
	
	private InfoPanel optimizationTimeAverage;
	
	private InfoPanel totalOptimizeErrors;
	
	private SolrConnectedButton optimizeButton;
	
	@Inject
	public OptimizeConsolePanel(OptimizeExecutorController controller,
			SimpleOptimizeStatistic optimizeStatistic,
			OptimizeExecutor executor) {
		super(I18n.get("optimizeConsolePanel.title"));
		this.executor = executor;
		this.optimizeStatistic = optimizeStatistic;
		this.controller = controller;
		this.initGUI();
	}

	private void initGUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		optimizing = new InfoPanel(I18n.get("optimizeConsolePanel.optimizingNow"), "false");
		this.add(optimizing);
		totalOptimizeCount = new InfoPanel(I18n.get("optimizeConsolePanel.optimizeCount"), "0");
		this.add(totalOptimizeCount);
		lastOptimizationTime = new InfoPanel(I18n.get("optimizeConsolePanel.lastOptimizeTime"), "0");
		this.add(lastOptimizationTime);
		totalOptimizationTime = new InfoPanel(I18n.get("optimizeConsolePanel.totalOptimizeTime"), "0");
		this.add(totalOptimizationTime);
		optimizationTimeAverage = new InfoPanel(I18n.get("optimizeConsolePanel.optimizationTimeAverage"), "0");
		this.add(optimizationTimeAverage);
		lastOptimizationResult = new InfoPanel(I18n.get("optimizeConsolePanel.lastOptimizationResult"), "-");
		this.add(lastOptimizationResult);
		totalOptimizeErrors = new InfoPanel(I18n.get("optimizeConsolePanel.totalOptimizationErrors"), "0");
		this.add(totalOptimizeErrors);
		
		this.add(Box.createVerticalGlue());
		
		optimizeButton = new SolrConnectedButton(I18n.get("optimizeConsolePanel.optimizeNow"), I18n.get("optimizeConsolePanel.pingFailing"), this.createPingOperation());
		this.add(optimizeButton);
		optimizeButton.setAlignmentX(0.5f);
		optimizeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onOptimize();
			}
			
		});
	}
	
	private PingOperation createPingOperation() {
		return new PingOperation(SolrServerRegistry.getSolrServer(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.SOLR_ADD_URL)));
	}
	
	private void optimizing() {
		optimizing.setValue("true");
		optimizeButton.setEnabled(false);
	}
	
	private void idle() {
		optimizing.setValue("false");
		optimizeButton.setEnabled(true);
	}
	
	public void refreshView() {
		if(executor.isOptimizing()) {
			this.optimizing();
		}else {
			this.idle();
		}
		totalOptimizeCount.setValue(String.valueOf(optimizeStatistic.getOptimizationCount()));
		lastOptimizationTime.setValue(String.valueOf(optimizeStatistic.getLastOptimizationTime()));
		totalOptimizationTime.setValue(String.valueOf(optimizeStatistic.getTotalOptimizationTime()));
		optimizationTimeAverage.setValue(String.valueOf(optimizeStatistic.getAverageOptimizationTime()));
		if(optimizeStatistic.getLastOptimizationResult() != null) {
			lastOptimizationResult.setValue(String.valueOf(optimizeStatistic.getLastOptimizationResult()));
		}
		totalOptimizeErrors.setValue(String.valueOf(optimizeStatistic.getOptimizeErrorCount()));
	}

}
