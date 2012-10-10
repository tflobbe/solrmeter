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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.RectangleEdge;

import com.google.inject.Inject;
import com.plugtree.solrmeter.model.statistic.CacheData;
import com.plugtree.solrmeter.model.statistic.CacheHistoryStatistic;
import com.plugtree.solrmeter.util.ReflectionUtils;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.StatisticPanel;
import com.plugtree.solrmeter.view.component.InfoPanel;
import com.plugtree.solrmeter.view.component.RoundedBorderJPanel;
import com.plugtree.stressTestScope.StressTestScope;

/**
 * This class will show the Cache history Statistic. This statistic will show a chart with the hit ratio
 * of all caches together OR the data (lookups, hits, insertions and evictions) of a specific cache.
 * @author tomas
 *
 */
@StressTestScope
public class CacheHistoryPanel extends StatisticPanel implements ActionListener {
	
	private static final long serialVersionUID = -154560067788983461L;

	/**
	 * Just for i18n stuff
	 */
	private static final String PREFIX = "statistic.cacheStatistic.";

	/**
	 * The dataset used in the chart.
	 */
	private DefaultXYDataset xyDataset = new DefaultXYDataset();
	
	/**
	 * The model class for this statistic
	 */
	private CacheHistoryStatistic statistic;
	
	/**
	 * The combo box where the user will chose the cache hi wants to see
	 */
	private JComboBox comboBoxCache;
	
	/**
	 * Combo box to select wether to see the hit ratio information or a specific
	 * cache information
	 */
	private JComboBox whatToShowBoxCache;
	
	/**
	 * Info Panel to display comulative lookups of the selected cache
	 */
	private InfoPanel cumulativeLookupsInfoPanel;
	
	/**
	 * Info Panel to display comulative hits of the selected cache
	 */
	private InfoPanel cumulativeHitsInfoPanel;
	
	/**
	 * Info Panel to display comulative hit ratio of the selected cache
	 */
	private InfoPanel cumulativeHitRatioInfoPanel;
	
	/**
	 * Info Panel to display comulative inserts of the selected cache
	 */
	private InfoPanel cumulativeInsertsInfoPanel;
	
	/**
	 * Info Panel to display comulative evictions of the selected cache
	 */
	private InfoPanel cumulativeEvictionsInfoPanel;
	
	/**
	 * Boolean value that indicates if the user is displaying hit ratio information 
	 * or specific cache data
	 */
	private boolean showingSpecificCacheData = false;
	
	/**
	 * JPanel to hold all the cumulative information
	 */
	private JPanel cumulativeDataPanel;
	
	/**
	 * Chart that shows the data
	 */
	private JFreeChart chart;
	
	/**
	 * Plot of the above chart
	 */
	private XYPlot plot;
	
	/**
	 * Constructor to be injected by Guice
	 * @param statistic
	 */
	@Inject
	public CacheHistoryPanel(CacheHistoryStatistic statistic) {
		super();
		this.statistic = statistic;
		this.xyDataset = new DefaultXYDataset();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.add(this.createLeftPanel());
		this.add(this.createChartPanel());
		this.setShowingSpecificData(false);//Show the hit ratio chart as default
	}
	
	/**
	 * Creates the left panel, with the combo boxes to select wether to see hit ratio
	 * or specific cache data, and if specific cache data is selected, will also show
	 * the combo box to select the cache and the cumulative information
	 * @return
	 */
	private Component createLeftPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(1,1,1,1);
		for(Component component: this.createControlPanel()) {
			panel.add(component, constraints);
			constraints.gridy++;
		}
		cumulativeDataPanel = this.createCumulativeDataPanel(); 
		panel.add(cumulativeDataPanel, constraints);
		constraints.gridy++;
		constraints.weighty = 2.0;
		panel.add(Box.createVerticalGlue(), constraints);
		panel.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
		
		return panel;
	}

	/**
	 * Creates the cumulative data panel
	 * @return
	 */
	private JPanel createCumulativeDataPanel() {
		JPanel panel = new RoundedBorderJPanel("Cumulative Data");
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		cumulativeLookupsInfoPanel = new InfoPanel(I18n.get(PREFIX + "Lookups"));
		cumulativeHitsInfoPanel = new InfoPanel(I18n.get(PREFIX + "Hits"));
		cumulativeHitRatioInfoPanel = new InfoPanel(I18n.get(PREFIX + "HitRatio"));
		cumulativeInsertsInfoPanel = new InfoPanel(I18n.get(PREFIX + "Inserts"));
		cumulativeEvictionsInfoPanel = new InfoPanel(I18n.get(PREFIX + "Evictions"));
		panel.add(cumulativeLookupsInfoPanel);
		panel.add(cumulativeHitsInfoPanel);
		panel.add(cumulativeHitRatioInfoPanel);
		panel.add(cumulativeInsertsInfoPanel);
		panel.add(cumulativeEvictionsInfoPanel);
		return panel;
	}

	/**
	 * Creates the controlers to select hit ratio or caches
	 * @return
	 */
	private List<Component> createControlPanel() {
		List<Component> components = new LinkedList<Component>();
		whatToShowBoxCache = new JComboBox();
		whatToShowBoxCache.addItem(I18n.get("statistic.cacheStatistic.showHitRatio"));
		whatToShowBoxCache.addItem(I18n.get("statistic.cacheStatistic.showSpecificData"));
		whatToShowBoxCache.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setShowingSpecificData(!whatToShowBoxCache.getSelectedItem().equals(I18n.get("statistic.cacheStatistic.showHitRatio")));
				refreshView();
			}

		});
		components.add(whatToShowBoxCache);
		components.add(Box.createRigidArea(new Dimension(3, 3)));
		
		comboBoxCache = new JComboBox();
		comboBoxCache.addItem("documentCache");
		comboBoxCache.addItem("fieldValueCache");
		comboBoxCache.addItem("filterCache");
		comboBoxCache.addItem("queryResultCache");
		comboBoxCache.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changeChartTitle("title" + comboBoxCache.getSelectedItem().toString());
				refreshSeries(Collections.EMPTY_MAP);
				refreshView();
			}

		});
		components.add(comboBoxCache);
		return components;
	}
	
	/**
	 * Invoke this method when the selection of what to show changes
	 * @param b
	 */
	private void setShowingSpecificData(boolean b) {
		showingSpecificCacheData = b;
		comboBoxCache.setEnabled(showingSpecificCacheData);
		cumulativeDataPanel.setVisible(showingSpecificCacheData);
		if(b) {
			plot.getRangeAxis().setAutoRange(true);
			changeChartTitle("title" + comboBoxCache.getSelectedItem().toString());
		} else {
			plot.getRangeAxis().setRange(new Range(0, 1));
			plot.getRangeAxis().setAutoRange(false);
			changeChartTitle("titleHitRatio");
		}
		clearChart();
		
	}
	
	/**
	 * Removes all series from the chart. To be invoked when the display information
	 * changes from hit ratio to specific cache data or viceversa, not when the selected
	 * cache changes
	 */
	private void clearChart() {
		xyDataset.removeSeries(I18n.get(PREFIX + "Lookups"));
		xyDataset.removeSeries(I18n.get(PREFIX + "Hits"));
		xyDataset.removeSeries(I18n.get(PREFIX + "Size"));
		xyDataset.removeSeries(I18n.get(PREFIX + "Inserts"));
		xyDataset.removeSeries(I18n.get(PREFIX + "Evictions"));
		xyDataset.removeSeries(I18n.get(PREFIX + "hitratio.filterCache"));
		xyDataset.removeSeries(I18n.get(PREFIX + "hitratio.documentCache"));
		xyDataset.removeSeries(I18n.get(PREFIX + "hitratio.queryResultCache"));
		xyDataset.removeSeries(I18n.get(PREFIX + "hitratio.fieldValueCache"));
	}

	/**
	 * Refreshes the cumulative cache information
	 */
	private void refreshCumulativeData() {
		CacheData cacheData = (CacheData) ReflectionUtils.getAttribute(statistic, comboBoxCache.getSelectedItem().toString() + "CumulativeData");
		if(cacheData != null) {
			cumulativeLookupsInfoPanel.setValue(String.valueOf(cacheData.getLookups()));
			cumulativeHitsInfoPanel.setValue(String.valueOf(cacheData.getHits()));
			cumulativeHitRatioInfoPanel.setValue(String.valueOf(cacheData.getHitratio()));
			cumulativeInsertsInfoPanel.setValue(String.valueOf(cacheData.getInserts()));
			cumulativeEvictionsInfoPanel.setValue(String.valueOf(cacheData.getEvictions()));
		} else {
		  cumulativeLookupsInfoPanel.setValue("");
      cumulativeHitsInfoPanel.setValue("");
      cumulativeHitRatioInfoPanel.setValue("");
      cumulativeInsertsInfoPanel.setValue("");
      cumulativeEvictionsInfoPanel.setValue("");
		}
		
	}

	/**
	 * Creates the chart of this statistic
	 * @return
	 */
	private Component createChartPanel() {
		NumberAxis xaxis = new NumberAxis(I18n.get(PREFIX + "time"));
		NumberAxis yaxis = new NumberAxis(I18n.get(PREFIX + "entries"));
		
		plot = new XYPlot(xyDataset, xaxis, yaxis, new XYLineAndShapeRenderer(true, true));
		
		chart = new JFreeChart("notitle",
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
	
	/**
	 * Changes the title of the chart
	 * @param key is the last part of the i18n key
	 */
	private void changeChartTitle(String key) {
		chart.setTitle(I18n.get(PREFIX + key));
	}
	
	@Override
	public String getStatisticName() {
		return I18n.get("statistic.cacheStatistic.title");
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void refreshView() {
		Logger.getLogger(this.getClass()).debug("Cache History Panel");
		if(showingSpecificCacheData) {
			refreshSeries((Map<Long, CacheData>)ReflectionUtils.getAttribute(statistic, comboBoxCache.getSelectedItem().toString() + "Data"));
			refreshCumulativeData();
		} else {
			refreshHitRatio();
		}
	}

  /**
	 * If showing the hit ratio, this method will be invoked to refresh the chart
	 */
	private void refreshHitRatio() {
//		synchronized(statistic) {
			addSerie(statistic.getFilterCacheData(), "hitratio", PREFIX + "hitratio.filterCache");
			addSerie(statistic.getDocumentCacheData(), "hitratio", PREFIX + "hitratio.documentCache");
			addSerie(statistic.getQueryResultCacheData(), "hitratio", PREFIX + "hitratio.queryResultCache");
			addSerie(statistic.getFieldValueCacheData(), "hitratio", PREFIX + "hitratio.fieldValueCache");
			
//		}
	}

	/**
	 * If showing specific cache data, this method will be invoked to refresh
	 * @param data
	 */
	private void refreshSeries(Map<Long, CacheData> data) {
		synchronized(data) {
			addSerie(data, "Lookups");
			addSerie(data, "Hits");
			addSerie(data, "Size");
			addSerie(data, "Inserts");
			addSerie(data, "Evictions");
		}
		
	}
	
	private void addSerie(Map<Long, CacheData> data, String element, String label) {
		int i = 0;
		double[][] seriesData;
		seriesData = new double[2][data.size()];
		
		for(Map.Entry<Long, CacheData> xy: data.entrySet()) {
			seriesData[0][i] = xy.getKey()/1000.0;
			try {
				seriesData[1][i] = Double.valueOf(ReflectionUtils.getAttribute(xy.getValue(), element).toString());
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
			i++;
		}
		xyDataset.addSeries(I18n.get(label), seriesData);
	}
	
	private void addSerie(Map<Long, CacheData> data, String element) {
		this.addSerie(data, element, PREFIX + element);
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
