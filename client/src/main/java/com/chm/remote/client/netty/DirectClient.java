package com.chm.remote.client.netty;

import com.chm.remote.client.utils.DialogBuilder;
import com.chm.remote.common.command.Commands;

import com.chm.remote.client.netty.handler.DirectClientHandler;
import com.chm.remote.common.netty.NettyChannelInitializer;
import com.chm.remote.common.transfer.Transfer;
import com.chm.remote.common.utils.IpUtil;
import com.chm.remote.common.utils.LogUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author caihongming
 * @version v1.0
 * @title DirectClient
 * @package com.chm.remote.client.netty
 * @since 2019-11-22
 * description 直连客户端
 **/
@Component
public class DirectClient {

  final NioEventLoopGroup group = new NioEventLoopGroup();

  @Autowired
  private DirectClientHandler directServerHandler;

  public void connect(String host, Integer port) {
    try {
      final Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(group)
              .channel(NioSocketChannel.class)
              .option(ChannelOption.TCP_NODELAY, true)
              .handler(new NettyChannelInitializer(directServerHandler));
      final ChannelFuture sync = bootstrap.connect(host, port).sync();
      Transfer transfer = buildConnectRequest(host, port);
      if (null == transfer) {
        sync.channel().close();
        return;
      }
      sync.channel().writeAndFlush(transfer);
      sync.channel().closeFuture();
    } catch (Exception e) {
      new DialogBuilder().setTitle("连接错误").setMessage("无法连接到远端控制台").setPositiveBtn("确定").setNegativeBtn("取消").create();
      LogUtil.logError("远端连接异常", e);
    }
  }

  private Transfer buildConnectRequest(String host, Integer port) {
    AtomicReference<Transfer> request = new AtomicReference<>(new Transfer());
    request.get().setSource(IpUtil.getIp());
    request.get().setTarget(String.format("%s:%d", host, port));
    request.get().setCommand(Commands.TOKEN);
    //返回一个输入结果result
    //相关的逻辑操作
    new DialogBuilder().setTitle("输入密码").setTextFieldText(request.get()::setValue).setPositiveBtn("确定").setNegativeBtn("取消", () -> request.set(null)).create();
    return request.get();
  }

  /**
   * 停止服务
   */
  @PreDestroy
  public void destroy() {
    LogUtil.logOther("Shutdown Direct Client...");
    group.shutdownGracefully();
    LogUtil.logOther("Shutdown Direct Client Success!");
  }

}
