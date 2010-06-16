/**
 * Copyright Linebee LLC
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
package com.linebee.solrmeter.mock;

import com.linebee.solrmeter.model.task.AbstractOperationThread;

public class DummyThread extends AbstractOperationThread {
	
	private MockExecutor executor;

	public DummyThread(MockExecutor executor) {
		super(0);
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
