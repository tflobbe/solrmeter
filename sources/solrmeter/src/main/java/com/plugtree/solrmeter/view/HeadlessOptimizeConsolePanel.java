package com.plugtree.solrmeter.view;

import com.google.inject.Inject;
import com.plugtree.solrmeter.controller.OptimizeExecutorController;
import com.plugtree.solrmeter.model.OptimizeExecutor;
import com.plugtree.solrmeter.model.statistic.SimpleOptimizeStatistic;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HeadlessOptimizeConsolePanel extends HeadlessConsolePanel {

    private OptimizeExecutor executor;
    private SimpleOptimizeStatistic optimizeStatistic;
    private OptimizeExecutorController controller;

    @Inject
	public HeadlessOptimizeConsolePanel(SimpleOptimizeStatistic optimizeStatistic,
			OptimizeExecutor executor,
            OptimizeExecutorController controller) {
		this.executor = executor;
		this.optimizeStatistic = optimizeStatistic;
        this.controller = controller;
		init();
	}

    private void init() {
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if(shouldOptimize()) {
                    controller.onOptimize();
                }
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    private boolean shouldOptimize() {
        boolean optimize = false;
        File optimizeFile = new File(FilenameUtils.concat(HeadlessConsoleFrame.getOutputDirectory(), "directives/optimize"));
        if(optimizeFile.exists()) {
            optimize = true;
            optimizeFile.delete();
        }
        return optimize;
    }

    public void stopped() {

    }

    public void started() {

    }

    public void refreshView() {
        ArrayList<String> lines = new ArrayList<String>();
		if(executor.isOptimizing()) {
            lines.add("optimizing:\ttrue");
		}
        else {
            lines.add("optimizing:\tfalse");
		}
        lines.add("total optimizations:\t" + String.valueOf(optimizeStatistic.getOptimizationCount()));
        lines.add("last optimization time:\t" + String.valueOf(optimizeStatistic.getLastOptimizationTime()));
        lines.add("total optimization time:\t" + String.valueOf(optimizeStatistic.getTotalOptimizationTime()));
        lines.add("optimization time average:\t" + String.valueOf(optimizeStatistic.getAverageOptimizationTime()));
		if(optimizeStatistic.getLastOptimizationResult() != null) {
            lines.add("last optimization result:\t" + String.valueOf(optimizeStatistic.getLastOptimizationResult()));
		}
        lines.add("optimization error count:\t" + String.valueOf(optimizeStatistic.getOptimizeErrorCount()));
        HeadlessUtils.outputData("optimizeConsolePanel.title", HeadlessConsoleFrame.getOutputDirectory(), lines);
    }

    public void scheduleOperations() {}

    public boolean operationsComplete() {
        return true;
    }

}
