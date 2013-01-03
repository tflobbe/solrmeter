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
import org.apache.solr.client.solrj.SolrServer;

import com.plugtree.solrmeter.model.OptimizeExecutor;
import com.plugtree.solrmeter.model.OptimizeStatistic;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.SolrServerRegistry;
import com.plugtree.solrmeter.model.exception.OptimizeException;
import com.plugtree.stressTestScope.StressTestScope;
/**
 * Executes an optimize only when the "execute" method is invoked
 * @author tflobbe
 *
 */
@StressTestScope
public class OnDemandOptimizeExecutor implements OptimizeExecutor {
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * The Solr Server were the optimize is going to run.
	 */
	protected SolrServer server = null;
	
	/**
	 * Indicates whether the index is being optimized or not at this time
	 */
	private boolean isOptimizing = false;
	
	/**
	 * List of Statistics observing the operation
	 */
	protected List<OptimizeStatistic> optimizeObservers;
	
	public OnDemandOptimizeExecutor() {
		this(SolrServerRegistry.getSolrServer(SolrMeterConfiguration.getProperty(SolrMeterConfiguration.SOLR_ADD_URL)));
	}
	
	public OnDemandOptimizeExecutor(SolrServer server) {
		super();
		optimizeObservers = new LinkedList<OptimizeStatistic>();
		this.server = server;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public synchronized void execute() {
		if(isOptimizing) {
			logger.warn("Trying to optimize while already optimizing");
			return;
		}
		Thread optimizeThread = new Thread() {
			@Override
			public void run() {
				try {
					isOptimizing = true;
					long init = System.nanoTime();
					notifyOptimizeStatred(init);
					server.optimize();
					notifyOptimizeFinished((System.nanoTime() - init)/1000000);
					isOptimizing = false;
				} catch (Exception e) {
					logger.error(e);
					notifyErrorOnOptimize(new OptimizeException(e));
				}
			}
		};
		optimizeThread.start();
	}
	/**
	 * Notifies observers when the optimization started
	 * @param initTime
	 */
	private void notifyOptimizeStatred(long initTime) {
		for(OptimizeStatistic observer:optimizeObservers) {
			observer.onOptimizeStared(initTime);
		}
	}

	/**
	 * Notifies observers when an error ocurrs
	 * @param exception
	 */
	private void notifyErrorOnOptimize(OptimizeException exception) {
		for(OptimizeStatistic observer:optimizeObservers) {
			observer.onOptimizeError(exception);
		}
		this.isOptimizing = false;
	}

	/**
	 * Notifies observers when the operation finishes
	 * @param delay
	 */
	private void notifyOptimizeFinished(long delay) {
		for(OptimizeStatistic observer:optimizeObservers) {
			observer.onOptimizeFinished(delay);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addStatistic(OptimizeStatistic observer) {
		this.optimizeObservers.add(observer);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOptimizing() {
		return isOptimizing;
	}

}
