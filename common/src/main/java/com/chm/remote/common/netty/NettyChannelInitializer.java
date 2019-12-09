package com.chm.remote.common.netty;

import com.chm.remote.common.serialization.ProtobufDecoder;
import com.chm.remote.common.serialization.ProtobufEncoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author caihongming
 * @version v1.0
 * @title NettyChannelInitializer
 * @package com.chm.remote.common.netty
 * @since 2019-11-29
 * description 通道初始化类
 **/
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

  /**
   * 处理器
   */
  private ChannelHandler channelHandler;

  public NettyChannelInitializer(ChannelHandler channelHandler) {
    this.channelHandler = channelHandler;
  }

  @Override
  protected void initChannel(SocketChannel socketChannel) throws Exception {
    socketChannel.pipeline().addLast(new ProtobufDecoder())
            .addLast(new ProtobufEncoder())
            .addLast(channelHandler);
  }

}
