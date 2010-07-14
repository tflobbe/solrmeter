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
package com.linebee.solrmeter.view.statistic;

import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import com.google.inject.Inject;
import com.linebee.solrmeter.model.statistic.TimeRange;
import com.linebee.solrmeter.model.statistic.TimeRangeStatistic;
import com.linebee.solrmeter.view.I18n;
import com.linebee.solrmeter.view.StatisticPanel;
import com.linebee.stressTestScope.StressTestScope;

@StressTestScope
public class PieChartPanel extends StatisticPanel {

	private static final long serialVersionUID = -3022639027937641338L;
	
	private JLabel imageLabel;
	
	private TimeRangeStatistic timeRangeStatistic;

	@Inject
	public PieChartPanel(TimeRangeStatistic timeRangeStatistic) {
		super();
		imageLabel = new JLabel();
		this.add(imageLabel);
		this.timeRangeStatistic = timeRangeStatistic;
	}

	@Override
	public String getStatisticName() {
		return I18n.get("statistic.pieChartPanel.title");
	}

	@Override
	public void refreshView() {
		Logger.getLogger(this.getClass()).debug("Refreshing pie chart");
		DefaultPieDataset pieDataset = this.generatePieDataset();
		JFreeChart chart = ChartFactory.createPieChart(
			I18n.get("statistic.pieChartPanel.intervals"),
			pieDataset,
			true,
			true,
			false);
		BufferedImage image = chart.createBufferedImage(GRAPH_DEFAULT_WIDTH, GRAPH_DEFAULT_HEIGHT);
		imageLabel.setIcon(new ImageIcon(image));
	}

	private DefaultPieDataset generatePieDataset() {
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		Map<TimeRange, Integer> percentages = timeRangeStatistic.getActualPercentage();
		for(TimeRange range:percentages.keySet()) {
			pieDataset.setValue(range.toString(), percentages.get(range));
		}
		return pieDataset;
	}
}
