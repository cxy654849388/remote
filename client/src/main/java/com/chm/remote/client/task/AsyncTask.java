package com.chm.remote.client.task;

import com.chm.remote.client.netty.TransferInfo;
import com.chm.remote.client.utils.FfmpegUtil;
import com.chm.remote.common.command.Commands;
import com.chm.remote.common.transfer.Transfer;
import com.chm.remote.common.utils.LogUtil;
import com.chm.remote.common.utils.TaskExecutorUtil;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import io.netty.channel.Channel;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * @author caihongming
 * @version v1.0
 * @title AsyncTask
 * @package com.chm.remote.client.task
 * @since 2019-12-03
 * description 异步任务
 **/
@Component
public class AsyncTask implements CommandLineRunner {

  /**
   * 屏幕共享集合
   */
  private static final Set<TransferInfo> SHARED_SET = Sets.newLinkedHashSet();

  /**
   * 视频帧队列
   */
  private static final BlockingQueue<byte[]> FRAME_QUEUE = Queues.newLinkedBlockingQueue();

  @Override
  public void run(String... args) throws Exception {
    LogUtil.logOther("注册截图作业");
    TaskExecutorUtil.submit(new SharedTask(SHARED_SET, FRAME_QUEUE));
  }

  /**
   * 截图任务
   */
  private static class SharedTask implements Runnable {

    /**
     * 通道集
     */
    private Set<TransferInfo> transferInfoSet;

    /**
     * 数据队列
     */
    private BlockingQueue<byte[]> queue;

    /**
     * 前一个数据
     */
    private byte[] previousScreen;

    public SharedTask(Set<TransferInfo> transferInfoSet, BlockingQueue<byte[]> queue) {
      this.transferInfoSet = transferInfoSet;
      this.queue = queue;
    }

    @SneakyThrows
    @Override
    public void run() {
      while (true) {
        byte[] bytes = queue.take();
        if (CollectionUtils.isEmpty(transferInfoSet)) {
          //LogUtil.logOther("{}通道集为空，不发送截图", quality);
          continue;
        }
        if (isDifferentFrom(bytes)) {
          final Transfer request = new Transfer();
          request.setCommand(Commands.FRAME);
          request.setValue(bytes);
          transferInfoSet.forEach(transferInfo -> {
            request.setSource(transferInfo.getControlled());
            request.setTarget(transferInfo.getControl());
            if (isAvailable(transferInfo.getChannel())) {
              LogUtil.logOther("发送数据====》{}", request.getTarget());
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
    }

    /**
     * 比较上一个数据与当前数据是否一样
     *
     * @param now
     * @return
     */
    private boolean isDifferentFrom(byte[] now) {
      if (now == null) {
        return false;
      }

      //如果前一个数据为空，而且当前数据与前一个数据不一样，则发送
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

  private static boolean isAvailable(Channel channel) {
    return channel != null && channel.isActive() && channel.isOpen();
  }

  public static void addSharedChannel(TransferInfo transferInfo) {
    AsyncTask.SHARED_SET.add(transferInfo);
    if (!FfmpegUtil.isStarted()) {
      FfmpegUtil.start();
    }
  }

  public static void removeSharedChannel(TransferInfo transferInfo) {
    AsyncTask.SHARED_SET.add(transferInfo);
    if (CollectionUtils.isEmpty(AsyncTask.SHARED_SET) && FfmpegUtil.isStarted()) {
      FfmpegUtil.stop();
    }
  }

  public static void offerFrame(byte[] bytes) {
    FRAME_QUEUE.offer(bytes);
  }
}
