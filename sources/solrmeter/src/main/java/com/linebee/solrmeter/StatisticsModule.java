/**
 * Copyright Linebee LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

/**
 * 
 * @author tflobbe
 *
 */
public class StatisticsModule extends AbstractModule {

	@Override
	protected void configure() {
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
