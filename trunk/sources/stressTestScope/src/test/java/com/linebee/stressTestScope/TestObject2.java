package com.linebee.stressTestScope;

import com.google.inject.Inject;
/**
 * 
 * @author tflobbe
 *
 */
@StressTestScope
public class TestObject2 {

	public static int createdObjects = 0;
	
	private TestObject testObject;
	
	@Inject
	public TestObject2(TestObject testObject) {
		this.testObject = testObject;
		createdObjects++;
	}
	
	public static void restartCounter() {
		createdObjects = 0;
	}

	public TestObject getTestObject() {
		return testObject;
	}
}
