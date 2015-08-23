package com.plugtree.solrmeter.task;

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.mock.MockOperation;
import com.plugtree.solrmeter.model.operation.ConstantOperationExecutorThread;

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
