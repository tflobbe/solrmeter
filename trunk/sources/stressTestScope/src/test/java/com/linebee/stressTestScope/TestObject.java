package com.linebee.stressTestScope;
/**
 * 
 * @author tflobbe
 *
 */
@StressTestScope
public class TestObject {

	public static int createdObjects = 0;
	
	public TestObject() {
		createdObjects++;
	}
	
	public static void restartCounter() {
		createdObjects = 0;
	}
}
