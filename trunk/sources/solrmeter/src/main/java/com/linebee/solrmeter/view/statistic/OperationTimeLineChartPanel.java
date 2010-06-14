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
package com.linebee.solrmeter.view.statistic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.linebee.solrmeter.model.statistic.OperationTimeHistory;
import com.linebee.solrmeter.view.I18n;
import com.linebee.solrmeter.view.StatisticPanel;

public class OperationTimeLineChartPanel extends StatisticPanel implements ActionListener {

	private static final long serialVersionUID = 3661614439597184136L;

	private JLabel imageLabel;
	
	private OperationTimeHistory statistic;
	
	private JCheckBox checkBoxShowQueries;
	private JCheckBox checkBoxShowOptimize;
	private JCheckBox checkBoxShowAdd;
	private JCheckBox checkBoxShowCommit;
	
	public OperationTimeLineChartPanel(OperationTimeHistory statistic) {
		super();
		this.statistic = statistic;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(this.createCheckBoxPanel());
		imageLabel = new JLabel();
		JPanel auxPanel = new JPanel();
		auxPanel.add(imageLabel);
		this.add(auxPanel);
	}
	
	private Component createCheckBoxPanel() {
		JPanel panelCheckBox = new JPanel();
		panelCheckBox.setLayout(new BoxLayout(panelCheckBox, BoxLayout.X_AXIS));
		checkBoxShowCommit = new JCheckBox(I18n.get("statistic.operationTimeLineChartPanel.checkbox.commit"));
		checkBoxShowCommit.addActionListener(this);
		checkBoxShowOptimize = new JCheckBox(I18n.get("statistic.operationTimeLineChartPanel.checkbox.optimize"));
		checkBoxShowOptimize.addActionListener(this);
		checkBoxShowAdd = new JCheckBox(I18n.get("statistic.operationTimeLineChartPanel.checkbox.add"));
		checkBoxShowAdd.addActionListener(this);
		checkBoxShowQueries = new JCheckBox(I18n.get("statistic.operationTimeLineChartPanel.checkbox.query"));
		checkBoxShowQueries.addActionListener(this);
		panelCheckBox.add(checkBoxShowCommit);
		panelCheckBox.add(checkBoxShowOptimize);
		panelCheckBox.add(checkBoxShowQueries);
		panelCheckBox.add(checkBoxShowAdd);
		panelCheckBox.add(Box.createHorizontalGlue());
		panelCheckBox.setMaximumSize(new Dimension(800, 25));
		checkAll();
		return panelCheckBox;
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
	public synchronized void refresh() {
		Logger.getLogger(this.getClass()).debug("refreshing Time Line");
		JFreeChart chart = ChartFactory.createXYLineChart(I18n.get("statistic.operationTimeLineChartPanel.timeLine"), 
				I18n.get("statistic.operationTimeLineChartPanel.executionInstance"), 
				I18n.get("statistic.operationTimeLineChartPanel.qTime"), createDataset(), PlotOrientation.VERTICAL, true, true, false);
		BufferedImage image = chart.createBufferedImage(GRAPH_DEFAULT_WIDTH, GRAPH_DEFAULT_HEIGHT);
		imageLabel.setIcon(new ImageIcon(image));

	}

	private XYDataset createDataset() {
		XYSeriesCollection seriesCollection = new XYSeriesCollection();
		if(checkBoxShowQueries.isSelected()) {
			seriesCollection.addSeries(this.createSeries(I18n.get("statistic.operationTimeLineChartPanel.query"), statistic.getQueriesTime()));
		}
		if(checkBoxShowOptimize.isSelected()) {
			seriesCollection.addSeries(this.createSeries(I18n.get("statistic.operationTimeLineChartPanel.optimize"), statistic.getOptimizeTime()));
		}
		if(checkBoxShowAdd.isSelected()) {
			seriesCollection.addSeries(this.createSeries(I18n.get("statistic.operationTimeLineChartPanel.add"), statistic.getUpdatesTime()));
		}
		if(checkBoxShowCommit.isSelected()) {
			seriesCollection.addSeries(this.createSeries(I18n.get("statistic.operationTimeLineChartPanel.commit"), statistic.getCommitTime()));
		}
		return seriesCollection;
	}

	private XYSeries createSeries(String string, Map<Long, Long> operations) {
		XYSeries series = new XYSeries(string);
		for(Long executionInstant:operations.keySet()) {
			series.add(executionInstant, operations.get(executionInstant));
		}
		return series;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				refresh();
			}
		};
		thread.start();
	}

}
