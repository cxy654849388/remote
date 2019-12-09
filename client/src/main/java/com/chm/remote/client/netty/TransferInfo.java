package com.chm.remote.client.netty;

import io.netty.channel.Channel;
import lombok.Data;

/**
 * @author caihongming
 * @version v1.0
 * @title TransferInfo
 * @package com.chm.remote.client.netty
 * @since 2019-12-04
 * description 通信双方信息
 **/
@Data
public class TransferInfo {

  /**
   * 受控端
   */
  private String controlled;

  /**
   * 控制端
   */
  private String control;

  /**
   * 通道
   */
  private Channel channel;

}
