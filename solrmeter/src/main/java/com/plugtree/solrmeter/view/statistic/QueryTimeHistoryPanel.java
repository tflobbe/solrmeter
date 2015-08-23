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

import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYBarDataset;

import com.google.inject.Inject;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.model.statistic.QueryTimeHistoryStatistic;
import com.plugtree.solrmeter.util.ChartUtils;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.StatisticPanel;

@StressTestScope
public class QueryTimeHistoryPanel extends StatisticPanel {
	
	private static final long serialVersionUID = 2781214713297030466L;
	
	private static final int LOWER_TICK_UNIT = 10;
	
	private static final int BAR_WIDTH = LOWER_TICK_UNIT-1;
	
	private static final String SERIES_KEY = "queryTime";

	private QueryTimeHistoryStatistic queryTimeStatistic;
	
	private DefaultXYDataset dataset;
	
	@Inject
	public QueryTimeHistoryPanel(QueryTimeHistoryStatistic queryTimeStatistic) {
		super();
		this.queryTimeStatistic = queryTimeStatistic;
		this.dataset = new DefaultXYDataset();
		this.add(createChartPanel());
	}

	@Override
	public String getStatisticName() {
		return I18n.get("statistic.queryTimeHistoryPanel.title");
	}

	@Override
	public void refreshView() {
		Logger.getLogger(this.getClass()).debug("refreshing query Time History");
		
		Map<Integer, Integer> histogramData = queryTimeStatistic.getCurrentHistory();
		int size = histogramData.size();
		double[][] data = new double[2][size];
		
		int i=0;
		for(Map.Entry<Integer, Integer> entry: histogramData.entrySet()) {
			data[0][i] = entry.getKey().doubleValue();
			data[1][i] = entry.getValue().doubleValue();
			i++;
		}
		
		dataset.addSeries(SERIES_KEY, data);
	}
	
	/**
	 * Creates and initializes the chart panel.
	 */
	public ChartPanel createChartPanel() {
		XYBarDataset barDataset = new XYBarDataset(dataset, BAR_WIDTH);
		NumberAxis xaxis = new NumberAxis(I18n.get("statistic.queryTimeHistoryPanel.time"));
		NumberAxis yaxis = new NumberAxis(I18n.get("statistic.queryTimeHistoryPanel.averageQueryTime"));
		
		xaxis.setStandardTickUnits(new ChartUtils.LowerBoundedTickUnitSource(xaxis.getStandardTickUnits(), LOWER_TICK_UNIT));
		
		XYPlot plot = new XYPlot(barDataset, xaxis, yaxis, new XYBarRenderer());
		
		JFreeChart chart = new JFreeChart(I18n.get("statistic.queryTimeHistoryPanel.queryHistory"),
				null, plot, false);
		
		ChartPanel chartPanel = new ChartPanel(chart);
		
		chartPanel.setBorder(CHART_BORDER);
		chartPanel.setMinimumDrawHeight(0);
		chartPanel.setMinimumDrawWidth(0);
		chartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
		chartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
		
		return chartPanel;
	}

}
