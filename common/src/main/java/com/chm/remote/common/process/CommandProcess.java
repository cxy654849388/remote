package com.chm.remote.common.process;

import com.chm.remote.common.transfer.Transfer;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author caihongming
 * @version v1.0
 * @title CommadProcess
 * @package com.chm.remote.common.process
 * @since 2019-11-22
 * description 命令处理接口
 **/
public interface CommandProcess {

  /**
   * 处理命令
   *
   * @param ctx
   * @param transfer
   * @return 处理结果
   */
  Transfer process(ChannelHandlerContext ctx, Transfer transfer);
}
