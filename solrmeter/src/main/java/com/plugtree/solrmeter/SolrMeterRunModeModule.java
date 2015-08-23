package com.plugtree.solrmeter;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.plugtree.solrmeter.runMode.SolrMeterRunMode;
import com.plugtree.solrmeter.runMode.SolrMeterRunModeGUI;
import com.plugtree.solrmeter.runMode.SolrMeterRunModeHeadless;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;

import java.util.HashMap;
import java.util.Map;

public class SolrMeterRunModeModule extends AbstractModule {

    @Override
    public void configure(){
        configureRunModes();
    }

    private void configureRunModes() {
        Map<String, Class<? extends SolrMeterRunMode>> map = getRunModesMap();
        for(String runModeName:map.keySet()) {
            bind(SolrMeterRunMode.class).annotatedWith(Names.named(runModeName)).to(map.get(runModeName));
        }
    }

    private Map<String, Class<? extends SolrMeterRunMode>> getRunModesMap() {
        Map<String, Class<? extends SolrMeterRunMode>> map = new HashMap<String, Class<? extends SolrMeterRunMode>>();
        map.put(SolrMeterRunModeGUI.RUN_MODE_NAME, SolrMeterRunModeGUI.class);
        map.put(SolrMeterRunModeHeadless.RUN_MODE_NAME, SolrMeterRunModeHeadless.class);
        return map;
    }

    @Provides
    public SolrMeterRunMode getRunMode(Injector injector) {
        String name = SolrMeterRunModeGUI.RUN_MODE_NAME;
        if(SolrMeterConfiguration.isHeadless()) {
            name = SolrMeterRunModeHeadless.RUN_MODE_NAME;
        }
        final Key<SolrMeterRunMode> key = Key.get(SolrMeterRunMode.class, Names.named(name));
        return injector.getInstance(key);
    }
}
