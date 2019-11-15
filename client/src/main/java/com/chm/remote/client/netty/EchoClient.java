package com.chm.remote.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @author caihongming
 * @version v1.0
 * @title EchoClient
 * @package com.chm.remote.client.netty
 * @since 2019-10-31
 * description
 **/
@Component
public class EchoClient {

  public void start(String host, int port) throws Exception {

    /**
     * Netty用于接收客户端请求的线程池职责如下。
     * （1）接收客户端TCP连接，初始化Channel参数；
     * （2）将链路状态变更事件通知给ChannelPipeline
     */
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(group)
              .channel(NioSocketChannel.class)
              .remoteAddress(new InetSocketAddress(host, port))
              .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                  socketChannel.pipeline().addLast(new EchoClientHandler());
                }
              });
      //绑定端口
      ChannelFuture f = b.connect().sync();

      f.channel().closeFuture().sync();
    } catch (Exception e) {
      group.shutdownGracefully().sync();
    }
  }
}
