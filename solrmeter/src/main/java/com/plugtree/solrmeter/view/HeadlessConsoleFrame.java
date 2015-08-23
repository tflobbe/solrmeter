package com.plugtree.solrmeter.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class HeadlessConsoleFrame {

    private HeadlessQueryConsolePanel queryPanel;
    private HeadlessUpdateConsolePanel updatePanel;
    private HeadlessCommitConsolePanel commitPanel;
    private HeadlessOptimizeConsolePanel optimizePanel;
    private HeadlessStatisticsContainer statistics;

    @Inject
    public HeadlessConsoleFrame(HeadlessQueryConsolePanel queryPanel,
                                HeadlessUpdateConsolePanel updatePanel,
                                HeadlessCommitConsolePanel commitPanel,
                                HeadlessOptimizeConsolePanel optimizePanel,
                                HeadlessStatisticsContainer statistics) {
        this.queryPanel = queryPanel;
        this.updatePanel = updatePanel;
        this.commitPanel = commitPanel;
        this.optimizePanel = optimizePanel;
        this.statistics = statistics;
        init();
    }

    private void init() {
        initWorkspace();
    }

    private void initWorkspace() {
        try {
            File outDir = new File(getOutputDirectory());
            FileUtils.forceMkdir(outDir);
            FileUtils.cleanDirectory(outDir);
            FileUtils.forceMkdir(new File(getStatisticsOutputDirectory()));
            FileUtils.forceMkdir(new File(FilenameUtils.concat(getOutputDirectory(), "directives")));
        }
        catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public HeadlessStatisticsContainer getStatistics() {
        return statistics;
    }

    public HeadlessQueryConsolePanel getQueryPanel() {
        return queryPanel;
    }

    public HeadlessUpdateConsolePanel getUpdatePanel() {
        return updatePanel;
    }

    public HeadlessCommitConsolePanel getCommitPanel() {
        return commitPanel;
    }

    public HeadlessOptimizeConsolePanel getOptimizePanel() {
        return optimizePanel;
    }

    public List<HeadlessConsolePanel> getConsolePanels() {
        return new ArrayList<HeadlessConsolePanel>(){{
            add(getQueryPanel());
            add(getUpdatePanel());
            add(getCommitPanel());
            add(getOptimizePanel());
        }};
    }

    public static String getOutputDirectory() {
        return SolrMeterConfiguration.getProperty("headless.outputDirectory");
    }

    public static String getStatisticsOutputDirectory() {
        return FilenameUtils.concat(getOutputDirectory(), "statistics");
    }

}
