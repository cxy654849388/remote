package com.chm.remote.client.process;

import com.chm.remote.client.config.GlobalConfiguration;
import com.chm.remote.client.utils.TemVariables;
import com.chm.remote.common.process.CommandProcess;
import com.chm.remote.common.transfer.Transfer;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author caihongming
 * @version v1.0
 * @title ControlProcess
 * @package com.chm.remote.client.process
 * @since 2019-11-22
 * description 控制命令处理实现
 **/
@Service("controlProcess")
public class ControlProcess implements CommandProcess {

  private GlobalConfiguration globalConfiguration = GlobalConfiguration.getInstance();

  @Override
  public Transfer process(ChannelHandlerContext ctx, Transfer transfer) {
    String password = (String) transfer.getValue();
    if (StringUtils.equalsAny(password, globalConfiguration.getPersonalPassword(), TemVariables.getRandomPassword())) {
      // 连接成功
      return null;
    }
    // 连接失败
    return null;
  }
}
