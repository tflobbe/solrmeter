package com.plugtree.solrmeter.view;

import com.google.inject.Inject;
import com.plugtree.solrmeter.controller.UpdateExecutorController;
import com.plugtree.solrmeter.model.UpdateExecutor;
import com.plugtree.solrmeter.model.statistic.CommitHistoryStatistic;
import com.plugtree.solrmeter.model.statistic.OperationRateStatistic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HeadlessCommitConsolePanel extends HeadlessConsolePanel {

    private UpdateExecutor updateExecutor;
    private CommitHistoryStatistic commitHistoryStatistic;
    private UpdateExecutorController controller;
    private OperationRateStatistic operationRateStatistic;

    @Inject
	public HeadlessCommitConsolePanel(UpdateExecutor updateExecutor,
			CommitHistoryStatistic commitHistoryStatistic,
			OperationRateStatistic operationRateStatistic,
            UpdateExecutorController controller) {
		this.updateExecutor = updateExecutor;
		this.commitHistoryStatistic = commitHistoryStatistic;
        this.operationRateStatistic = operationRateStatistic;
        this.controller = controller;
        controller.addObserver(this);
		init();
	}

    private void init() {}

    public void stopped() {}

    public void started () {}

    public void refreshView() {
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("not committed documents:\t" + String.valueOf(updateExecutor.getNotCommitedDocuments()));
		if(commitHistoryStatistic.getLastCommitDate() != null) {
			lines.add("last commit:\t" + SimpleDateFormat.getInstance().format(commitHistoryStatistic.getLastCommitDate()));
		}
        lines.add("total commits:\t" + String.valueOf(commitHistoryStatistic.getTotalCommits()));
		lines.add("errors on commit:\t" + String.valueOf(commitHistoryStatistic.getCommitErrorCount()));
        HeadlessUtils.outputData("commitConsolePanel.title", HeadlessConsoleFrame.getOutputDirectory(), lines);
    }

    public void scheduleOperations() {}

    public boolean operationsComplete() {
        return true;
    }

}
