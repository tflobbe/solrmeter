package com.plugtree.solrmeter.view;

import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Singleton
public class HeadlessStatisticsContainer implements Refreshable {

    private final long INITIAL_DELAY_MS = 1000 * 2;
    private final long REFRESH_INTERVAL_MS = 1000 * 2;

    private Map<String, HeadlessStatisticPanel> statistics;

    public HeadlessStatisticsContainer() {
        statistics = new HashMap<String, HeadlessStatisticPanel>();
        scheduleRefresher();
    }

    private void scheduleRefresher() {
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                refreshView();
            }
        }, INITIAL_DELAY_MS, REFRESH_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    private Map<String,HeadlessStatisticPanel> getStatistics() {
        return statistics;
    }

    @Override
    public synchronized void refreshView() {
        for(HeadlessStatisticPanel panel:getStatistics().values()) {
            panel.refreshView();
        }
    }

    public void addStatistic(HeadlessStatisticPanel statistic) {
		statistics.put(statistic.getStatisticName(), statistic);
	}

}
