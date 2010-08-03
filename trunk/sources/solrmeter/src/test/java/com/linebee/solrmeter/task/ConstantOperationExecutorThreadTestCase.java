package com.linebee.solrmeter.task;

import com.linebee.solrmeter.BaseTestCase;
import com.linebee.solrmeter.mock.MockOperation;
import com.linebee.solrmeter.model.task.ConstantOperationExecutorThread;

public class ConstantOperationExecutorThreadTestCase extends BaseTestCase {

	public void test() {
		try {
			MockOperation operation = new MockOperation();
			ConstantOperationExecutorThread executor = new ConstantOperationExecutorThread(operation);
			executor.setTimeToWait(1000);
			executor.start();
			Thread.sleep(1100);
			assertEquals(1, operation.getExecutionTimes());
			Thread.sleep(1100);
			assertEquals(2, operation.getExecutionTimes());
			executor.destroy();
			Thread.sleep(2000);
			assertEquals(2, operation.getExecutionTimes());
			
		} catch (InterruptedException e) {
			fail(e);
		}
		
		
	}
}
