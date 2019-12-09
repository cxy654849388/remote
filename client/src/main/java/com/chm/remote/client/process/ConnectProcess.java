package com.chm.remote.client.process;

import com.chm.remote.common.process.CommandProcess;
import com.chm.remote.common.transfer.Transfer;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

/**
 * @author caihongming
 * @version v1.0
 * @title ConnectProcess
 * @package com.chm.remote.client.process
 * @since 2019-11-22
 * description 连接命令处理实现
 **/
@Service("connectProcess")
public class ConnectProcess implements CommandProcess {


  /**
   * 处理命令
   *
   * @param transfer
   */
  @Override
  public Transfer process(ChannelHandlerContext ctx, Transfer transfer) {
    return null;
  }
}
