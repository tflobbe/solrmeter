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
package com.linebee.solrmeter.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.linebee.solrmeter.model.statistic.ErrorLogStatistic;
import com.linebee.solrmeter.model.statistic.FullQueryStatistic;
import com.linebee.solrmeter.model.statistic.HistogramQueryStatistic;
import com.linebee.solrmeter.model.statistic.OperationTimeHistory;
import com.linebee.solrmeter.model.statistic.QueryLogStatistic;
import com.linebee.solrmeter.model.statistic.QueryTimeHistoryStatistic;
import com.linebee.solrmeter.model.statistic.SimpleQueryStatistic;
import com.linebee.solrmeter.model.statistic.TimeRangeStatistic;
import com.linebee.solrmeter.view.statistic.ErrorLogPanel;
import com.linebee.solrmeter.view.statistic.FullQueryStatisticPanel;
import com.linebee.solrmeter.view.statistic.HistogramChartPanel;
import com.linebee.solrmeter.view.statistic.OperationTimeLineChartPanel;
import com.linebee.solrmeter.view.statistic.PieChartPanel;
import com.linebee.solrmeter.view.statistic.QueryTimeHistoryPanel;


public class ConsoleFrame extends JFrame {
	
	private static final long serialVersionUID = 976934495299084244L;
	private QueryConsolePanel queryPanel;
	private UpdateConsolePanel updatePanel;
	private OptimizeConsolePanel optimizePanel;
	private StatisticsContainer statisticsContainer;
	
	public ConsoleFrame() {
		super();
		this.initMenu();
		this.initGUI();
		this.addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent evt) {
	        	exitApplication();
	        }
	    });
		SwingUtils.centerWindow(this);
	}
	
	private void exitApplication() {
		System.exit(0);
	}

	private void initGUI() {
//		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setSize(new Dimension(800, 700));
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		JPanel auxiliarPanel = new JPanel();
		auxiliarPanel.setLayout(new GridBagLayout());
		auxiliarPanel.add(this.getQueryPanel(), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		auxiliarPanel.add(this.getUpdatePanel(), new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		auxiliarPanel.add(this.getOptimizePanel(), new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		this.getContentPane().add(auxiliarPanel);
		this.addStatisticsPanel();
		
	}

	private void initMenu() {
		MenuBar menuBar = new SolrMeterMenuBar();
		this.setMenuBar(menuBar);
		
	}

	private OptimizeConsolePanel getOptimizePanel() {
		optimizePanel = new OptimizeConsolePanel();
		return optimizePanel;
	}

	private UpdateConsolePanel getUpdatePanel() {
		updatePanel = new UpdateConsolePanel();
		return updatePanel;
	}

	private void addStatisticsPanel() {
		statisticsContainer = new StatisticsContainer();
		statisticsContainer.addStatistic(new PieChartPanel((TimeRangeStatistic) Model.getInstance().getQueryStatistic("timeRangeStatistic")));
		statisticsContainer.addStatistic(new HistogramChartPanel((HistogramQueryStatistic) Model.getInstance().getQueryStatistic("histogram")));
		statisticsContainer.addStatistic(new QueryTimeHistoryPanel((QueryTimeHistoryStatistic) Model.getInstance().getQueryStatistic("queryTimeHistory")));
		statisticsContainer.addStatistic(new ErrorLogPanel((ErrorLogStatistic) Model.getInstance().getQueryStatistic("errorLogStatistic")));
		statisticsContainer.addStatistic(new OperationTimeLineChartPanel((OperationTimeHistory) Model.getInstance().getQueryStatistic("operationTimeHistory")));
		statisticsContainer.addStatistic(new FullQueryStatisticPanel((FullQueryStatistic) Model.getInstance().getQueryStatistic("fullQueryStatistic"), 
				(QueryLogStatistic) Model.getInstance().getQueryStatistic("queryLogStatistic")));
		
		this.getContentPane().add(statisticsContainer);
	}

	private QueryConsolePanel getQueryPanel() {
		queryPanel = new QueryConsolePanel((SimpleQueryStatistic) Model.getInstance().getQueryStatistic("simpleQueryStatistic"));
		return queryPanel;
	}

	public void onConfigurationChanged() {
		this.getContentPane().removeAll();
		this.initGUI();
		this.getContentPane().repaint();
		((JComponent)this.getContentPane()).revalidate();
	}

}
