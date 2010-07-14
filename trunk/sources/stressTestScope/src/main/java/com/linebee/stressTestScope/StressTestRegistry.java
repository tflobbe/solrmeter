package com.linebee.stressTestScope;

/**
 * 
 * @author tflobbe
 *
 */
public class StressTestRegistry {
	
	private static long numberOfTests = 0;
	
	private static String stressTestId;

	public static void start() {
		restart();
	}
	
	public static void restart(){
		stressTestId = String.valueOf(numberOfTests);
		numberOfTests++;
	}
	
	static String getStressTestId() {
		return stressTestId;
	}
}
