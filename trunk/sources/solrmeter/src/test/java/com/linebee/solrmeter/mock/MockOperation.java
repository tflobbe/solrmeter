package com.linebee.solrmeter.mock;

import com.linebee.solrmeter.model.task.Operation;

public class MockOperation implements Operation {
	
	private int executions = 0;

	@Override
	public void execute() {
		executions++;
	}
	
	public int getExecutionTimes() {
		return executions;
	}

}
