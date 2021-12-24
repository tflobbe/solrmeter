/**
 * Copyright Plugtree LLC
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
package com.plugtree.solrmeter.model.executor;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;

import com.plugtree.solrmeter.model.FileUtils;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.SolrServerRegistry;
import com.plugtree.solrmeter.model.operation.RandomOperationExecutorThread;

/**
 * Base class for operation executors that run multiple threads.
 * @see com.plugtree.solrmeter.model.operation.RandomOperationExecutorThread
 * @author tflobbe
 *
 */
public abstract class AbstractRandomExecutor {
	
	protected final Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * List of threads executing strings
	 */
	protected List<RandomOperationExecutorThread> threads;
	
	/**
	 * The number of operations that are executed every one second.
	 */
	protected int operationsPerSecond;
	
	/**
	 * Indicates whether the Executor is running or not
	 */
	private boolean running;
	
	/**
	 * Indicates whether the Executor is prepared to run or not.
	 */
	private boolean prepared = false;
	
	/**
	 * Returns the list of files from the text file at "filePath"
	 * @param filePath
	 * @return
	 */
	protected List<String> loadStringsFromFile(String filePath) {
		return FileUtils.loadStringsFromResource(filePath);
	}
	
	/**
	 * Returns a random object of the passed list
	 * @param list
	 * @return
	 */
	protected Object getNextRandomObject(List<?> list) {
		int index = (int) (Math.random() * list.size());
		return list.get(index);
	}

	/**
	 * Prepare this executor to run a test
	 */
	public void prepare() {
	    if (threads == null) {
	        threads = new LinkedList<RandomOperationExecutorThread>();
	    }
		running = false;
		synchronized (threads) {
		    for(int i = threads.size(); i < operationsPerSecond; i++) {
	            threads.add(createThread());
	        }
        }
		prepared = true;
	}
	
    public void setOperationsPerSecond(int newOperationsPerSecond) {
        if (newOperationsPerSecond < 1) {
            throw new IllegalArgumentException("Invalid number of operations: " + newOperationsPerSecond);
        }
	    logger.debug("Setting Operations per second to "+ newOperationsPerSecond);
	    synchronized (threads) {
	        if (newOperationsPerSecond == threads.size()) {
	            return;
	        }
	        if (newOperationsPerSecond > threads.size()) {
	            while (threads.size() < newOperationsPerSecond) {
	                RandomOperationExecutorThread newThread = this.createThread();
	                threads.add(newThread);
	                if (running) {
	                    newThread.start();
	                }
	            }
	            this.operationsPerSecond = newOperationsPerSecond;
	            SolrMeterConfiguration.setProperty(getOperationsPerSecondConfigurationKey(), String.valueOf(operationsPerSecond));
	        } else {
	            while (threads.size() > newOperationsPerSecond) {
	                RandomOperationExecutorThread removedThread = threads.remove(threads.size() - 1);
	                removedThread.destroy();
	            }
	            this.operationsPerSecond = newOperationsPerSecond;
	            SolrMeterConfiguration.setProperty(getOperationsPerSecondConfigurationKey(), String.valueOf(operationsPerSecond));
	        }
	    }
    }
	
	protected abstract String getOperationsPerSecondConfigurationKey();

	protected abstract RandomOperationExecutorThread createThread();

	/**
	 * Starts the executor. All Threads are started.
	 */
	public void start() {
		if(running == true) {
			return;
		}
		if(!prepared) {
			prepare();
		}
		running = true;
		synchronized (threads) {
    		for(Thread thread:threads) {
    			thread.start();
    		}
		}
	}
	
	/**
	 * Stops the executor. All threads are stopped. Statistics are printed.
	 */
	public void stop() {
		if(running == false) {
			return;
		}
		running = false;
		synchronized (threads) {
    		for(RandomOperationExecutorThread thread:threads) {
    			thread.destroy();
    		}
		}
		stopStatistics();
		threads.clear();;
		prepared = false;
	}

	protected abstract void stopStatistics();

	/**
	 * 
	 * @param url
	 * @return Return the Solr Server instance for the url. There is only one 
	 * Solr Server for every difFerent url
	 */
	public SolrClient getSolrServer(String url) {
		return SolrServerRegistry.getSolrServer(url);
	}

	public boolean isRunning() {
		return running;
	}

}
