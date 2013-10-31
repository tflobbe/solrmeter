package com.plugtree.solrmeter;


import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.plugtree.solrmeter.controller.QueryExecutorController;
import com.plugtree.solrmeter.view.*;

public class HeadlessModule extends AbstractModule {

    @Override
    public void configure() {
        bind(HeadlessConsoleFrame.class);
        bind(HeadlessQueryConsolePanel.class);
        bind(HeadlessUpdateConsolePanel.class);
        bind(HeadlessCommitConsolePanel.class);
        bind(HeadlessOptimizeConsolePanel.class);
        bind(HeadlessStatisticsContainer.class);
        bind(QueryExecutorController.class);
        bind(ConsolePanel.class).annotatedWith(Names.named("queryConsolePanel")).to(HeadlessQueryConsolePanel.class);
		bind(Refreshable.class).annotatedWith(Names.named("statisticsContainer")).to(HeadlessStatisticsContainer.class);
		bind(Refreshable.class).annotatedWith(Names.named("optimizeConsolePanel")).to(HeadlessOptimizeConsolePanel.class);
    }
}
