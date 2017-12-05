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

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.RectangleEdge;

import com.google.inject.Inject;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.statistic.CacheData;
import com.plugtree.solrmeter.model.statistic.CacheHistoryStatistic;
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
	
	private static final String collectionsStr = SolrMeterConfiguration.getProperty("solr.collection.names", null);
	
	private String activeCollection = "";

	private static final List<String> collections;
	
	private enum cacheTypes {
		documentCache,
		fieldValueCache,
		filterCache,
		queryResultCache;
		
		private static final String[] cacheTypeValues = 
			stream(cacheTypes.values())
				.map(cacheTypes::toString)
				.collect(Collectors.toList())
				.toArray(new String[0]);
		
	}
	
	//Avoids having to use expensive Reflection calls.
	private final Map<String, Supplier<Map<String, CacheData>>> cumulativeDataMethodMapper;
	private final Map<String, Supplier<Map<String, SortedMap<Long, CacheData>>>> dataMethodMapper;
	private final Map<String, Function<CacheData, Object>> cacheDataMethodMap;
	
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
	
	private JComboBox<String> collectionBox;
	
	/**
	 * The combo box where the user will chose the cache hi wants to see
	 */
	private JComboBox<String> comboBoxCache;
	
	/**
	 * Combo box to select wether to see the hit ratio information or a specific
	 * cache information
	 */
	private JComboBox<String> whatToShowBoxCache;
	
	private static final String SINGLE_COLLECTION = "SINGLE_COLLECTION";
	
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
	
	static {
		List<String> _collections = new ArrayList<>();
		stream(ofNullable(collectionsStr).orElse(SINGLE_COLLECTION).split("\\,")).map(String::trim).forEach(_collections::add);
		collections = Collections.unmodifiableList(_collections);
	} 
	
	{
		activeCollection = collections.get(0);
		
		cumulativeDataMethodMapper = new HashMap<>();
		cumulativeDataMethodMapper.put(cacheTypes.documentCache.toString(), () -> statistic.getDocumentCacheCumulativeData());
		cumulativeDataMethodMapper.put(cacheTypes.fieldValueCache.toString(), () -> statistic.getFieldValueCacheCumulativeData());
		cumulativeDataMethodMapper.put(cacheTypes.filterCache.toString(), () -> statistic.getFilterCacheCumulativeData());
		cumulativeDataMethodMapper.put(cacheTypes.queryResultCache.toString(), () -> statistic.getQueryResultCacheCumulativeData());

		dataMethodMapper = new HashMap<>();
		dataMethodMapper.put(cacheTypes.documentCache.toString(), () -> statistic.getDocumentCacheData());
		dataMethodMapper.put(cacheTypes.fieldValueCache.toString(), () -> statistic.getFieldValueCacheData());
		dataMethodMapper.put(cacheTypes.filterCache.toString(), () -> statistic.getFilterCacheData());
		dataMethodMapper.put(cacheTypes.queryResultCache.toString(), () -> statistic.getQueryResultCacheData());
		
		cacheDataMethodMap = new HashMap<>();
		cacheDataMethodMap.put("Lookups", CacheData::getLookups);
		cacheDataMethodMap.put("Hits", CacheData::getHits);
		cacheDataMethodMap.put("Hitratio", CacheData::getHitratio);
		cacheDataMethodMap.put("Inserts", CacheData::getInserts);
		cacheDataMethodMap.put("Evictions", CacheData::getEvictions);
		cacheDataMethodMap.put("Size", CacheData::getSize);
		cacheDataMethodMap.put("WarmupTime", CacheData::getWarmupTime);
	}

	Supplier<CacheData> selectedCache = () ->
		cumulativeDataMethodMapper.get(comboBoxCache.getSelectedItem().toString()).get().get(activeCollection);
	
	
	Supplier<Map<String, SortedMap<Long,CacheData>>> cacheDataByCollection = () -> 
		dataMethodMapper.get(comboBoxCache.getSelectedItem().toString()).get();

	@SuppressWarnings("unchecked")
	private Consumer<ActionEvent> cacheTypeChange = event -> {
		changeChartTitle("title" + comboBoxCache.getSelectedItem().toString());
		refreshSeries(Collections.EMPTY_MAP);
		refreshView();
	};

	private Consumer<ActionEvent> whatToShowChange = event -> {
		setShowingSpecificData(!whatToShowBoxCache.getSelectedItem().equals(I18n.get("statistic.cacheStatistic.showHitRatio")));
		refreshView();
	};

	
	@SuppressWarnings("unchecked")
	private Consumer<ActionEvent> collectionChange = event -> {
		activeCollection = collectionBox.getSelectedItem().toString();
		changeChartTitle("title" + comboBoxCache.getSelectedItem().toString());
		refreshSeries(Collections.EMPTY_MAP);
		refreshView();
	};
	
	private Runnable delayedAction = () -> refreshView();

	
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
	
		collections.forEach(collectionName -> {
			cumulativeLookupsInfoPanel = new InfoPanel(I18n.get(PREFIX + "Lookups"));
			cumulativeHitsInfoPanel = new InfoPanel(I18n.get(PREFIX + "Hits"));
			cumulativeHitRatioInfoPanel = new InfoPanel(I18n.get(PREFIX + "HitRatio"));
			cumulativeInsertsInfoPanel = new InfoPanel(I18n.get(PREFIX + "Inserts"));
			cumulativeEvictionsInfoPanel = new InfoPanel(I18n.get(PREFIX + "Evictions"));
		});
				
		panel.add(cumulativeLookupsInfoPanel);
		panel.add(cumulativeHitsInfoPanel);
		panel.add(cumulativeHitRatioInfoPanel);
		panel.add(cumulativeInsertsInfoPanel);
		panel.add(cumulativeEvictionsInfoPanel);
		
		return panel;
	}

	/**
	 * Creates the controllers to select hit ratio or caches
	 * @return
	 */
	private List<Component> createControlPanel() {
		List<Component> components = new LinkedList<Component>();
		
		addCollectionsComboBox(components);
		addWhatToShowComboBox(components);
		addCacheComboBox(components);
		
		return components;
	}

	private void addCacheComboBox(List<Component> components) {
		comboBoxCache = createComboBox(cacheTypeChange, cacheTypes.cacheTypeValues);
		components.add(comboBoxCache);
	}
	
	private void addWhatToShowComboBox(List<Component> components) {
		whatToShowBoxCache = 
			createComboBox(whatToShowChange, 
				I18n.get("statistic.cacheStatistic.showHitRatio"),
				I18n.get("statistic.cacheStatistic.showSpecificData"));

		components.add(whatToShowBoxCache);
		components.add(Box.createRigidArea(new Dimension(3, 3)));
	}

	private void addCollectionsComboBox(List<Component> components) {
		if (!collections.isEmpty() && !collections.get(0).contentEquals(SINGLE_COLLECTION)) {
			collectionBox = createComboBox(collectionChange, collections.toArray(new String[0]));
			components.add(collectionBox);
		}
	}
	
	private <T> JComboBox<T> createComboBox(Consumer<ActionEvent> eventListener, T...items) {
		JComboBox<T> box = new JComboBox<>();
		stream(items).forEach(box::addItem);
		box.addActionListener(eventListener::accept);
		return box;
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
		CacheData cacheData = selectedCache.get();

		if (cacheData != null) {
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
		chart = new JFreeChart("notitle", null, plot, true);
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
		char[] collectionName = activeCollection.toCharArray();
		collectionName[0] = Character.toUpperCase(collectionName[0]);
		
		chart.setTitle(String.valueOf(collectionName) + " " + I18n.get(PREFIX + key));
	}
	
	@Override
	public String getStatisticName() {
		return I18n.get("statistic.cacheStatistic.title");
	}

	@Override
	public synchronized void refreshView() {
		if(showingSpecificCacheData) {
			refreshSeries(cacheDataByCollection.get());
			refreshCumulativeData();
		} else {
			refreshHitRatio();
		}
	}

  /**
	 * If showing the hit ratio, this method will be invoked to refresh the chart
	 */
	private void refreshHitRatio() {
		addSeries(statistic.getFilterCacheData().get(activeCollection), "Hitratio", PREFIX + "hitratio.filterCache");
		addSeries(statistic.getDocumentCacheData().get(activeCollection), "Hitratio", PREFIX + "hitratio.documentCache");
		addSeries(statistic.getQueryResultCacheData().get(activeCollection), "Hitratio", PREFIX + "hitratio.queryResultCache");
		addSeries(statistic.getFieldValueCacheData().get(activeCollection), "Hitratio", PREFIX + "hitratio.fieldValueCache");
	}

	/**
	 * If showing specific cache data, this method will be invoked to refresh
	 * @param data
	 */
	private void refreshSeries(Map<String, SortedMap<Long, CacheData>> data) {
		synchronized(data) {
				addSeries(data.get(activeCollection), "Lookups");
				addSeries(data.get(activeCollection), "Hits");
				addSeries(data.get(activeCollection), "Size");
				addSeries(data.get(activeCollection), "Inserts");
				addSeries(data.get(activeCollection), "Evictions");
		}
		
	}
		
	private void addSeries(Map<Long, CacheData> data, String element, String label) {
		if (data == null) {
			return;
		}
		
		int i = 0;
		double[][] seriesData = new double[2][data.size()];

		Function<CacheData, Double> cacheDataValue = cacheDataMethodMap.get(element)
			.andThen(o -> o.toString()).andThen(Double::valueOf);
		Function<Long, Double> timePoint = (time) -> time / 1000.0;
		
		for(Map.Entry<Long, CacheData> xy: data.entrySet()) {
			seriesData[0][i] = timePoint.apply(xy.getKey());
			seriesData[1][i] = cacheDataValue.apply(xy.getValue());
			i++;
		}
		xyDataset.addSeries(I18n.get(label), seriesData);
	}
	
	private void addSeries(Map<Long, CacheData> data, String element) {
		this.addSeries(data, element, PREFIX + element);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater(delayedAction);
	}
}
