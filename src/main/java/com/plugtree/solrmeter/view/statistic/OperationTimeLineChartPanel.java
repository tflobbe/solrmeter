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
package com.plugtree.solrmeter.view.statistic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.RectangleEdge;

import com.google.inject.Inject;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.model.statistic.OperationTimeHistory;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.StatisticPanel;

@StressTestScope
public class OperationTimeLineChartPanel extends StatisticPanel implements ActionListener {

	private static final long serialVersionUID = 3661614439597184136L;
	
	private static final String SERIES_KEY_COMMIT_TIME = "commitTime";
	
	private static final String SERIES_KEY_QUERIES_TIME = "queriesTime";
	
	private static final String SERIES_KEY_UPDATES_TIME = "updatesTime";
	
	private static final String SERIES_KEY_OPTIMIZE_TIME = "optimizeTime";
	
	private DefaultXYDataset xyDataset = new DefaultXYDataset();
	
	private OperationTimeHistory statistic;
	
	private JCheckBox checkBoxShowQueries;
	private JCheckBox checkBoxShowOptimize;
	private JCheckBox checkBoxShowAdd;
	private JCheckBox checkBoxShowCommit;
	
	@Inject
	public OperationTimeLineChartPanel(OperationTimeHistory statistic) {
		super();
		this.statistic = statistic;
		this.xyDataset = new DefaultXYDataset();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(this.createChartPanel());
		this.add(this.createCheckBoxPanel());
	}
	
	private Component createCheckBoxPanel() {
		JPanel panelCheckBox = new JPanel();
		panelCheckBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panelCheckBox.setLayout(new BoxLayout(panelCheckBox, BoxLayout.X_AXIS));
		checkBoxShowCommit = new JCheckBox(I18n.get("statistic.operationTimeLineChartPanel.checkbox.commit"));
		checkBoxShowCommit.addActionListener(this);
		checkBoxShowOptimize = new JCheckBox(I18n.get("statistic.operationTimeLineChartPanel.checkbox.optimize"));
		checkBoxShowOptimize.addActionListener(this);
		checkBoxShowAdd = new JCheckBox(I18n.get("statistic.operationTimeLineChartPanel.checkbox.add"));
		checkBoxShowAdd.addActionListener(this);
		checkBoxShowQueries = new JCheckBox(I18n.get("statistic.operationTimeLineChartPanel.checkbox.query"));
		checkBoxShowQueries.addActionListener(this);
		panelCheckBox.add(Box.createHorizontalGlue());
		panelCheckBox.add(checkBoxShowCommit);
		panelCheckBox.add(Box.createHorizontalGlue());
		panelCheckBox.add(checkBoxShowOptimize);
		panelCheckBox.add(Box.createHorizontalGlue());
		panelCheckBox.add(checkBoxShowQueries);
		panelCheckBox.add(Box.createHorizontalGlue());
		panelCheckBox.add(checkBoxShowAdd);
		panelCheckBox.add(Box.createHorizontalGlue());
		panelCheckBox.setMaximumSize(new Dimension(800, 25));
		checkAll();
		return panelCheckBox;
	}
	
	private Component createChartPanel() {
		NumberAxis xaxis = new NumberAxis(I18n.get("statistic.operationTimeLineChartPanel.executionInstant"));
		NumberAxis yaxis = new NumberAxis(I18n.get("statistic.operationTimeLineChartPanel.qTime"));
		
		XYPlot plot = new XYPlot(xyDataset, xaxis, yaxis, new XYLineAndShapeRenderer(true, true));
		
		JFreeChart chart = new JFreeChart(I18n.get("statistic.operationTimeLineChartPanel.title"),
				null, plot, true);
		chart.getLegend().setPosition(RectangleEdge.RIGHT);
		
		ChartPanel chartPanel = new ChartPanel(chart);
		
		chartPanel.setBorder(CHART_BORDER);
		chartPanel.setMinimumDrawHeight(0);
		chartPanel.setMinimumDrawWidth(0);
		chartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
		chartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
		
		return chartPanel;
	}

	private void checkAll() {
		checkBoxShowCommit.setSelected(true);
		checkBoxShowOptimize.setSelected(true);
		checkBoxShowAdd.setSelected(true);
		checkBoxShowQueries.setSelected(true);
	}

	@Override
	public String getStatisticName() {
		return I18n.get("statistic.operationTimeLineChartPanel.title");
	}

	@Override
	public synchronized void refreshView() {
		Logger.getLogger(this.getClass()).debug("refreshing Time Line");
		
		// instead of deleting the series when a checkbox is unchecked,
		// I prefer to use an empty map because in this way the legend
		// and colors of the chart remain unchanged
		Map<Long,Long> emptyMap = Collections.emptyMap();
		
		if(checkBoxShowCommit.isSelected()) {
			refreshSeries(statistic.getCommitTime(), SERIES_KEY_COMMIT_TIME);
		} else {
			refreshSeries(emptyMap, SERIES_KEY_COMMIT_TIME);
		}
		
		if(checkBoxShowQueries.isSelected()) {
			refreshSeries(statistic.getQueriesTime(), SERIES_KEY_QUERIES_TIME);
		} else {
			refreshSeries(emptyMap, SERIES_KEY_QUERIES_TIME);
		}
		
		if(checkBoxShowAdd.isSelected()) {
			refreshSeries(statistic.getUpdatesTime(), SERIES_KEY_UPDATES_TIME);
		} else {
			refreshSeries(emptyMap, SERIES_KEY_UPDATES_TIME);
		}
		
		if(checkBoxShowOptimize.isSelected()) {
			refreshSeries(statistic.getOptimizeTime(), SERIES_KEY_OPTIMIZE_TIME);
		} else {
			refreshSeries(emptyMap, SERIES_KEY_OPTIMIZE_TIME);
		}
	}
	
	private void refreshSeries(Map<Long, Long> data, Comparable<?> seriesKey) {
		int i = 0;
		double[][] seriesData;
		
		synchronized(data) {
			seriesData = new double[2][data.size()];
			
			for(Map.Entry<Long, Long> xy: data.entrySet()) {
				seriesData[0][i] = xy.getKey()/1000.0;
				seriesData[1][i] = xy.getValue();
				i++;
			}
		}
		xyDataset.addSeries(seriesKey, seriesData);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				refreshView();
			}
		});
	}

}
