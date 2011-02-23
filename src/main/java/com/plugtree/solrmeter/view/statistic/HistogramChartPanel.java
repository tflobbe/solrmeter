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
import com.plugtree.solrmeter.model.statistic.HistogramQueryStatistic;
import com.plugtree.solrmeter.util.ChartUtils;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.StatisticPanel;

@StressTestScope
public class HistogramChartPanel extends StatisticPanel {

	private static final long serialVersionUID = 2779231592485893152L;
	
	private static final String SERIES_KEY = "histogram";
	
	private static final double BAR_WIDTH = HistogramQueryStatistic.HISTOGRAM_INTERVAL*0.9;
	
	private static final double LOWER_TICK_UNIT = HistogramQueryStatistic.HISTOGRAM_INTERVAL;
	
	private HistogramQueryStatistic histogram;
	
	private DefaultXYDataset xyDataset;
	
	@Inject
	public HistogramChartPanel(HistogramQueryStatistic histogram) {
		super();
		this.histogram = histogram;
		this.xyDataset = new DefaultXYDataset();
		this.add(createChartPanel());
	}
	
	private Component createChartPanel() {
		XYBarDataset xyBarDataset = new XYBarDataset(xyDataset, BAR_WIDTH);
		NumberAxis xaxis = new NumberAxis(I18n.get("statistic.histogramChartPanel.time"));
		NumberAxis yaxis = new NumberAxis(I18n.get("statistic.histogramChartPanel.numberOfQueries"));
		
		xaxis.setStandardTickUnits(new ChartUtils.LowerBoundedTickUnitSource(xaxis.getStandardTickUnits(), LOWER_TICK_UNIT));
		
		XYPlot plot = new XYPlot(xyBarDataset, xaxis, yaxis, new XYBarRenderer());
		
		JFreeChart chart = new JFreeChart(I18n.get(
				"statistic.histogramChartPanel.title"),
				null, plot, false);
		
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
		return I18n.get("statistic.histogramChartPanel.title");
	}
	
	@Override
	public synchronized void refreshView() {
		Logger.getLogger(this.getClass()).debug("refreshing histogram");
		
		Map<Integer, Integer> histogramData = histogram.getCurrentHisogram();
		double[][] data = new double[2][histogramData.size()];
		
		int i =0;
		for(Map.Entry<Integer, Integer> entry: histogramData.entrySet()) {
			data[0][i] = entry.getKey().doubleValue();
			data[1][i] = entry.getValue().doubleValue();
			i++;
		}
		
		xyDataset.addSeries(SERIES_KEY, data);
	}

}
