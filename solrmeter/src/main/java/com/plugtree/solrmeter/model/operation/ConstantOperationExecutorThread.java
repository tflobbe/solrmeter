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
package com.plugtree.solrmeter.model.operation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.model.exception.OperationException;

/**
 * Worker that executes an operation every N milliseconds, represented by the
 * 'timeToWait' field.
 * The operation is executed on a different thread, that way the operation time
 * does not affect the interval of this worker. For example, if the worker has a
 * timeToWait of 20 seconds and it executes a query that takes 10 seconds, the next
 * query will be executed 20 seconds after the last one STARTED (10 seconds after it
 * finished).
 * @author tflobbe
 *
 */
@StressTestScope
public class ConstantOperationExecutorThread extends Thread {

  private final ExecutorService threadPool = Executors.newCachedThreadPool();

  private final AtomicLong timeToWait = new AtomicLong(1);

  private final AtomicBoolean running = new AtomicBoolean(false);

  private final Operation operation;

  public ConstantOperationExecutorThread(Operation operation) {
    super();
    this.operation = operation;
  }

  @Override
  public synchronized void run() {
    while(running.get()) {
      try {
        this.wait(getTimeToWait());
        if(running.get()) {
          executeOperation();
        }
      } catch (InterruptedException e) {
        Logger.getLogger(this.getClass()).error("Thread interrupted", e);
        Thread.currentThread().interrupt();
        running.set(false);
        break;
      } catch (OperationException e) {
        Logger.getLogger(this.getClass()).error("Error on query thread", e);
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public synchronized void start() {
    this.running.set(true);
    super.start();
  }

  public synchronized void wake() {
    this.notify();
  }

  public void destroy() {
    running.set(false);
  }

  private void executeOperation() throws OperationException {
    Runnable r = new Runnable() {

      @Override
      public void run() {
        try {
          operation.execute();
        } catch (OperationException e) {
          Logger.getLogger(this.getClass()).error("There was an error executing operation " + operation, e);
        }
      }
    };
    threadPool.execute(r);
  }

  private long getTimeToWait() {
    return timeToWait.get();
  }

  public void setTimeToWait(long timeToWait) {
    this.timeToWait.set(timeToWait);
  }
}
