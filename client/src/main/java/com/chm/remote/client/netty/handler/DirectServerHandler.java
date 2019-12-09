package com.chm.remote.client.netty.handler;

import com.chm.remote.client.netty.TransferInfo;
import com.chm.remote.client.task.AsyncTask;
import com.chm.remote.common.process.CommandProcess;
import com.chm.remote.common.transfer.Transfer;
import com.chm.remote.common.utils.LogUtil;
import com.chm.remote.common.utils.SpringUtil;
import com.google.common.collect.Maps;
import io.netty.channel.*;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @title DirectServerHandler
 * @package com.chm.remote.client.netty
 * @since 2019-11-22
 * description
 **/
@Component
@ChannelHandler.Sharable
public class DirectServerHandler extends SimpleChannelInboundHandler<Transfer> {

  private Map<ChannelHandlerContext, TransferInfo> transferInfoMap = Maps.newConcurrentMap();

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Transfer transfer) throws Exception {
    try {
      CommandProcess commandProcess = SpringUtil.getBean(transfer.getCommand().getDirectServerProcess(), CommandProcess.class);
      Transfer result = commandProcess.process(ctx, transfer);
      if (null == result) {
        return;
      }
      ctx.writeAndFlush(result);
    } catch (Exception ignored) {
    }
  }


  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LogUtil.logError("连接异常", cause);
    AsyncTask.remove(transferInfoMap.get(ctx));
    transferInfoMap.remove(ctx);
    ctx.close();
  }


  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    LogUtil.logOther("客户端连接:{}", ctx);
    super.channelRegistered(ctx);
  }


  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    LogUtil.logOther("客户端断开:{}", ctx);
    super.channelUnregistered(ctx);
    AsyncTask.remove(transferInfoMap.get(ctx));
    transferInfoMap.remove(ctx);
  }

  public void putTransferInfo(ChannelHandlerContext ctx, TransferInfo transferInfo) {
    this.transferInfoMap.put(ctx, transferInfo);
  }
}
