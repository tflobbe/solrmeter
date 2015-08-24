package com.plugtree.solrmeter.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SolrMeterThreadFactory implements ThreadFactory {

    private final AtomicInteger threadNumber = new AtomicInteger(0);
    
    private final String prefix;
    
    public SolrMeterThreadFactory(String namePrefix) {
        prefix = namePrefix + "-";
    }
    
    @Override
    public Thread newThread(Runnable r) {
        return new Thread(prefix + threadNumber.incrementAndGet());
    }

}
