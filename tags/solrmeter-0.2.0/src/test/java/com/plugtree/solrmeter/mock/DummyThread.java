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

public class DummyThread extends RandomOperationExecutorThread {
	
	private MockExecutor executor;

	public DummyThread(MockExecutor executor) {
		super(null, 0);
		this.executor = executor;
	}
	
	
	@Override
	public void run() {
		executor.onThreadExecuted(this);
	}


	@Override
	protected void executeOperation() {
		throw new RuntimeException("not supose to get here");
	}
	
	@Override
	public void destroy() {
		executor.onDestroyedThread(this);
		super.destroy();
	}
}
