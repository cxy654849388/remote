package com.chm.remote.common.serialization;

import com.chm.remote.common.utils.LogUtil;
import com.chm.remote.common.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author caihongming
 * @version v1.0
 * @title ProtobufEncoder
 * @package com.chm.remote.common.serialization
 * @since 2019-11-29
 * description Protobuf编码器
 **/
public class ProtobufEncoder extends MessageToByteEncoder {

  @Override
  protected void encode(ChannelHandlerContext channelHandlerContext, Object in, ByteBuf out) throws Exception {
    byte[] data = SerializationUtil.serializer(in);
    int length = data.length;
    out.writeInt(length);
    out.writeBytes(data);
  }
}
