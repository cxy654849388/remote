package com.chm.remote.client.task;

import com.chm.remote.client.config.GlobalConfiguration;
import com.chm.remote.client.netty.TransferInfo;
import com.chm.remote.common.command.Commands;
import com.chm.remote.common.transfer.Transfer;
import com.chm.remote.common.utils.ImageUtil;
import com.chm.remote.common.utils.LogUtil;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Set;
import java.util.concurrent.*;

/**
 * @author caihongming
 * @version v1.0
 * @title AsyncTask
 * @package com.chm.remote.client.task
 * @since 2019-12-03
 * description 异步任务
 **/
@Configuration
@EnableScheduling
public class AsyncTask implements SchedulingConfigurer {

  /**
   * 流畅
   */
  private static final Set<TransferInfo> FLUENT_SET = Sets.newHashSet();

  /**
   * 清晰
   */
  private static final Set<TransferInfo> CLEAR_SET = Sets.newHashSet();

  /**
   * 高清
   */
  private static final Set<TransferInfo> HD_SET = Sets.newHashSet();


  private GlobalConfiguration globalConfiguration = GlobalConfiguration.getInstance();

  @Override
  public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
    LogUtil.logOther("注册截图作业");
    int interval = globalConfiguration.getScreenRefreshFrequency();
    scheduledTaskRegistrar.setTaskScheduler(taskScheduler());
    scheduledTaskRegistrar.addFixedRateTask(new ScreenSnapShotTask(FLUENT_SET, 0.5), interval);
    scheduledTaskRegistrar.addFixedRateTask(new ScreenSnapShotTask(CLEAR_SET, 0.8), interval);
    scheduledTaskRegistrar.addFixedRateTask(new ScreenSnapShotTask(HD_SET, 1.0), interval);
  }

  /**
   * 默认的，SchedulingConfigurer 使用的也是单线程的方式，如果需要配置多线程，则需要指定 PoolSize
   *
   * @return
   */
  @Bean("taskScheduler")
  public TaskScheduler taskScheduler() {
    ThreadFactory threadFactory = new ThreadFactoryBuilder().setUncaughtExceptionHandler((t, e) -> {
      if (null != e) {
        // 添加全局异常日志打印
        LogUtil.logError("任务执行异常", e);
      }
    }).setNameFormat("async-task-%d").build();

    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setThreadFactory(threadFactory);
    taskScheduler.setPoolSize(20);
    taskScheduler.setRejectedExecutionHandler((r, e) -> {
      if (!e.isShutdown()) {
        r.run();
      }
      // 记录执行失败的任务到数据库表中
      // 发送告警邮件给相关负责人
    });
    taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
    taskScheduler.setAwaitTerminationSeconds(60);
    taskScheduler.initialize();
    return taskScheduler;
  }

  /**
   * 截图任务
   */
  private static class ScreenSnapShotTask implements Runnable {

    /**
     * 通道集
     */
    private Set<TransferInfo> transferInfoSet;

    /**
     * 清晰度
     */
    private Double quality;

    /**
     * 前一个截屏
     */
    private byte[] previousScreen;

    public ScreenSnapShotTask(Set<TransferInfo> transferInfoSet, Double quality) {
      this.transferInfoSet = transferInfoSet;
      this.quality = quality;

    }

    @Override
    public void run() {
      if (CollectionUtils.isEmpty(transferInfoSet)) {
        LogUtil.logOther("{}通道集为空，不发送截图", quality);
        return;
      }
      final byte[] bytes = ImageUtil.getScreenshotByteArray(quality);
      if (isDifferentFrom(bytes)) {
        final Transfer request = new Transfer();
        request.setCommand(Commands.SCREEN);
        request.setValue(bytes);
        transferInfoSet.forEach(transferInfo -> {
          request.setSource(transferInfo.getControlled());
          request.setTarget(transferInfo.getControl());
          if (isAvailable(transferInfo.getChannel())) {
            LogUtil.logOther("发送截图====》{}", request.getTarget());
            transferInfo.getChannel().writeAndFlush(request);
          }
        });
      } else {
        //如果屏幕相同，则不发送屏幕，发送心跳
        Transfer heartBeatRequest = new Transfer();
        heartBeatRequest.setCommand(Commands.HEARTBEAT);
        transferInfoSet.forEach(transferInfo -> {
          heartBeatRequest.setSource(transferInfo.getControlled());
          heartBeatRequest.setTarget(transferInfo.getControl());
          if (isAvailable(transferInfo.getChannel())) {
            LogUtil.logOther("发送心跳====》{}", heartBeatRequest.getTarget());
            transferInfo.getChannel().writeAndFlush(heartBeatRequest);
          }
        });

      }
    }

    /**
     * 比较上一个屏幕与当前屏幕是否一样
     *
     * @param now
     * @return
     */
    private boolean isDifferentFrom(byte[] now) {
      if (now == null) {
        return false;
      }

      //如果前一个屏幕为空，而且当前屏幕与前一个屏幕不一样，则发送
      if (previousScreen == null || previousScreen.length == 0 || previousScreen.length != now.length) {
        previousScreen = now;
        return true;
      }

      int len = previousScreen.length;
      boolean changeable = false;
      for (int i = 0; i < len; i++) {
        if (previousScreen[i] != now[i]) {
          previousScreen = now;
          changeable = true;
          break;
        }
      }
      return changeable;
    }
  }

  ;

  private static boolean isAvailable(Channel channel) {
    return channel != null && channel.isActive() && channel.isOpen();
  }

  public static void addFluentChannel(TransferInfo transferInfo) {
    AsyncTask.FLUENT_SET.add(transferInfo);
  }

  public static void moveFluentChannel(TransferInfo transferInfo) {
    AsyncTask.FLUENT_SET.add(transferInfo);
    AsyncTask.CLEAR_SET.remove(transferInfo);
    AsyncTask.HD_SET.remove(transferInfo);
  }

  public static void addClearChannel(TransferInfo transferInfo) {
    AsyncTask.CLEAR_SET.add(transferInfo);
  }

  public static void moveClearChannel(TransferInfo transferInfo) {
    AsyncTask.FLUENT_SET.remove(transferInfo);
    AsyncTask.CLEAR_SET.add(transferInfo);
    AsyncTask.HD_SET.remove(transferInfo);
  }

  public static void addHdChannel(TransferInfo transferInfo) {
    AsyncTask.HD_SET.add(transferInfo);
  }

  public static void moveHdChannel(TransferInfo transferInfo) {
    AsyncTask.FLUENT_SET.remove(transferInfo);
    AsyncTask.CLEAR_SET.remove(transferInfo);
    AsyncTask.HD_SET.add(transferInfo);
  }

  public static void remove(TransferInfo transferInfo) {
    if (null == transferInfo) {
      return;
    }
    AsyncTask.FLUENT_SET.remove(transferInfo);
    AsyncTask.CLEAR_SET.remove(transferInfo);
    AsyncTask.HD_SET.remove(transferInfo);
  }
}
