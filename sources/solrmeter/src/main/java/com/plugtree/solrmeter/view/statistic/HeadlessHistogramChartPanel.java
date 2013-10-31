package com.plugtree.solrmeter.view.statistic;

import com.google.inject.Inject;
import com.plugtree.solrmeter.model.statistic.HistogramQueryStatistic;
import com.plugtree.solrmeter.view.HeadlessConsoleFrame;
import com.plugtree.solrmeter.view.HeadlessStatisticPanel;
import com.plugtree.solrmeter.view.HeadlessUtils;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.stressTestScope.StressTestScope;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@StressTestScope
public class HeadlessHistogramChartPanel extends HeadlessStatisticPanel {

    private final String PREFIX = "statistic.histogramChartPanel.";
    private HistogramQueryStatistic histogram;

    @Inject
	public HeadlessHistogramChartPanel(HistogramQueryStatistic histogram) {
		super();
		this.histogram = histogram;
	}

    public String getStatisticName() {
        return I18n.get(PREFIX + "title");
    }

    public void refreshView() {
        if(!histogram.getCurrentHisogram().isEmpty()) {
            try {
                File outFile = HeadlessUtils.getOutputFile(PREFIX + "title", HeadlessConsoleFrame.getStatisticsOutputDirectory());
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outFile));
                histogram.printHistogramToStream(outputStream);
                outputStream.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
