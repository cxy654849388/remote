package com.chm.remote.common.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author caihongming
 * @version v1.0
 * @title TaskExecutors
 * @package com.chm.remote.common.utils
 * @since 2019-12-13
 * description 异步任务工具
 **/
public final class TaskExecutorUtil {

  private static ThreadFactory threadFactory = new ThreadFactoryBuilder().setUncaughtExceptionHandler((t, e) -> {
    if (null != e) {
      // 添加全局异常日志打印
      LogUtil.logError("任务执行异常", e);
    }
  }).setNameFormat("async-task-%d").build();

  /**
   * 计划线程池
   */
  private static final ScheduledExecutorService asyncTaskService = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1,
          threadFactory,
          new ThreadPoolExecutor.AbortPolicy());

  /**
   * 提交任务执行
   *
   * @param callable 任务
   * @param interval 重试间隔
   * @param times    重试次数
   * @param <T>      任务结果类型
   * @return 任务执行结果
   */
  public static <T> T submit(Callable<T> callable, long interval, int times) {
    ThreadLocal<Integer> count = new ThreadLocal<>();
    count.set(1);
    //第一次立即执行
    ScheduledFuture<T> schedule = asyncTaskService.schedule(callable, 0, TimeUnit.MICROSECONDS);
    //执行失败，则重试
    try {
      if (schedule.get() == null) {
        while (count.get() <= times) {
          schedule = asyncTaskService.schedule(callable, interval, TimeUnit.MILLISECONDS);
          //注意:要想重试，callable中的返回值必须null
          if (schedule.get() != null) {
            return schedule.get();
          }
          count.set(count.get() + 1);
        }
      } else {
        return schedule.get();
      }
    } catch (ExecutionException | InterruptedException e) {
      LogUtil.logError(e.getMessage(), e);
    } finally {
      //清除记数
      count.remove();
    }

    return null;
  }

  public static void submit(Runnable task) {
    asyncTaskService.schedule(task, 0, TimeUnit.MILLISECONDS);
  }

  public static void submit(Runnable task, long delay) {
    asyncTaskService.schedule(task, delay, TimeUnit.MILLISECONDS);
  }

  public static void submit(Runnable task, long delay, long period) {
    asyncTaskService.scheduleAtFixedRate(task, delay, period, TimeUnit.MILLISECONDS);
  }

  public static void shutdown() {
    asyncTaskService.shutdown();
  }
}
