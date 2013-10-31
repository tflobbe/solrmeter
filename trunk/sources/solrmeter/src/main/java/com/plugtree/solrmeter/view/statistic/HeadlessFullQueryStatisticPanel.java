package com.plugtree.solrmeter.view.statistic;

import com.google.inject.Inject;
import com.plugtree.solrmeter.model.statistic.FullQueryStatistic;
import com.plugtree.solrmeter.model.statistic.QueryLogStatistic;
import com.plugtree.solrmeter.view.HeadlessConsoleFrame;
import com.plugtree.solrmeter.view.HeadlessStatisticPanel;
import com.plugtree.solrmeter.view.HeadlessUtils;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.stressTestScope.StressTestScope;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@StressTestScope
public class HeadlessFullQueryStatisticPanel extends HeadlessStatisticPanel {

    private final String PREFIX = "statistic.fullQueryStatistic.";
    private static final int DOUBLE_SCALE = 2;
	private FullQueryStatistic fullQueryStatistic;
	private QueryLogStatistic queryLogStatistic;

    @Inject
    public HeadlessFullQueryStatisticPanel(FullQueryStatistic fullQueryStatistic, QueryLogStatistic queryLogStatistic) {
        super();
        this.fullQueryStatistic = fullQueryStatistic;
        this.queryLogStatistic = queryLogStatistic;
    }

    private String getString(Double number) {
		return new BigDecimal(number).setScale(DOUBLE_SCALE, BigDecimal.ROUND_HALF_DOWN).toString();
	}

    private String prepareData(String str) {
        return str.replace(",", "\t")
                  .replace("false", "ok")
                  .replace("true", "error");
    }

    @Override
	public String getStatisticName() {
		return I18n.get(PREFIX + "title");
	}

    @Override
    public void refreshView() {
        Logger.getLogger(this.getClass()).debug("refreshing Full Query Statistics");

        ArrayList<String> lines = new ArrayList<String>();
        lines.add("median:\t" + getString(fullQueryStatistic.getMedian()));
        lines.add("mode:\t" + fullQueryStatistic.getMode().toString());
        lines.add("variance:\t" + getString(fullQueryStatistic.getVariance()));
        lines.add("standard deviation:\t" + getString(fullQueryStatistic.getStandardDeviation()));
        lines.add("total average:\t" + fullQueryStatistic.getTotaAverage().toString());
        lines.add("last minute average:\t" + fullQueryStatistic.getLastMinuteAverage().toString());
        lines.add("last ten minute average:\t" + fullQueryStatistic.getLastTenMinutesAverage().toString());
		if(fullQueryStatistic.getLastErrorTime() != null) {
            lines.add("last error time:\t" + SimpleDateFormat.getInstance().format(fullQueryStatistic.getLastErrorTime()));
		} else {
            lines.add("last error time:\t-");
		}
        lines.add("status\tquery\tfilter query\tfacet query\tquery time\tresult count");
        List<QueryLogStatistic.QueryLogValue> queries = queryLogStatistic.getLastQueries();
        for(QueryLogStatistic.QueryLogValue value:queries) {
            lines.add(prepareData(value.getCSV()));
        }
        HeadlessUtils.outputData(PREFIX + "title", HeadlessConsoleFrame.getStatisticsOutputDirectory(), lines);
    }

}
