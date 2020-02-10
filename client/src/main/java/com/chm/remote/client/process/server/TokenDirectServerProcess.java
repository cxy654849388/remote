package com.chm.remote.client.process.server;

import com.chm.remote.client.netty.handler.DirectServerHandler;

import com.chm.remote.client.config.GlobalConfiguration;
import com.chm.remote.client.netty.TransferInfo;
import com.chm.remote.client.task.AsyncTask;
import com.chm.remote.client.utils.TemVariables;
import com.chm.remote.common.command.Commands;
import com.chm.remote.common.process.CommandProcess;
import com.chm.remote.common.transfer.Transfer;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author caihongming
 * @version v1.0
 * @title TokenDirectServerProcess
 * @package com.chm.remote.client.process.server
 * @since 2019-12-04
 * description 访问密码命令处理服务端实现
 **/
@Service("tokenDirectServerProcess")
public class TokenDirectServerProcess implements CommandProcess {

  private GlobalConfiguration globalConfiguration = GlobalConfiguration.getInstance();

  @Autowired
  private DirectServerHandler directServerHandler;

  @Override
  public Transfer process(ChannelHandlerContext ctx, Transfer transfer) {

    String password = (String) transfer.getValue();
    Transfer result = new Transfer();
    result.setSource(transfer.getTarget());
    result.setTarget(transfer.getSource());
    result.setCommand(Commands.TOKEN);
    if (StringUtils.equalsAny(password, globalConfiguration.getPersonalPassword(), TemVariables.getRandomPassword())) {
      // 连接成功
      result.setValue(true);
      // 将控制端加入截图群发组
      TransferInfo transferInfo = new TransferInfo();
      transferInfo.setControlled(transfer.getTarget());
      transferInfo.setControl(transfer.getSource());
      transferInfo.setChannel(ctx.channel());
      AsyncTask.addSharedChannel(transferInfo);
      directServerHandler.putTransferInfo(ctx, transferInfo);
    } else {
      // 连接失败
      result.setValue(false);
    }
    return result;
  }

}
