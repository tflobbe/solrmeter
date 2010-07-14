package com.linebee.solrmeter;

import java.awt.MenuBar;

import javax.swing.JFrame;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.linebee.solrmeter.controller.QueryExecutorController;
import com.linebee.solrmeter.controller.SolrMeterMenuController;
import com.linebee.solrmeter.model.statistic.SimpleOptimizeStatistic;
import com.linebee.solrmeter.view.ConsoleFrame;
import com.linebee.solrmeter.view.ConsolePanel;
import com.linebee.solrmeter.view.OptimizeConsolePanel;
import com.linebee.solrmeter.view.QueryConsolePanel;
import com.linebee.solrmeter.view.Refreshable;
import com.linebee.solrmeter.view.SolrMeterMenuBar;
import com.linebee.solrmeter.view.StatisticsContainer;
import com.linebee.solrmeter.view.UpdateConsolePanel;

public class StandalonePresentationModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ConsoleFrame.class);
		bind(JFrame.class).annotatedWith(Names.named("mainFrame")).to(ConsoleFrame.class);
		bind(QueryConsolePanel.class);
		bind(UpdateConsolePanel.class);
		bind(OptimizeConsolePanel.class);
		bind(StatisticsContainer.class);
		bind(MenuBar.class).to(SolrMeterMenuBar.class);
		bind(QueryExecutorController.class);
		bind(ConsolePanel.class).annotatedWith(Names.named("queryConsolePanel")).to(QueryConsolePanel.class);
		bind(ConsolePanel.class).annotatedWith(Names.named("updateConsolePanel")).to(UpdateConsolePanel.class);
		bind(Refreshable.class).annotatedWith(Names.named("statisticsContainer")).to(StatisticsContainer.class);
		bind(Refreshable.class).annotatedWith(Names.named("optimizeConsolePanel")).to(OptimizeConsolePanel.class);
		bind(SolrMeterMenuController.class);
		bind(SimpleOptimizeStatistic.class);
	}

}
