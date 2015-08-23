package com.plugtree.solrmeter.view.statistic;

import com.google.inject.Inject;
import com.plugtree.solrmeter.model.statistic.OperationTimeHistory;
import com.plugtree.solrmeter.view.HeadlessConsoleFrame;
import com.plugtree.solrmeter.view.HeadlessStatisticPanel;
import com.plugtree.solrmeter.view.HeadlessUtils;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.stressTestScope.StressTestScope;
import org.apache.commons.lang.StringUtils;

import java.util.*;

@StressTestScope
public class HeadlessOperationTimeLineChartPanel extends HeadlessStatisticPanel {

    private final String PREFIX = "statistic.operationTimeLineChartPanel.";
    private OperationTimeHistory statistic;

    @Inject
    public HeadlessOperationTimeLineChartPanel(OperationTimeHistory statistic) {
        super();
        this.statistic = statistic;
    }

    @Override
    public String getStatisticName() {
        return I18n.get(PREFIX + "title");
    }

    @Override
    public void refreshView() {
        Map<Long, Long> commitTime = statistic.getCommitTime();
        Map<Long, Long> queriesTime = statistic.getQueriesTime();
        Map<Long, Long> updatesTime = statistic.getUpdatesTime();
        Map<Long, Long> optimizeTime = statistic.getOptimizeTime();

        Set<Long> executionInstants = new TreeSet<Long>();
        executionInstants.addAll(commitTime.keySet());
        executionInstants.addAll(optimizeTime.keySet());
        executionInstants.addAll(queriesTime.keySet());
        executionInstants.addAll(updatesTime.keySet());

        ArrayList<String> lines = new ArrayList<String>();
        lines.add("execution instant\tcommits\tqueries\tupdates\toptimizes");
        for(long instant:executionInstants) {
            ArrayList<String> fields = new ArrayList<String>();
            fields.add(String.valueOf(instant/1000.0));
            fields.add(getFieldValue(instant, commitTime));
            fields.add(getFieldValue(instant, queriesTime));
            fields.add(getFieldValue(instant, updatesTime));
            fields.add(getFieldValue(instant, optimizeTime));
            lines.add(StringUtils.join(fields, "\t"));
        }
        HeadlessUtils.outputData(PREFIX + "title", HeadlessConsoleFrame.getStatisticsOutputDirectory(), lines);
    }

    private String getFieldValue(long executionInstant, Map<Long, Long> series) {
        String value = new String();
        if(series.containsKey(executionInstant)) {
            return String.valueOf(series.get(executionInstant));
        }
        else {
            return "-";
        }
    }
}
