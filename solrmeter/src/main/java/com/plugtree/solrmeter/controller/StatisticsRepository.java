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
package com.plugtree.solrmeter.controller;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.plugtree.solrmeter.controller.statisticsParser.ParserException;
import com.plugtree.solrmeter.controller.statisticsParser.StatisticsParser;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
/**
 * Repository for all the available statistics
 * @author tflobbe
 *
 */
@Singleton
public class StatisticsRepository {
	
	public final static String PLUGIN_STATISTICS_CONF_FILE_PROPERTY = "pluginsStatisticsConfigFile";
	
	private List<StatisticDescriptor> availableStatistics;
	
	private StatisticsParser statisticParser;
	
	@Inject
	public StatisticsRepository(StatisticsParser statisticParser) {
		super();
		availableStatistics = new LinkedList<StatisticDescriptor>();
		this.statisticParser = statisticParser;
		parseAvailableStatyistics();
	}

	protected void parseAvailableStatyistics() {
		try {
			List<StatisticDescriptor> descriptors = statisticParser.getStatisticDescriptors(SolrMeterConfiguration.getProperty("statistic.configuration.filePath"));
			if(SolrMeterConfiguration.getTransientProperty(PLUGIN_STATISTICS_CONF_FILE_PROPERTY) != null && 
					new File(SolrMeterConfiguration.getTransientProperty(PLUGIN_STATISTICS_CONF_FILE_PROPERTY)).exists()) {
				descriptors.addAll(statisticParser.getStatisticDescriptors(SolrMeterConfiguration.getTransientProperty("pluginsStatisticsConfigFile")));
			}
			availableStatistics.addAll(descriptors);
		} catch (ParserException e) {
			Logger.getLogger(this.getClass()).error("Could not parse statistics file! Non will be available", e);
		}
	}

	/**
	 * Add a statistic description. This statistic will be available
	 * to be enabled from the UI.
	 * @param description
	 */
	public void addStatistic(StatisticDescriptor description) {
		availableStatistics.add(description);
	}

	public List<StatisticDescriptor> getAvailableStatistics() {
		return availableStatistics;
	}

	public List<StatisticDescriptor> getActiveStatistics() {
		String showingStatisticsString = SolrMeterConfiguration.getProperty("statistic.showingStatistics");
		List<StatisticDescriptor> list = new LinkedList<StatisticDescriptor>();
		if(showingStatisticsString == null || showingStatisticsString.isEmpty() || showingStatisticsString.equalsIgnoreCase("all")) {
			list.addAll(availableStatistics);
			return list;
		}
		Set<String> set = new HashSet<String>();
		String[] statisticsNames = showingStatisticsString.split(",");
		for(String name:statisticsNames) {
			set.add(name.trim());
		}
		for(StatisticDescriptor description:availableStatistics) {
			if(set.contains(description.getName()) || !description.isHasView()) {
				list.add(description);
			}
		}
		return list;
	}
}
