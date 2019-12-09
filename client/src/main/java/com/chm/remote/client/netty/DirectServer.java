package com.chm.remote.client.netty;

import com.chm.remote.client.config.GlobalConfiguration;
import com.chm.remote.client.netty.handler.DirectServerHandler;
import com.chm.remote.common.netty.NettyChannelInitializer;
import com.chm.remote.common.utils.LogUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author caihongming
 * @version v1.0
 * @title DirectServer
 * @package com.chm.remote.client.netty
 * @since 2019-11-22
 * description 直连服务端
 **/
@Component
public class DirectServer implements CommandLineRunner {

  final NioEventLoopGroup boss = new NioEventLoopGroup();

  final NioEventLoopGroup worker = new NioEventLoopGroup();

  private Channel channel;

  private GlobalConfiguration globalConfiguration = GlobalConfiguration.getInstance();

  @Autowired
  private DirectServerHandler directServerHandler;


  @Override
  @Async
  public void run(String... args) throws Exception {
    ChannelFuture future = bind(globalConfiguration.getDirectServerPort());
    //服务端管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束进程
    future.channel().closeFuture().syncUninterruptibly();
  }

  private ChannelFuture bind(int port) throws InterruptedException {
    final ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(boss, worker)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childHandler(new NettyChannelInitializer(directServerHandler));

    final ChannelFuture f = bootstrap.bind(port).sync();
    channel = f.channel();
    LogUtil.logOther("Direct Server Start On Port:{}", port);
    return f;
  }

  /**
   * 停止服务
   */
  @PreDestroy
  public void destroy() {
    LogUtil.logOther("Shutdown Direct Server...");
    if (channel != null) {
      channel.close();
      channel = null;
    }
    worker.shutdownGracefully();
    boss.shutdownGracefully();
    LogUtil.logOther("Shutdown Direct Server Success!");
  }
}
