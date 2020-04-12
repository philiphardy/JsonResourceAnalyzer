package jsonresourceanalyzer.concurrency;

import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import jsonresourceanalyzer.ErrorCode;
import jsonresourceanalyzer.constants.ErrorMessages;

public class WorkDispatcher {

  private static final int MAX_WORKERS = 10;

  private final ThreadPoolExecutor threadPool;
  private final Semaphore semaphore;

  public WorkDispatcher() {
    threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_WORKERS);
    threadPool.prestartAllCoreThreads();
    semaphore = new Semaphore(MAX_WORKERS);
  }

  /**
   * Dispatches work to the worker thread pool. If no available workers in the pool, the calling
   * thread will block until one becomes available.
   *
   * @param runnable Runnable work to be dispatched.
   */
  public void dispatch(Runnable runnable) {
    try {
      // wait for a thread to become available before assigning work to the thread pool
      semaphore.acquire();
    } catch (InterruptedException ex) {
      System.err.println(ErrorMessages.WORK_DISPATCHER_THREAD_INTERRUPTED);
      System.exit(ErrorCode.WORK_DISPATCHER_THREAD_INTERRUPTED.getValue());
    }

    threadPool.submit(() -> {
      runnable.run();

      // after the work is complete release the semaphore lock to allow more work to be
      // dispatched to the thread pool
      semaphore.release();
    });
  }

  /**
   * Causes calling thread to wait until the thread pool has finished.
   */
  public void waitForWorkToComplete() {
    while (threadPool.getActiveCount() > 0) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException ex) {
        System.err.println(ErrorMessages.WORK_DISPATCHER_THREAD_INTERRUPTED);
        System.exit(ErrorCode.WORK_DISPATCHER_THREAD_INTERRUPTED.getValue());
      }
    }
  }
}
