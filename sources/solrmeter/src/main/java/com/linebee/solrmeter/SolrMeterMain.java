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

import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.linebee.solrmeter.model.OptimizeExecutor;
import com.linebee.solrmeter.model.OptimizeStatistic;
import com.linebee.solrmeter.model.QueryExecutor;
import com.linebee.solrmeter.model.UpdateExecutor;
import com.linebee.solrmeter.model.statistic.CommitHistoryStatistic;
import com.linebee.solrmeter.model.statistic.ErrorLogStatistic;
import com.linebee.solrmeter.model.statistic.FullQueryStatistic;
import com.linebee.solrmeter.model.statistic.HistogramQueryStatistic;
import com.linebee.solrmeter.model.statistic.OperationTimeHistory;
import com.linebee.solrmeter.model.statistic.QueryLogStatistic;
import com.linebee.solrmeter.model.statistic.QueryTimeHistoryStatistic;
import com.linebee.solrmeter.model.statistic.SimpleOptimizeStatistic;
import com.linebee.solrmeter.model.statistic.SimpleQueryStatistic;
import com.linebee.solrmeter.model.statistic.TimeRangeStatistic;
import com.linebee.solrmeter.view.ConsoleFrame;
import com.linebee.solrmeter.view.I18n;
import com.linebee.solrmeter.view.Model;


public class SolrMeterMain {
	
	public static ConsoleFrame mainFrame;

	public static void main(String[] args) throws Exception {
		loadLookAndFeel();
		initModel();
		initView();
	}
	
	public static void restartApplication() {
		Model.getInstance().prepareAll();
		I18n.onConfigurationChange();
		mainFrame.onConfigurationChanged();
//		initView();
	}

	private static void initView() {
		mainFrame = new ConsoleFrame();
		mainFrame.setVisible(true);
		mainFrame.setTitle(I18n.get("mainFrame.title"));
	}

	private static void loadLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			Logger.getLogger(SolrMeterMain.class).error("Error loading look and feel. Will Continue with default.", e);
		}
	}

	private static void initModel() {
		initQueryModel();
		initUpdateModel();
		initOptimizeModel();
		Model.getInstance().prepareAll();
		addErrorLogStatistic();
		addOperationHistoryStatistic();
	}

	private static void addOperationHistoryStatistic() {
		OperationTimeHistory statistic = new OperationTimeHistory();
		statistic.prepare();
		Model.getInstance().getCurrentQueryExecutor().addStatistic(statistic);
		Model.getInstance().getCurrentUpdateExecutor().addStatistic(statistic);
		Model.getInstance().getCurrentOptimizeExecutor().addStatistic(statistic);
		Model.getInstance().putQueryStatistic("operationTimeHistory", statistic);
		
	}

	private static void addErrorLogStatistic() {
		ErrorLogStatistic statistic = new ErrorLogStatistic();
		statistic.prepare();
		Model.getInstance().getCurrentQueryExecutor().addStatistic(statistic);
		Model.getInstance().getCurrentUpdateExecutor().addStatistic(statistic);
		Model.getInstance().getCurrentOptimizeExecutor().addStatistic(statistic);
		Model.getInstance().putQueryStatistic("errorLogStatistic", statistic);
	}

	private static void initOptimizeModel() {
		OptimizeExecutor executor = new OptimizeExecutor();
		OptimizeStatistic observer = new SimpleOptimizeStatistic();
		executor.addStatistic(observer);
		Model.getInstance().putOptimizeStatistic("optimizeStatistic", observer);
		Model.getInstance().setOptimizeExecutor(executor);
	}

	private static void initUpdateModel() {
		UpdateExecutor executor = new UpdateExecutor();
		CommitHistoryStatistic commitHistoryStatistic = new CommitHistoryStatistic();
		executor.addStatistic(commitHistoryStatistic);
		Model.getInstance().putUpdateStatistic("commitHistoryStatistic", commitHistoryStatistic);
		Model.getInstance().setUpdateExecutor(executor);
		
	}

	private static void initQueryModel() {
		QueryExecutor queryExecutor = new QueryExecutor();
		
		HistogramQueryStatistic histogramStatistic = new HistogramQueryStatistic();
		queryExecutor.addStatistic(histogramStatistic);
		Model.getInstance().putQueryStatistic("histogram", histogramStatistic);
		
		QueryTimeHistoryStatistic queryTimeHistoryStatistic = new QueryTimeHistoryStatistic();
		queryExecutor.addStatistic(queryTimeHistoryStatistic);
		Model.getInstance().putQueryStatistic("queryTimeHistory", queryTimeHistoryStatistic);
		
		TimeRangeStatistic timeRangeStatistic = new TimeRangeStatistic();
		queryExecutor.addStatistic(timeRangeStatistic);
		Model.getInstance().putQueryStatistic("timeRangeStatistic", timeRangeStatistic);
		
		SimpleQueryStatistic simpleQueryStatistic = new SimpleQueryStatistic();
		queryExecutor.addStatistic(simpleQueryStatistic);
		Model.getInstance().putQueryStatistic("simpleQueryStatistic", simpleQueryStatistic);
		
		FullQueryStatistic fullQueryStatistic = new FullQueryStatistic();
		queryExecutor.addStatistic(fullQueryStatistic);
		Model.getInstance().putQueryStatistic("fullQueryStatistic", fullQueryStatistic);
		
		QueryLogStatistic queryLogStatistic = new QueryLogStatistic();
		queryExecutor.addStatistic(queryLogStatistic);
		Model.getInstance().putQueryStatistic("queryLogStatistic", queryLogStatistic);
		
		Model.getInstance().setQueryExecutor(queryExecutor);
	}
	
}
