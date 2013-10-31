package com.plugtree.solrmeter.view;

import com.google.inject.Inject;
import com.plugtree.solrmeter.controller.QueryExecutorController;
import com.plugtree.solrmeter.model.QueryExecutor;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.statistic.OperationRateStatistic;
import com.plugtree.solrmeter.model.statistic.SimpleQueryStatistic;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HeadlessQueryConsolePanel extends HeadlessConsolePanel {

    private SimpleQueryStatistic simpleQueryStatistic;
    private OperationRateStatistic operationRateStatistic;
    private QueryExecutor queryExecutor;
    private QueryExecutorController controller;
    private final long DEFAULT_INITIAL_DELAY_MS = 1000 * 2;
    private final long DEFAULT_NUM_QUERIES = 100;
    private boolean completed = false;

 	@Inject
	public HeadlessQueryConsolePanel(SimpleQueryStatistic simpleQueryStatistic,
			OperationRateStatistic operationRateStatistic,
			QueryExecutor queryExecutor,
            QueryExecutorController controller) {
		this.simpleQueryStatistic = simpleQueryStatistic;
		this.operationRateStatistic = operationRateStatistic;
		this.queryExecutor = queryExecutor;
        this.controller = controller;
	}

    private void scheduleQueryOperations() {
        String queryDelay = SolrMeterConfiguration.getProperty("headless.queryInitialDelay", String.valueOf(DEFAULT_INITIAL_DELAY_MS));
        new ScheduledThreadPoolExecutor(1).schedule(new Runnable() {
            @Override
            public void run() {
                controller.onStart();
            }
        }, Long.valueOf(queryDelay), TimeUnit.MILLISECONDS);
    }

    private long getNumQueries() {
        return Integer.valueOf(SolrMeterConfiguration.getProperty("headless.numQueries", String.valueOf(DEFAULT_NUM_QUERIES)));
    }

    public void refreshView() {
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("queries per minute:\t" + String.valueOf(queryExecutor.getQueriesPerMinute()));
        lines.add("total queries:\t" + String.valueOf(simpleQueryStatistic.getTotalQueries()));
        lines.add("total query time:\t" + String.valueOf(simpleQueryStatistic.getTotalQTime()));
        lines.add("total client time:\t" + String.valueOf(simpleQueryStatistic.getTotalClientTime()));
        lines.add("total errors:\t" + String.valueOf(simpleQueryStatistic.getTotalErrors()));
        if(simpleQueryStatistic.getTotalQueries() != 0) {
			lines.add("average query time:\t" + String.valueOf(simpleQueryStatistic.getAverageQueryTime()));
			lines.add("average client time:\t" + String.valueOf(simpleQueryStatistic.getAverageClientTime()));
		}
        lines.add("intended queries per minute:\t" + SolrMeterConfiguration.getProperty("solr.load.queriesperminute"));
		lines.add("actual query rate:\t" + String.valueOf(operationRateStatistic.getQueryRate()));
        HeadlessUtils.outputData("queryConsolePanel.title", HeadlessConsoleFrame.getOutputDirectory(), lines);

        if (simpleQueryStatistic.getTotalQueries() >= getNumQueries()) {
            controller.onStop();
        }
    }

    public boolean operationsComplete() {
        return completed;
    }

    public void scheduleOperations() {
        if (SolrMeterConfiguration.getProperty("headless.performQueryOperations", "").equalsIgnoreCase("true")) {
            if (getNumQueries() > 0) {
                scheduleQueryOperations();
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
}
