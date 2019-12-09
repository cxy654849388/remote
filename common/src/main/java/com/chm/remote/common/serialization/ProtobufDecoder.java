package com.chm.remote.common.serialization;

import com.chm.remote.common.transfer.Transfer;
import com.chm.remote.common.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author caihongming
 * @version v1.0
 * @title ProtobufDecoder
 * @package com.chm.remote.common.serialization
 * @since 2019-11-29
 * description Protobuf解码器
 **/
public class ProtobufDecoder extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
    if (in.readableBytes() < 4) {
      return;
    }
    in.markReaderIndex();

    int dataLength = in.readInt();
    if (dataLength < 0) {
      ctx.close();
    }

    if (in.readableBytes() < dataLength) {
      in.resetReaderIndex();
      return;
    }

    byte[] data = new byte[dataLength];
    in.readBytes(data);

    Object obj = SerializationUtil.deserializer(data, Transfer.class);
    list.add(obj);
  }
}
