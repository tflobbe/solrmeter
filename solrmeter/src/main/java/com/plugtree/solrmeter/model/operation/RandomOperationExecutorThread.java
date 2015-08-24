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
package com.plugtree.solrmeter.model.operation;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.plugtree.solrmeter.model.exception.OperationException;

/**
 * 
 * An Operation that has to be executed every N milliseconds. The interval of execution
 * is not precise, it is at least queryInterval (parameter passed to the constructor)
 * and at most queryInterval + operation time. This is because the instant where the
 * operation is executed is randomly chosen inside the N seconds interval.
 * @author tflobbe
 *
 */
public class RandomOperationExecutorThread extends Thread {
	
	protected final static Logger logger = Logger.getLogger(RandomOperationExecutorThread.class);

	protected final AtomicBoolean stopping = new AtomicBoolean(false);
	
	private final long operationIntervalInMs;
	
	/**
	 * Operation to execute
	 */
	private Operation operation;
	
	public RandomOperationExecutorThread(Operation operation, long operationInterval) {
		super();
		this.operationIntervalInMs = operationInterval;
		this.operation = operation;
	}
	
	public void run() {
		while(!isStopping()) {
			long init = System.currentTimeMillis();
			long waitBeforeTime = getRandomSleepTime();
			try {
				Thread.sleep(waitBeforeTime);
			} catch (InterruptedException e) {
				logger.error("Thread interrupted", e);
				stopping.set(true);
				Thread.currentThread().interrupt();
				break;
			}
			if(!isStopping()) {
				executeOperation();
			}
			try {
				
				long diff = (operationIntervalInMs + init) - System.currentTimeMillis();
				if(diff > 0L) {
					Thread.sleep(diff);
				}
			} catch (InterruptedException e) {
			    logger.error("Thread interrupted", e);
			    stopping.set(true);
                Thread.currentThread().interrupt();
                break;
			}
		}
	}
	
	protected long getRandomSleepTime() {
		return (long) (Math.random() * operationIntervalInMs);
	}
	
	protected void executeOperation() {
		try {
			operation.execute();
		} catch (OperationException e) {
			logger.error("There was an error executing operation " + operation, e);
		}
	}

	protected boolean isStopping() {
		return stopping.get();
	}
	
	public void destroy() {
	    this.stopping.set(true);
	}
}
