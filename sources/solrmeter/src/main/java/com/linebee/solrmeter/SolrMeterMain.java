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
package com.linebee.solrmeter;

import java.awt.MenuBar;

import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.linebee.solrmeter.model.OptimizeExecutor;
import com.linebee.solrmeter.model.QueryExecutor;
import com.linebee.solrmeter.model.QueryStatistic;
import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.model.UpdateExecutor;
import com.linebee.solrmeter.model.statistic.CommitHistoryStatistic;
import com.linebee.solrmeter.model.statistic.ErrorLogStatistic;
import com.linebee.solrmeter.model.statistic.FullQueryStatistic;
import com.linebee.solrmeter.model.statistic.HistogramQueryStatistic;
import com.linebee.solrmeter.model.statistic.OperationTimeHistory;
import com.linebee.solrmeter.model.statistic.QueryLogStatistic;
import com.linebee.solrmeter.model.statistic.QueryTimeHistoryStatistic;
import com.linebee.solrmeter.model.statistic.SimpleOptimizeStatistic;
import com.linebee.solrmeter.model.statistic.SimpleQueryStatistic;
import com.linebee.solrmeter.model.statistic.TimeRangeStatistic;
import com.linebee.solrmeter.view.ConsoleFrame;
import com.linebee.solrmeter.view.I18n;
import com.linebee.solrmeter.view.Model;
import com.linebee.solrmeter.view.OptimizeConsolePanel;
import com.linebee.solrmeter.view.QueryConsolePanel;
import com.linebee.solrmeter.view.StatisticPanel;
import com.linebee.solrmeter.view.StatisticsContainer;
import com.linebee.solrmeter.view.UpdateConsolePanel;
import com.linebee.solrmeter.view.statistic.ErrorLogPanel;
import com.linebee.solrmeter.view.statistic.FullQueryStatisticPanel;
import com.linebee.solrmeter.view.statistic.HistogramChartPanel;
import com.linebee.solrmeter.view.statistic.OperationTimeLineChartPanel;
import com.linebee.solrmeter.view.statistic.PieChartPanel;
import com.linebee.solrmeter.view.statistic.QueryTimeHistoryPanel;
import com.linebee.stressTestScope.StressTestRegistry;
import com.linebee.stressTestScope.StressTestScopeModule;

/**
 * 
 * @author tflobbe
 *
 */
public class SolrMeterMain {
	
	public static ConsoleFrame mainFrame;

	public static void main(String[] args) throws Exception {
		Injector injector = createInjector();
		loadLookAndFeel();
		initModel(injector);
		initView(injector);
		addStatistics(injector);
	}
	
	private static Injector createInjector() {
		Injector injector = Guice.createInjector(
				createModule("guice.statisticsModule"),
				createModule("guice.modelModule"),
				createModule("guice.standalonePresentationModule"),
				new StressTestScopeModule());
		StressTestRegistry.start();
		return injector;
	}

	private static Module createModule(String moduleKey) {
		String statisticsModuleClass = SolrMeterConfiguration.getProperty(moduleKey);
		Logger.getLogger(SolrMeterMain.class).info("Using module: " + statisticsModuleClass);
		Class<?> moduleClass;
		try {
			moduleClass = Class.forName(statisticsModuleClass);
		} catch (ClassNotFoundException e) {
			Logger.getLogger(SolrMeterMain.class).error("Module for name " + statisticsModuleClass + " can't be found! Make sure it is in classpath.", e);
			throw new RuntimeException("Could not start application, module for name " + statisticsModuleClass + " was not found.", e);
		}
		Module moduleInstance;
		try {
			moduleInstance = (Module) moduleClass.newInstance();
		} catch (Exception e) {
			Logger.getLogger(SolrMeterMain.class).error("Module for name " + statisticsModuleClass + " could not be instantiated.", e);
			throw new RuntimeException("Module for name " + statisticsModuleClass + " could not be instantiated.", e);
		}
		return moduleInstance;
	}

	public static void restartApplication() {
		StressTestRegistry.restart();
		Injector injector = createInjector();
		I18n.onConfigurationChange();
		initModel(injector);
		mainFrame.setQueryPanel(injector.getInstance(QueryConsolePanel.class));
		mainFrame.setUpdatePanel(injector.getInstance(UpdateConsolePanel.class));
		mainFrame.setOptimizePanel(injector.getInstance(OptimizeConsolePanel.class));
		mainFrame.setMenuBar(injector.getInstance(MenuBar.class));
		mainFrame.onConfigurationChanged();
		addStatistics(injector);
	}

	private static void initView(Injector injector) {
		mainFrame = injector.getInstance(ConsoleFrame.class);
		mainFrame.setVisible(true);
		mainFrame.setTitle(I18n.get("mainFrame.title"));
	}

	private static void loadLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			Logger.getLogger(SolrMeterMain.class).error("Error loading look and feel. Will Continue with default.", e);
		}
	}

	private static void initModel(Injector injector) {
		initQueryModel(injector);
		initUpdateModel(injector);
		initOptimizeModel(injector);
	}

	private static void addOperationHistoryStatistic(Injector injector) {
		OperationTimeHistory statistic = injector.getInstance(OperationTimeHistory.class);
		statistic.prepare();
		Model.getInstance().getCurrentQueryExecutor().addStatistic(statistic);
		Model.getInstance().getCurrentUpdateExecutor().addStatistic(statistic);
		Model.getInstance().getCurrentOptimizeExecutor().addStatistic(statistic);
		
	}

	private static void addErrorLogStatistic(Injector injector) {
		ErrorLogStatistic statistic =  injector.getInstance(ErrorLogStatistic.class);
		statistic.prepare();
		Model.getInstance().getCurrentQueryExecutor().addStatistic(statistic);
		Model.getInstance().getCurrentUpdateExecutor().addStatistic(statistic);
		Model.getInstance().getCurrentOptimizeExecutor().addStatistic(statistic);
	}

	private static void initOptimizeModel(Injector injector) {
		OptimizeExecutor executor = injector.getInstance(OptimizeExecutor.class);
		Model.getInstance().setOptimizeExecutor(executor);
	}

	private static void initUpdateModel(Injector injector) {
		UpdateExecutor executor = injector.getInstance(UpdateExecutor.class);
		Model.getInstance().setUpdateExecutor(executor);
		
	}

	private static void initQueryModel(Injector injector) {
		QueryExecutor queryExecutor = injector.getInstance(QueryExecutor.class);
		Model.getInstance().setQueryExecutor(queryExecutor);
	}
	
	/*
	 * TODO refactor this on issue issue #21
	 */
	private static void addStatistics(Injector injector) {
		QueryExecutor queryExecutor = injector.getInstance(QueryExecutor.class);
		addStatistic(queryExecutor, injector, HistogramQueryStatistic.class);
		addStatistic(queryExecutor, injector, QueryTimeHistoryStatistic.class);
		addStatistic(queryExecutor, injector, TimeRangeStatistic.class);
		addStatistic(queryExecutor, injector, SimpleQueryStatistic.class);
		addStatistic(queryExecutor, injector, FullQueryStatistic.class);
		addStatistic(queryExecutor, injector, QueryLogStatistic.class);
		
		UpdateExecutor updateExecutor = injector.getInstance(UpdateExecutor.class);
		updateExecutor.addStatistic(injector.getInstance(CommitHistoryStatistic.class));
		
		OptimizeExecutor optimizeExecutor = injector.getInstance(OptimizeExecutor.class);
		optimizeExecutor.addStatistic(injector.getInstance(SimpleOptimizeStatistic.class));
		
		addErrorLogStatistic(injector);
		addOperationHistoryStatistic(injector);
		
		addStatictic(mainFrame.getStatisticsContainer(), injector, PieChartPanel.class);
		addStatictic(mainFrame.getStatisticsContainer(), injector, HistogramChartPanel.class);
		addStatictic(mainFrame.getStatisticsContainer(), injector, QueryTimeHistoryPanel.class);
		addStatictic(mainFrame.getStatisticsContainer(), injector, ErrorLogPanel.class);
		addStatictic(mainFrame.getStatisticsContainer(), injector, OperationTimeLineChartPanel.class);
		addStatictic(mainFrame.getStatisticsContainer(), injector, FullQueryStatisticPanel.class);
		
		
	}

	private static void addStatictic(StatisticsContainer statisticsContainer,
			Injector injector, Class<? extends StatisticPanel> clazz) {
		statisticsContainer.addStatistic(injector.getInstance(clazz));
		
	}
	
	private static void addStatistic(QueryExecutor queryExecutor, Injector injector,
			Class<? extends QueryStatistic> modelKey) {
		queryExecutor.addStatistic(injector.getInstance(modelKey));
	}
	
	
}
