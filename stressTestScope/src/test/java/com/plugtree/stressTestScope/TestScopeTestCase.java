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
package com.plugtree.stressTestScope;

import junit.framework.TestCase;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.plugtree.stressTestScope.StressTestRegistry;
import com.plugtree.stressTestScope.StressTestScopeModule;
/**
 * 
 * @author tflobbe
 *
 */
public class TestScopeTestCase extends TestCase {
	
	private Injector injector;
	
	@Override
	protected void setUp() throws Exception {
		injector = Guice.createInjector(new StressTestScopeModule(), new TestModule());
		TestObject.restartCounter();
	}
	
	public void testJustOneObject() {
		StressTestRegistry.start();
		assertEquals(0, TestObject.createdObjects);
		injector.getInstance(TestObject.class);
		assertEquals(1, TestObject.createdObjects);
		injector.getInstance(TestObject.class);
		assertEquals(1, TestObject.createdObjects);
		
		for(int i = 0; i < 10 ; i++) {
			injector.getInstance(TestObject.class);
			assertEquals(1, TestObject.createdObjects);
		}
	}
	
	public void testManyObject() {
		StressTestRegistry.start();
		assertEquals(0, TestObject.createdObjects);
		injector.getInstance(TestObject.class);
		assertEquals(1, TestObject.createdObjects);
		StressTestRegistry.restart();
		injector.getInstance(TestObject.class);
		assertEquals(2, TestObject.createdObjects);
		
		for(int i = 0; i < 10 ; i++) {
			StressTestRegistry.restart();
			injector.getInstance(TestObject.class);
			assertEquals(3 + i, TestObject.createdObjects);
		}
	}
	
	public void testObjectGraph() {
		StressTestRegistry.start();
		assertEquals(0, TestObject.createdObjects);
		assertEquals(0, TestObject2.createdObjects);
		TestObject object1 = injector.getInstance(TestObject.class);
		assertEquals(1, TestObject.createdObjects);
		assertEquals(0, TestObject2.createdObjects);
		
		TestObject2 object2 = injector.getInstance(TestObject2.class);
		assertEquals(1, TestObject.createdObjects);
		assertEquals(1, TestObject2.createdObjects);
		assertEquals(object1,object2.getTestObject());
		
		StressTestRegistry.restart();
		object2 = injector.getInstance(TestObject2.class);
		assertEquals(2, TestObject.createdObjects);
		assertEquals(2, TestObject2.createdObjects);
		
		object1 = injector.getInstance(TestObject.class);
		assertEquals(2, TestObject.createdObjects);
		assertEquals(2, TestObject2.createdObjects);
		assertEquals(object1,object2.getTestObject());
		System.out.println(object1);
		System.out.println(object2);
	}

}
