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

import com.google.inject.Inject;
import com.linebee.solrmeter.model.statistic.QueryTimeHistoryStatistic;
import com.linebee.solrmeter.view.I18n;
import com.linebee.solrmeter.view.StatisticPanel;
import com.linebee.stressTestScope.StressTestScope;

@StressTestScope
public class QueryTimeHistoryPanel extends StatisticPanel {
	
	private static final long serialVersionUID = 2781214713297030466L;

	private QueryTimeHistoryStatistic queryTimeStatistic;
	
	private JLabel imageLabel;
	
	@Inject
	public QueryTimeHistoryPanel(QueryTimeHistoryStatistic queryTimeStatistic) {
		super();
		this.queryTimeStatistic = queryTimeStatistic;
		imageLabel = new JLabel();
		this.add(imageLabel);
	}

	@Override
	public String getStatisticName() {
		return I18n.get("statistic.queryTimeHistoryPanel.title");
	}

	@Override
	public void refreshView() {
		Logger.getLogger(this.getClass()).debug("refreshing query Time History");
		JFreeChart chart = ChartFactory.createBarChart(I18n.get("statistic.queryTimeHistoryPanel.queryHistory"), 
				I18n.get("statistic.queryTimeHistoryPanel.time"), 
				I18n.get("statistic.queryTimeHistoryPanel.averageQueryTime"),
				createDataset(), PlotOrientation.VERTICAL, false, true, false);
		BufferedImage image = chart.createBufferedImage(GRAPH_DEFAULT_WIDTH, GRAPH_DEFAULT_HEIGHT);
		imageLabel.setIcon(new ImageIcon(image));
	}
	
	private CategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Map<Integer, Integer> histogramData = queryTimeStatistic.getCurrentHistory();
		if(histogramData.isEmpty()) {
			return dataset;
		}
		
		List<Integer> sortedIntegers = new LinkedList<Integer>(histogramData.keySet());
		Collections.sort(sortedIntegers);
		for(Integer category:sortedIntegers) {
			dataset.addValue(histogramData.get(category), "", category);
		}
		return dataset;
	}

}
