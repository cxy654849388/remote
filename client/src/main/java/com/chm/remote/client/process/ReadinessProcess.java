package com.chm.remote.client.process;

import com.chm.remote.common.process.CommandProcess;
import com.chm.remote.common.transfer.Transfer;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

/**
 * @author caihongming
 * @version v1.0
 * @title ReadinessProcess
 * @package com.chm.remote.client.process
 * @since 2019-12-02
 * description
 **/
@Service("readinessProcess")
public class ReadinessProcess implements CommandProcess {

  @Override
  public Transfer process(ChannelHandlerContext ctx, Transfer transfer) {
    return null;
  }
}
