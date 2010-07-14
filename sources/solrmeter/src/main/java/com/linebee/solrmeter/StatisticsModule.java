package com.linebee.solrmeter;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.linebee.solrmeter.model.statistic.CommitHistoryStatistic;
import com.linebee.solrmeter.model.statistic.ErrorLogStatistic;
import com.linebee.solrmeter.model.statistic.FullQueryStatistic;
import com.linebee.solrmeter.model.statistic.HistogramQueryStatistic;
import com.linebee.solrmeter.model.statistic.OperationTimeHistory;
import com.linebee.solrmeter.model.statistic.QueryLogStatistic;
import com.linebee.solrmeter.model.statistic.QueryTimeHistoryStatistic;
import com.linebee.solrmeter.model.statistic.SimpleQueryStatistic;
import com.linebee.solrmeter.model.statistic.TimeRangeStatistic;
import com.linebee.solrmeter.view.Refreshable;
import com.linebee.solrmeter.view.statistic.ErrorLogPanel;
import com.linebee.solrmeter.view.statistic.FullQueryStatisticPanel;
import com.linebee.solrmeter.view.statistic.HistogramChartPanel;
import com.linebee.solrmeter.view.statistic.OperationTimeLineChartPanel;
import com.linebee.solrmeter.view.statistic.PieChartPanel;
import com.linebee.solrmeter.view.statistic.QueryTimeHistoryPanel;

public class StatisticsModule extends AbstractModule {

	@Override
	protected void configure() {
//		bind(QueryStatistic.class).annotatedWith(Names.named("histogramQueryStatistic")).to(HistogramQueryStatistic.class);
		bind(HistogramQueryStatistic.class);
		bind(HistogramChartPanel.class);
		bind(TimeRangeStatistic.class);
		bind(PieChartPanel.class);
		bind(QueryTimeHistoryStatistic.class);
		bind(SimpleQueryStatistic.class);
		bind(FullQueryStatistic.class);
		bind(QueryLogStatistic.class);
		bind(QueryTimeHistoryPanel.class);
		bind(ErrorLogPanel.class);
		bind(OperationTimeLineChartPanel.class);
		bind(FullQueryStatisticPanel.class);
		bind(CommitHistoryStatistic.class);
		bind(OperationTimeHistory.class);
		bind(ErrorLogStatistic.class);
		bind(Refreshable.class).annotatedWith(Names.named("errorLogPanel")).to(ErrorLogPanel.class);
		
	}
}
