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

import com.plugtree.solrmeter.model.executor.AbstractRandomExecutor;
import com.plugtree.solrmeter.model.operation.RandomOperationExecutorThread;

public class MockExecutor extends AbstractRandomExecutor {
	
	private int executedThreads;
	
	private int destroyedThreads;
	
	public MockExecutor(int operations) {
		super();
		this.operationsPerSecond = operations;
		destroyedThreads = 0;
		executedThreads = 0;
	}

	@Override
	protected RandomOperationExecutorThread createThread() {
		return new DummyThread(this);
	}

	@Override
	protected void stopStatistics() {
		// TODO Auto-generated method stub

	}

	public void onThreadExecuted(DummyThread dummyThread) {
		executedThreads++;
	}

	public int getExecutedThreads() {
		return executedThreads;
	}

	public void setExecutedThreads(int executedThreads) {
		this.executedThreads = executedThreads;
	}
	
	public int getThreadCount() {
		if(threads == null) {
			return 0;
		}
		return threads.size();
	}

	public void onDestroyedThread(DummyThread dummyThread) {
		destroyedThreads++;
		
	}

	public int getDestroyedThreads() {
		return destroyedThreads;
	}

	public void setDestroyedThreads(int destroyedThreads) {
		this.destroyedThreads = destroyedThreads;
	}

	@Override
	protected String getOperationsPerSecondConfigurationKey() {
		return "";
	}


}
