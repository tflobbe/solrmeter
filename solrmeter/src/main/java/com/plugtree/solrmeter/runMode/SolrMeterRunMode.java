package com.plugtree.solrmeter.runMode;

import com.google.inject.Injector;
import com.plugtree.solrmeter.view.ConsoleFrame;

public interface SolrMeterRunMode {

    public void main(Injector injector);
    public void restartApplication();
    public ConsoleFrame getMainFrame();
}
