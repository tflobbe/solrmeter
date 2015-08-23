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
package com.plugtree.solrmeter.task;

import java.util.Date;

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.mock.DummyAbstractOperationThread;


public class AbstractOperationThreadTestCase extends BaseTestCase {

	public void testStop() {
		DummyAbstractOperationThread thread = new DummyAbstractOperationThread(0, 0);
		thread.setStopping(true);
		thread.run();
		assertFalse(thread.isExecuted());
	}
	public void testStop2() {
		DummyAbstractOperationThread thread = new DummyAbstractOperationThread(10, 0);
		thread.setStopping(false);
		thread.start();
		thread.destroy();
		assertFalse(thread.isExecuted());
	}
	
	public void testRunNoOpTime() throws InterruptedException {
		DummyAbstractOperationThread thread = new DummyAbstractOperationThread(1000, 0);//every 1 second
		thread.start();
		Thread.sleep(5000);
		assertEquals(5, thread.getExecutionCount());
		thread.destroy();
	}
	
	public void testRunSmallOpTime() throws InterruptedException {
		DummyAbstractOperationThread thread = new DummyAbstractOperationThread(1000, 100);//every 1 second
		thread.forceExecutionTimes(1);
		long init = new Date().getTime();
		thread.run();
		long time = new Date().getTime() - init;
		assertTrue("It should take at least one second", time >= 1000);
		assertTrue("It should take at most one second + operation time", time <= 1100);
	}
	
	public void testRunMediumOpTime() throws InterruptedException {
		DummyAbstractOperationThread thread = new DummyAbstractOperationThread(1000, 500);//every 1 second
		thread.forceExecutionTimes(1);
		long init = new Date().getTime();
		thread.run();
		long time = new Date().getTime() - init;
		assertTrue("It should take at least one second", time >= 1000);
		assertTrue("It should take at most one second + operation time", time <= 1500);
	}
	
	public void testRunBigOpTime() throws InterruptedException {
		DummyAbstractOperationThread thread = new DummyAbstractOperationThread(1000, 1500);//every 1 second
		thread.forceExecutionTimes(1);
		long init = new Date().getTime();
		thread.run();
		long time = new Date().getTime() - init;
		assertTrue("It should take at least one second", time >= 1000);
		assertTrue("It should take at most one second + operation time", time <= 2500);
	}
}
