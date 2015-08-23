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
package com.plugtree.solrmeter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.plugtree.solrmeter.controller.StatisticsRepository;
import com.plugtree.solrmeter.runMode.SolrMeterRunMode;
import com.plugtree.solrmeter.model.*;
import com.plugtree.solrmeter.view.*;
import com.plugtree.stressTestScope.StressTestScopeModule;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author tflobbe
 *
 */
public class SolrMeterMain {
	
	public static ConsoleFrame mainFrame;
	
	private static Injector injector;
    private static SolrMeterRunMode runMode;

	public static void main(String[] args) throws Exception {
		addPlugins(new ExpectedParameter(args, "statisticsLocation", "./plugins").getValue());
		createInjector();
        runMode = injector.getInstance(SolrMeterRunMode.class);
        runMode.main(injector);
        mainFrame = runMode.getMainFrame();
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

	private static void createInjector() {
        List<Module> modules = new ArrayList<Module>();
        modules.add(createModule("guice.statisticsModule"));
        modules.add(createModule("guice.modelModule"));
        if (SolrMeterConfiguration.isHeadless()) {
            modules.add(createModule("guice.headlessModule"));
        }
        else {
            modules.add(createModule("guice.standalonePresentationModule"));
        }
        modules.add(createModule("guice.solrMeterRunModeModule"));
        modules.add(new StressTestScopeModule());
		injector = Guice.createInjector(modules);
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
        runMode.restartApplication();
	}

    public static SolrMeterRunMode getRunMode() {
        return runMode;
    }

}
