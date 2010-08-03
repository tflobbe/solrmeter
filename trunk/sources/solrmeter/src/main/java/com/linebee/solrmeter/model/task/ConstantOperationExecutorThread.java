package com.linebee.solrmeter.model.task;

import org.apache.log4j.Logger;

import com.linebee.solrmeter.model.exception.OperationException;
import com.linebee.stressTestScope.StressTestScope;

@StressTestScope
public class ConstantOperationExecutorThread extends Thread {
	
	private long timeToWait;
	
	private boolean running;
	
	private Operation operation;
	
	public ConstantOperationExecutorThread(Operation operation) {
		super();
		this.operation = operation;
	}
	
	@Override
	public void run() {
		while(running) {
			try {
				Thread.sleep(getTimeToWait());
				if(running) {
					executeOperation();
				}
			} catch (InterruptedException e) {
				Logger.getLogger(this.getClass()).error("Error on query thread", e);
				throw new RuntimeException(e);
			} catch (OperationException e) {
				Logger.getLogger(this.getClass()).error("Error on query thread", e);
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public synchronized void start() {
		this.running = true;
		super.start();
	}
	
	@Override
	public void destroy() {
		this.running = false;
	}
	
	private void executeOperation() throws OperationException {
		operation.execute();
	}

	private long getTimeToWait() {
		return timeToWait;
	}

	public void setTimeToWait(long timeToWait) {
		this.timeToWait = timeToWait;
	}
}
