package com.plugtree.solrmeter.view.statistic;

import com.google.inject.Inject;
import com.plugtree.solrmeter.model.statistic.CacheHistoryStatistic;
import com.plugtree.solrmeter.view.HeadlessStatisticPanel;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.stressTestScope.StressTestScope;

@StressTestScope
public class HeadlessCacheHistoryPanel extends HeadlessStatisticPanel {

    private static final String PREFIX = "statistic.cacheStatistic.";
    private CacheHistoryStatistic statistic;

    @Inject
    public HeadlessCacheHistoryPanel(CacheHistoryStatistic statistic) {
        super();
        this.statistic = statistic;
    }

    @Override
    public String getStatisticName() {
        return I18n.get(PREFIX + "title");
    }

    @Override
    public void refreshView() {

    }
}
