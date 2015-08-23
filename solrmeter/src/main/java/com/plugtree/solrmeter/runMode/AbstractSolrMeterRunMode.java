package com.plugtree.solrmeter.runMode;

import com.google.inject.Injector;
import com.plugtree.solrmeter.view.ConsoleFrame;

public abstract class AbstractSolrMeterRunMode implements SolrMeterRunMode {

    protected Injector injector;
    protected ConsoleFrame mainFrame;

    public void main(Injector injector){
        this.injector = injector;
    }
    public abstract void restartApplication();

    public ConsoleFrame getMainFrame(){
        return this.mainFrame;
    }

}
