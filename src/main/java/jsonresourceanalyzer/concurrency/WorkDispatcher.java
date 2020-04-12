package jsonresourceanalyzer.concurrency;

import jsonresourceanalyzer.ErrorCode;
import jsonresourceanalyzer.constants.ErrorMessages;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class WorkDispatcher {

  private static final int MAX_WORKERS = 10;

  private final ExecutorService fixedThreadPool;
  private final Semaphore semaphore;

  public WorkDispatcher() {
    fixedThreadPool = Executors.newFixedThreadPool(MAX_WORKERS);
    semaphore = new Semaphore(MAX_WORKERS);
  }

  public void dispatch(Runnable runnable) {
    try {
      // wait for a thread to become available before assigning work to the thread pool
      semaphore.acquire();
    } catch (InterruptedException ex) {
      System.err.println(ErrorMessages.WORK_DISPATCHER_THREAD_INTERRUPTED);
      System.exit(ErrorCode.WORK_DISPATCHER_THREAD_INTERRUPTED.getValue());
    }

    fixedThreadPool.submit(() -> {
      runnable.run();

      // after the work is complete release the semaphore lock to allow more work to be
      // dispatched to the thread pool
      semaphore.release();
    });
  }
}
