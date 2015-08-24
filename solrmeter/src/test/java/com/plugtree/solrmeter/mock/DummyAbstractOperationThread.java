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
package com.plugtree.solrmeter.mock;

import com.plugtree.solrmeter.model.operation.RandomOperationExecutorThread;

public class DummyAbstractOperationThread extends RandomOperationExecutorThread {

	private long sleepTime;
	
	private int executionCount = 0;
	
	private int forcedExecutionTimes = -1;
	
	private long forcedSleepTime = -1;
	
	public DummyAbstractOperationThread(long operationInterval, long sleepTime) {
		super(null, operationInterval);
		this.sleepTime = sleepTime;
	}

	@Override
	protected void executeOperation() {
		try {
			Thread.sleep(sleepTime);
			this.executionCount++;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setStopping(boolean stopping) {
		this.stopping.set(stopping);
	}
	
	public boolean isStopping() {
		if(forcedExecutionTimes == -1) {
			return this.stopping.get();
		}else {
			return executionCount >= forcedExecutionTimes;
		}
	}
	
	@Override
	protected long getRandomSleepTime() {
		if(forcedSleepTime < 0) {
			return super.getRandomSleepTime();
		}else {
			return forcedSleepTime;
		}
	}

	public boolean isExecuted() {
		return (executionCount != 0);
	}
	
	public int getExecutionCount() {
		return executionCount;
	}

	public void forceExecutionTimes(int numberOfExecutions) {
		forcedExecutionTimes = numberOfExecutions;
		
	}
	
	public void forceSleepTime(long sleepTime) {
		this.forcedSleepTime = sleepTime;
	}

}
