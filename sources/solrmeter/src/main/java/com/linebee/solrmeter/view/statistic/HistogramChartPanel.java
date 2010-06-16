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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.linebee.solrmeter.model.statistic.HistogramQueryStatistic;
import com.linebee.solrmeter.view.I18n;
import com.linebee.solrmeter.view.StatisticPanel;


public class HistogramChartPanel extends StatisticPanel {

	private static final long serialVersionUID = 2779231592485893152L;
	
	private HistogramQueryStatistic histogram;
	
	private JLabel imageLabel;
	
	public HistogramChartPanel(HistogramQueryStatistic histogram) {
		super();
		this.histogram = histogram;
		imageLabel = new JLabel();
		this.add(imageLabel);
	}
	
	@Override
	public String getStatisticName() {
		return I18n.get("statistic.histogramChartPanel.title");
	}
	
	@Override
	public void refresh() {
		Logger.getLogger(this.getClass()).debug("refreshing histogram");
		JFreeChart chart = ChartFactory.createBarChart(I18n.get("statistic.histogramChartPanel.histogram"), 
				I18n.get("statistic.histogramChartPanel.time"), 
				I18n.get("statistic.histogramChartPanel.numberOfQueries"), createDataset(), PlotOrientation.VERTICAL, false, true, false);
		BufferedImage image = chart.createBufferedImage(GRAPH_DEFAULT_WIDTH, GRAPH_DEFAULT_HEIGHT);
		imageLabel.setIcon(new ImageIcon(image));
	}

	private CategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Map<Integer, Integer> histogramData = histogram.getCurrentHisogram();
		if(histogramData.isEmpty()) {
			return dataset;
		}
		
		List<Integer> sortedIntegers = new LinkedList<Integer>(histogramData.keySet());
		Collections.sort(sortedIntegers);
		for(Integer integer:sortedIntegers) {
			dataset.addValue(histogramData.get(integer), "", integer);
		}
		return dataset;
	}


//	@Override
//	public void refresh() {
//		System.out.println("refreshing histogram");
//		JFreeChart chart = ChartFactory.createHistogram("Histogram", "Time", "Nº of Queries", this.createDataset(), PlotOrientation.VERTICAL, false, false, false);
//		BufferedImage image = chart.createBufferedImage(500, 300);
//		imageLabel.setIcon(new ImageIcon(image));
//	}
//
//	private IntervalXYDataset createDataset() {
//		HistogramDataset dataset = new HistogramDataset();
//		dataset.setType(HistogramType.RELATIVE_FREQUENCY);
//		Map<Integer, Integer> histogramData = histogram.getCurrentHisogram();
//		if(histogramData.isEmpty()) {
//			return dataset;
//		}
//		List<Double> values = new ArrayList<Double>();
//		for(Integer integer:histogramData.keySet()) {
//			for(int i = 0; i < histogramData.get(integer); i++) {
//				values.add(new Double(integer));
//			}
//		}
//		dataset.addSeries("uno", getDoubleArray(values) , 30);
//		return dataset;
//	}
//
//	private double[] getDoubleArray(List<Double> values) {
//		double[] array = new double[values.size()];
//		int index = 0;
//		for(Double value:values) {
//			array[index] = value;
//			index++;
//		}
//		return array;
//	}

}
