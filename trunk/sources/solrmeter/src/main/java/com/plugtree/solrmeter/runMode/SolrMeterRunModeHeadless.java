package com.plugtree.solrmeter.runMode;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.plugtree.solrmeter.controller.StatisticType;
import com.plugtree.solrmeter.model.*;
import com.plugtree.solrmeter.controller.StatisticsRepository;
import com.plugtree.solrmeter.controller.StatisticDescriptor;
import com.plugtree.solrmeter.view.*;
import com.plugtree.stressTestScope.StressTestRegistry;
import org.apache.log4j.Logger;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SolrMeterRunModeHeadless extends AbstractSolrMeterRunMode {

    private HeadlessConsoleFrame frame;
    private final int INITIAL_START_DELAY = 2000;

    public static final String RUN_MODE_NAME = "headless";

    private void addStatistics() {
		QueryExecutor queryExecutor = injector.getInstance(QueryExecutor.class);
		UpdateExecutor updateExecutor = injector.getInstance(UpdateExecutor.class);
		OptimizeExecutor optimizeExecutor = injector.getInstance(OptimizeExecutor.class);
		StatisticsRepository repository = injector.getInstance(StatisticsRepository.class);
        HeadlessStatisticsContainer headlessStatisticsContainer = frame.getStatistics();
		for(StatisticDescriptor stat:repository.getActiveStatistics()) {
			Logger.getLogger("boot").info("Adding Statistic " + stat.getName());
            if(stat.isHasView()) {
				addStatistic(headlessStatisticsContainer, injector, stat.getHeadlessViewName());
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

    private void addStatistic(OptimizeExecutor optimizeExecutor,
			Injector injector, String modelName) {
		Key<OptimizeStatistic> injectorKey = Key.get(OptimizeStatistic.class, Names.named(modelName));
		optimizeExecutor.addStatistic(injector.getInstance(injectorKey));
	}

	private void addStatistic(UpdateExecutor updateExecutor,
			Injector injector, String modelName) {
		Key<UpdateStatistic> injectorKey = Key.get(UpdateStatistic.class, Names.named(modelName));
		updateExecutor.addStatistic(injector.getInstance(injectorKey));
	}

	private void addStatistic(QueryExecutor queryExecutor, Injector injector,
			String modelName) {
		Key<QueryStatistic> injectorKey = Key.get(QueryStatistic.class, Names.named(modelName));
		queryExecutor.addStatistic(injector.getInstance(injectorKey));
	}

    private void addStatistic(HeadlessStatisticsContainer headlessStatisticsContainer,
			Injector injector, String headlessViewName) {
		Key<HeadlessStatisticPanel> injectorKey = Key.get(HeadlessStatisticPanel.class, Names.named(headlessViewName));
		headlessStatisticsContainer.addStatistic(injector.getInstance(injectorKey));

	}

    private void scheduleOperations() {
        wait(INITIAL_START_DELAY);
        for (HeadlessConsolePanel panel:frame.getConsolePanels()){
            panel.scheduleOperations();
        }

        startMonitoring();
    }

    private void startMonitoring() {
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                boolean allDone = true;
                for (HeadlessConsolePanel panel : frame.getConsolePanels()) {
                    if (!panel.operationsComplete()) {
                        allDone = false;
                    }
                }
                if (allDone) {
                    System.exit(0);
                }
            }
        }, 10, 2, TimeUnit.SECONDS);
    }

    private void wait(int delay) {
        try {
            Thread.sleep(delay);
        }
        catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void main(Injector injector) {
        super.main(injector);
        StressTestRegistry.start();
        frame = injector.getInstance(HeadlessConsoleFrame.class);
        addStatistics();
        scheduleOperations();
    }

    public void restartApplication() {}

    public ConsoleFrame getMainFrame() {
        return null;
    }

}
