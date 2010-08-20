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
import java.io.File;
import java.io.IOException;

import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.linebee.solrmeter.controller.StatisticDescriptor;
import com.linebee.solrmeter.controller.StatisticType;
import com.linebee.solrmeter.controller.StatisticsRepository;
import com.linebee.solrmeter.model.OptimizeExecutor;
import com.linebee.solrmeter.model.OptimizeStatistic;
import com.linebee.solrmeter.model.QueryExecutor;
import com.linebee.solrmeter.model.QueryStatistic;
import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.model.UpdateExecutor;
import com.linebee.solrmeter.model.UpdateStatistic;
import com.linebee.solrmeter.view.ConsoleFrame;
import com.linebee.solrmeter.view.I18n;
import com.linebee.solrmeter.view.OptimizeConsolePanel;
import com.linebee.solrmeter.view.QueryConsolePanel;
import com.linebee.solrmeter.view.QueryPanel;
import com.linebee.solrmeter.view.StatisticPanel;
import com.linebee.solrmeter.view.StatisticsContainer;
import com.linebee.solrmeter.view.UpdateConsolePanel;
import com.linebee.stressTestScope.StressTestRegistry;
import com.linebee.stressTestScope.StressTestScopeModule;

/**
 * 
 * @author tflobbe
 *
 */
public class SolrMeterMain {
	
	public static ConsoleFrame mainFrame;
	
	private static Injector injector;

	public static void main(String[] args) throws Exception {
		addPlugins(new ExpectedParameter(args, "statisticsLocation", "./plugins").getValue());
		injector = createInjector();
		StressTestRegistry.start();
		loadLookAndFeel();
		initView(injector);
		addStatistics(injector);
		addQueryPanel(injector);
	}
	
	private static void addPlugins(String statisticsPath) {
		try {
			Logger.getLogger("boot").info("Adding plugins from " + statisticsPath);
			File pluginsDir = new File(statisticsPath);
			if(!pluginsDir.exists() || pluginsDir.list().length == 0) {
				Logger.getLogger("boot").warn("No plugins directory found. No pluggin added");
				return;
			}
			for(String jarName:pluginsDir.list()) {
				if(jarName.endsWith(".jar")) {
					Logger.getLogger("boot").info("Adding file " + jarName + " to classpath.");
					ClassPathHacker.addFile(new File(pluginsDir, jarName));
				}
			}
			SolrMeterConfiguration.setTransientProperty(StatisticsRepository.PLUGIN_STATISTICS_CONF_FILE_PROPERTY, statisticsPath + "/statistics-config.xml");
		} catch (IOException e) {
			Logger.getLogger("boot").error("Error while adding plugins to classpath", e);
			throw new RuntimeException(e);
		}
	}

	private static Injector createInjector() {
		Injector injector = Guice.createInjector(
				createModule("guice.statisticsModule"),
				createModule("guice.modelModule"),
				createModule("guice.standalonePresentationModule"),
				new StressTestScopeModule());
		return injector;
	}

	private static Module createModule(String moduleKey) {
		String moduleClassName = SolrMeterConfiguration.getProperty(moduleKey);
		Logger.getLogger(SolrMeterMain.class).info("Using module: " + moduleClassName);
		Class<?> moduleClass;
		try {
			moduleClass = Class.forName(moduleClassName);
		} catch (ClassNotFoundException e) {
			Logger.getLogger(SolrMeterMain.class).error("Module for name " + moduleClassName + " can't be found! Make sure it is in classpath.", e);
			throw new RuntimeException("Could not start application, module for name " + moduleClassName + " was not found.", e);
		}
		Module moduleInstance;
		try {
			moduleInstance = (Module) moduleClass.newInstance();
		} catch (Exception e) {
			Logger.getLogger(SolrMeterMain.class).error("Module for name " + moduleClassName + " could not be instantiated.", e);
			throw new RuntimeException("Module for name " + moduleClassName + " could not be instantiated.", e);
		}
		return moduleInstance;
	}

	public static void restartApplication() {
		StressTestRegistry.restart();
		I18n.onConfigurationChange();
		mainFrame.setQueryPanel(injector.getInstance(QueryConsolePanel.class));
		mainFrame.setUpdatePanel(injector.getInstance(UpdateConsolePanel.class));
		mainFrame.setOptimizePanel(injector.getInstance(OptimizeConsolePanel.class));
		mainFrame.setMenuBar(injector.getInstance(MenuBar.class));
		mainFrame.onConfigurationChanged();
		addStatistics(injector);
		addQueryPanel(injector);
	}

	private static void addQueryPanel(Injector injector2) {
		mainFrame.getStatisticsContainer().addStatistic(injector.getInstance(QueryPanel.class));
		
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
	
	private static void addStatistics(Injector injector) {
		QueryExecutor queryExecutor = injector.getInstance(QueryExecutor.class);
		UpdateExecutor updateExecutor = injector.getInstance(UpdateExecutor.class);
		OptimizeExecutor optimizeExecutor = injector.getInstance(OptimizeExecutor.class);
		StatisticsRepository repository = injector.getInstance(StatisticsRepository.class);
		for(StatisticDescriptor stat:repository.getActiveStatistics()) {
			Logger.getLogger("boot").info("Adding Statistic " + stat.getName());
			if(stat.isHasView()) {
				addStatictic(mainFrame.getStatisticsContainer(), injector, stat.getViewName());
			}
			if(stat.getTypes().contains(StatisticType.QUERY)) {
				addStatistic(queryExecutor, injector, stat.getModelName());
			}
			if(stat.getTypes().contains(StatisticType.UPDATE)) {
				addStatistic(updateExecutor, injector, stat.getModelName());
			}
			if(stat.getTypes().contains(StatisticType.OPTIMIZE)) {
				addStatistic(optimizeExecutor, injector, stat.getModelName());
			}
		}
		
	}
	
	private static void addStatistic(OptimizeExecutor optimizeExecutor,
			Injector injector, String modelName) {
		Key<OptimizeStatistic> injectorKey = Key.get(OptimizeStatistic.class, Names.named(modelName));
		optimizeExecutor.addStatistic(injector.getInstance(injectorKey));
	}

	private static void addStatistic(UpdateExecutor updateExecutor,
			Injector injector, String modelName) {
		Key<UpdateStatistic> injectorKey = Key.get(UpdateStatistic.class, Names.named(modelName));
		updateExecutor.addStatistic(injector.getInstance(injectorKey));
	}

	private static void addStatictic(StatisticsContainer statisticsContainer,
			Injector injector, String viewName) {
		Key<StatisticPanel> injectorKey = Key.get(StatisticPanel.class, Names.named(viewName));
		statisticsContainer.addStatistic(injector.getInstance(injectorKey));
		
	}
	
	private static void addStatistic(QueryExecutor queryExecutor, Injector injector,
			String modelName) {
		Key<QueryStatistic> injectorKey = Key.get(QueryStatistic.class, Names.named(modelName));
		queryExecutor.addStatistic(injector.getInstance(injectorKey));
	}
	
	
}
