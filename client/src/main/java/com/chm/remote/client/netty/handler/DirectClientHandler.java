package com.chm.remote.client.netty.handler;

import com.chm.remote.common.process.CommandProcess;
import com.chm.remote.common.transfer.Transfer;
import com.chm.remote.common.utils.LogUtil;
import com.chm.remote.common.utils.SpringUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

/**
 * @author caihongming
 * @version v1.0
 * @title DirectClientHandler
 * @package com.chm.remote.client.netty
 * @since 2019-11-22
 * description
 **/
@Component
@ChannelHandler.Sharable
public class DirectClientHandler extends SimpleChannelInboundHandler<Transfer> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Transfer transfer) throws Exception {
    try {
      CommandProcess commandProcess = SpringUtil.getBean(transfer.getCommand().getDirectClientProcess(), CommandProcess.class);
      Transfer result = commandProcess.process(ctx, transfer);
      if (null == result) {
        return;
      }
      ctx.writeAndFlush(result);
    } catch (Exception e) {
      LogUtil.logError(e.getMessage(), e);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LogUtil.logError("连接异常", cause);
    ctx.close();
  }
}
