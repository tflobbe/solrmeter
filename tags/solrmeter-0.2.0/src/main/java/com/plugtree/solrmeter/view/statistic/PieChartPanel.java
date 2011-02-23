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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;

import com.google.inject.Inject;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.SolrMeterMain;
import com.plugtree.solrmeter.model.statistic.TimeRange;
import com.plugtree.solrmeter.model.statistic.TimeRangeStatistic;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.StatisticPanel;

@StressTestScope
public class PieChartPanel extends StatisticPanel {

	private static final long serialVersionUID = -3022639027937641338L;
	
	private DefaultPieDataset pieDataset;
	
	private TimeRangeStatistic timeRangeStatistic;

	@Inject
	public PieChartPanel(TimeRangeStatistic timeRangeStatistic) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.timeRangeStatistic = timeRangeStatistic;
		this.pieDataset = new DefaultPieDataset();
		this.add(this.createChartPanel());
		this.add(this.createCustomizePanel());
	}

	private Component createCustomizePanel() {
		JButton jButtonCustomize = new JButton(I18n.get("statistic.pieChartPanel.customize"));
		jButtonCustomize.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JDialogCustomizePieChart dialog = new JDialogCustomizePieChart(SolrMeterMain.mainFrame, timeRangeStatistic);
				dialog.setVisible(true);
			}
			
		});
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
		panel.add(Box.createHorizontalGlue());
		panel.add(jButtonCustomize);
		
		return panel;
	}
	
	private Component createChartPanel() {
		PiePlot plot = new PiePlot(pieDataset);
		
		JFreeChart chart = new JFreeChart(
				I18n.get("statistic.pieChartPanel.title"),
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

	@Override
	public String getStatisticName() {
		return I18n.get("statistic.pieChartPanel.title");
	}

	@Override
	public void refreshView() {
		Logger.getLogger(this.getClass()).debug("Refreshing pie chart");
		
		pieDataset.clear();
		
		Map<TimeRange, Integer> percentages = timeRangeStatistic.getActualPercentage();
		for(TimeRange range: percentages.keySet()) {
			pieDataset.setValue(range.toString(), percentages.get(range));
		}
	}

}
