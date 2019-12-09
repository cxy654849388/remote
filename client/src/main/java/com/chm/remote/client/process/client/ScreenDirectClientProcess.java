package com.chm.remote.client.process.client;

import com.chm.remote.client.controller.ConsoleController;
import com.chm.remote.common.process.CommandProcess;
import com.chm.remote.common.transfer.Transfer;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author caihongming
 * @version v1.0
 * @title ScreenDirectClientProcess
 * @package com.chm.remote.client.process.client
 * @since 2019-12-06
 * description 控制端接收受控端屏幕截图
 **/
@Service("screenDirectClientProcess")
public class ScreenDirectClientProcess implements CommandProcess {

  @Autowired
  private ConsoleController consoleController;

  @Override
  public Transfer process(ChannelHandlerContext ctx, Transfer transfer) {
    byte[] data = (byte[]) transfer.getValue();
    consoleController.setImage(ctx, data);
    return null;
  }
}
