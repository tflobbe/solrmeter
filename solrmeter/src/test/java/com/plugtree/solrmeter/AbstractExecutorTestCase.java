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
package com.plugtree.solrmeter;

import com.plugtree.solrmeter.mock.MockExecutor;

public class AbstractExecutorTestCase extends BaseTestCase {

	public void testPrepare() throws InterruptedException {
		MockExecutor mockExcecutor = new MockExecutor(10);
		assertEquals(0, mockExcecutor.getExecutedThreads());
		assertEquals(0, mockExcecutor.getThreadCount());
		assertEquals(0, mockExcecutor.getDestroyedThreads());
		mockExcecutor.prepare();
		assertEquals(10, mockExcecutor.getThreadCount());
		assertEquals(0, mockExcecutor.getExecutedThreads());
		assertEquals(0, mockExcecutor.getDestroyedThreads());
		mockExcecutor.start();
		Thread.sleep(100);//until all threads are executed
		assertEquals(10, mockExcecutor.getExecutedThreads());
		assertEquals(10, mockExcecutor.getThreadCount());
		assertEquals(0, mockExcecutor.getDestroyedThreads());
		mockExcecutor.stop();
		Thread.sleep(100);//until all threads are stopped
		assertEquals(0, mockExcecutor.getThreadCount());
		assertEquals(10, mockExcecutor.getDestroyedThreads());
	}
	
	
	public void testIncrementThreadsBeforeStart() throws InterruptedException  {
		MockExecutor mockExcecutor = new MockExecutor(10);
		assertEquals(0, mockExcecutor.getExecutedThreads());
		assertEquals(0, mockExcecutor.getThreadCount());
		mockExcecutor.prepare();
		assertEquals(10, mockExcecutor.getThreadCount());
		assertEquals(0, mockExcecutor.getExecutedThreads());
		mockExcecutor.incrementOperationsPerMinute();
		assertEquals(11, mockExcecutor.getThreadCount());
		assertEquals(0, mockExcecutor.getExecutedThreads());
		mockExcecutor.start();
		Thread.sleep(100);//until all threads are executed
		assertEquals(11, mockExcecutor.getExecutedThreads());
		assertEquals(11, mockExcecutor.getThreadCount());
		mockExcecutor.stop();
		Thread.sleep(100);//until all threads are stopped
		assertEquals(0, mockExcecutor.getThreadCount());
		assertEquals(11, mockExcecutor.getDestroyedThreads());
	}
	
	public void testIncrementThreadsAfterStart() throws InterruptedException  {
		MockExecutor mockExcecutor = new MockExecutor(10);
		assertEquals(0, mockExcecutor.getExecutedThreads());
		assertEquals(0, mockExcecutor.getThreadCount());
		mockExcecutor.prepare();
		assertEquals(10, mockExcecutor.getThreadCount());
		assertEquals(0, mockExcecutor.getExecutedThreads());
		mockExcecutor.start();
		Thread.sleep(100);//until all threads are executed
		assertEquals(10, mockExcecutor.getExecutedThreads());
		assertEquals(10, mockExcecutor.getThreadCount());
		mockExcecutor.incrementOperationsPerMinute();
		Thread.sleep(100);//until the new thrad has executed
		assertEquals(11, mockExcecutor.getExecutedThreads());
		assertEquals(11, mockExcecutor.getThreadCount());
		mockExcecutor.stop();
		Thread.sleep(100);//until all threads are stopped
		assertEquals(0, mockExcecutor.getThreadCount());
		assertEquals(11, mockExcecutor.getDestroyedThreads());
	}
	
	public void testDecrementThreadsBeforeStart() throws InterruptedException  {
		MockExecutor mockExcecutor = new MockExecutor(10);
		assertEquals(0, mockExcecutor.getExecutedThreads());
		assertEquals(0, mockExcecutor.getThreadCount());
		mockExcecutor.prepare();
		assertEquals(10, mockExcecutor.getThreadCount());
		assertEquals(0, mockExcecutor.getExecutedThreads());
		mockExcecutor.decrementOperationsPerMinute();
		assertEquals(9, mockExcecutor.getThreadCount());
		assertEquals(0, mockExcecutor.getExecutedThreads());
		assertEquals(1, mockExcecutor.getDestroyedThreads());//the removed Thread is destroyed
		mockExcecutor.start();
		Thread.sleep(100);//until all threads are executed
		assertEquals(9, mockExcecutor.getExecutedThreads());
		assertEquals(9, mockExcecutor.getThreadCount());
		mockExcecutor.stop();
		Thread.sleep(100);//until all threads are stopped
		assertEquals(0, mockExcecutor.getThreadCount());
		assertEquals(10, mockExcecutor.getDestroyedThreads());
	}
	
	public void testDecrementThreadsAfterStart() throws InterruptedException  {
		MockExecutor mockExcecutor = new MockExecutor(10);
		assertEquals(0, mockExcecutor.getExecutedThreads());
		assertEquals(0, mockExcecutor.getThreadCount());
		mockExcecutor.prepare();
		assertEquals(10, mockExcecutor.getThreadCount());
		assertEquals(0, mockExcecutor.getExecutedThreads());
		mockExcecutor.start();
		Thread.sleep(100);//until all threads are executed
		assertEquals(10, mockExcecutor.getExecutedThreads());
		assertEquals(10, mockExcecutor.getThreadCount());
		mockExcecutor.decrementOperationsPerMinute();
		assertEquals(10, mockExcecutor.getExecutedThreads());
		assertEquals(9, mockExcecutor.getThreadCount());
		assertEquals(1, mockExcecutor.getDestroyedThreads());
		mockExcecutor.stop();
		Thread.sleep(100);//until all threads are stopped
		assertEquals(0, mockExcecutor.getThreadCount());
		assertEquals(10, mockExcecutor.getDestroyedThreads());
	}
}
