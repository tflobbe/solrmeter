package com.plugtree.solrmeter.view.statistic;

import com.google.inject.Inject;
import com.plugtree.solrmeter.model.statistic.TimeRange;
import com.plugtree.solrmeter.model.statistic.TimeRangeStatistic;
import com.plugtree.solrmeter.view.HeadlessConsoleFrame;
import com.plugtree.solrmeter.view.HeadlessStatisticPanel;
import com.plugtree.solrmeter.view.HeadlessUtils;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.stressTestScope.StressTestScope;

import java.util.ArrayList;
import java.util.Map;

@StressTestScope
public class HeadlessPieChartPanel extends HeadlessStatisticPanel {

    private final String PREFIX = "statistic.pieChartPanel.";
    private TimeRangeStatistic timeRangeStatistic;

    @Inject
    public HeadlessPieChartPanel(TimeRangeStatistic statistic) {
        super();
        timeRangeStatistic = statistic;
    }

    @Override
    public String getStatisticName() {
        return I18n.get(PREFIX + "title");
    }

    @Override
    public void refreshView() {
        ArrayList<String> lines = new ArrayList<String>();
		Map<TimeRange, Integer> percentages = timeRangeStatistic.getActualPercentage();
		for(TimeRange range: percentages.keySet()) {
            lines.add(range.toString() + ":\t" + percentages.get(range));
		}
        HeadlessUtils.outputData(PREFIX + "title", HeadlessConsoleFrame.getStatisticsOutputDirectory(), lines);
    }
}
