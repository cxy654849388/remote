package com.chm.remote.server.netty;

import com.chm.remote.common.utils.LogUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/**
 * @author caihongming
 * @version v1.0
 * @title EchoServer
 * @package com.chm.remote.server
 * @since 2019-10-31
 * description 服务端
 **/
@Component
public class EchoServer {

  /**
   * NioEventLoop并不是一个纯粹的I/O线程，它除了负责I/O的读写之外
   * 创建了两个NioEventLoopGroup，
   * 它们实际是两个独立的Reactor线程池。
   * 一个用于接收客户端的TCP连接，
   * 另一个用于处理I/O相关的读写操作，或者执行系统Task、定时任务Task等。
   */
  private final EventLoopGroup bossGroup = new NioEventLoopGroup();
  private final EventLoopGroup workerGroup = new NioEventLoopGroup();
  private Channel channel;

  /**
   * 启动服务
   *
   * @param port
   * @return
   * @throws Exception
   */
  public ChannelFuture start(int port) throws Exception {

    final EchoServerHandler serverHandler = new EchoServerHandler();
    ChannelFuture f = null;
    try {
      //ServerBootstrap负责初始化netty服务器，并且开始监听端口的socket请求
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
              .channel(NioServerSocketChannel.class)
              .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                  // 为监听客户端read/write事件的Channel添加用户自定义的ChannelHandler
                  socketChannel.pipeline().addLast(serverHandler);
                }
              });
      f = b.bind(port).sync();
      channel = f.channel();
      LogUtil.logOther("======EchoServer启动成功!!!=========");
    } catch (Exception e) {
      LogUtil.logError(e.getMessage(), e);
    } finally {
      if (f != null && f.isSuccess()) {
        LogUtil.logOther("Netty server listening port {} and ready for connections...", port);
      } else {
        LogUtil.logError("Netty server start up Error!");
      }
    }
    return f;
  }

  /**
   * 停止服务
   */
  public void destroy() {
    LogUtil.logOther("Shutdown Netty Server...");
    if (channel != null) {
      channel.close();
    }
    workerGroup.shutdownGracefully();
    bossGroup.shutdownGracefully();
    LogUtil.logOther("Shutdown Netty Server Success!");
  }
}
