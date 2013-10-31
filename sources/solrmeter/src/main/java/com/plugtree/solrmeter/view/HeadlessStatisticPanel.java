package com.plugtree.solrmeter.view;

public abstract class HeadlessStatisticPanel implements Refreshable {

    public abstract String getStatisticName();
    public abstract void refreshView();

}
