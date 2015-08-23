package com.plugtree.solrmeter.view.statistic;

import com.google.inject.Inject;
import com.plugtree.solrmeter.model.exception.OperationException;
import com.plugtree.solrmeter.model.statistic.ErrorLogStatistic;
import com.plugtree.solrmeter.view.HeadlessConsoleFrame;
import com.plugtree.solrmeter.view.HeadlessStatisticPanel;
import com.plugtree.solrmeter.view.HeadlessUtils;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.stressTestScope.StressTestScope;

import java.util.List;

@StressTestScope
public class HeadlessErrorLogPanel extends HeadlessStatisticPanel {

    private String PREFIX = "statistic.errorLogPanel.";
    private ErrorLogStatistic statistic;

    @Inject
    public HeadlessErrorLogPanel(ErrorLogStatistic statistic) {
        super();
        this.statistic = statistic;
    }

    @Override
    public String getStatisticName() {
        return I18n.get(PREFIX + "title");
    }

    @Override
    public void refreshView() {
        List<OperationException> errors = statistic.getLastErrors(true, true, true, true);
        HeadlessUtils.outputData(PREFIX + "title", HeadlessConsoleFrame.getStatisticsOutputDirectory(), errors);
    }

}
