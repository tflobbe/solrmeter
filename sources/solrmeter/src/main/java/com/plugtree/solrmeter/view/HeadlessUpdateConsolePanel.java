package com.plugtree.solrmeter.view;

import com.google.inject.Inject;
import com.plugtree.solrmeter.controller.UpdateExecutorController;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.UpdateExecutor;
import com.plugtree.solrmeter.model.statistic.CommitHistoryStatistic;
import com.plugtree.solrmeter.model.statistic.OperationRateStatistic;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HeadlessUpdateConsolePanel extends HeadlessConsolePanel {

    private UpdateExecutor updateExecutor;
    private CommitHistoryStatistic commitHistoryStatistic;
    private OperationRateStatistic operationRateStatistic;
    private UpdateExecutorController controller;
    private final long DEFAULT_INITIAL_DELAY_MS = 1000 * 10;
    private final long DEFAULT_NUM_UPDATES = 100;
    private boolean completed = false;

    @Inject
	public HeadlessUpdateConsolePanel(UpdateExecutor updateExecutor,
			CommitHistoryStatistic commitHistoryStatistic,
			OperationRateStatistic operationRatestatistic,
            UpdateExecutorController controller) {
		this.updateExecutor = updateExecutor;
		this.commitHistoryStatistic = commitHistoryStatistic;
		this.operationRateStatistic = operationRatestatistic;
        this.controller = controller;
        this.controller.addObserver(this);
    }

    private void scheduleUpdateOperations() {
        String updateDelay = SolrMeterConfiguration.getProperty("headless.updateInitialDelay", String.valueOf(DEFAULT_INITIAL_DELAY_MS));
        new ScheduledThreadPoolExecutor(1).schedule(new Runnable() {
            @Override
            public void run() {
                controller.onStart();
            }
        }, Long.valueOf(updateDelay), TimeUnit.MILLISECONDS);
    }

    private long getNumUpdates() {
        return Integer.valueOf(SolrMeterConfiguration.getProperty("headless.numUpdates", String.valueOf(DEFAULT_NUM_UPDATES)));
    }

    public void scheduleOperations() {
        if (SolrMeterConfiguration.getProperty("headless.performUpdateOperations", "").equalsIgnoreCase("true")) {
            if (getNumUpdates() > 0) {
                scheduleUpdateOperations();
            } else {
                completed = true;
            }
        } else {
            completed = true;
        }
    }

    public void stopped() {
        completed = true;
    }

    public void started() {}

    public void refreshView() {
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("added documents:\t" + String.valueOf(commitHistoryStatistic.getTotalAddedDocuments()));
		lines.add("errors on update:\t" + String.valueOf(commitHistoryStatistic.getUpdateErrorCount()));
        lines.add("intended updates per minute:\t" + SolrMeterConfiguration.getProperty("solr.load.updatesperminute"));
		lines.add("update rate:\t" + String.valueOf(operationRateStatistic.getUpdateRate()));
        HeadlessUtils.outputData("updateConsolePanel.title", HeadlessConsoleFrame.getOutputDirectory(), lines);

        if(commitHistoryStatistic.getTotalAddedDocuments() >= getNumUpdates()) {
            controller.onStop();
        }
    }

    public boolean operationsComplete() {
        return completed;
    }

}
