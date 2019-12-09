package com.chm.remote.common.transfer;

import com.chm.remote.common.command.Commands;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * @author caihongming
 * @version v1.0
 * @title Transfer
 * @package com.chm.remote.common.transfer
 * @since 2019-11-22
 * description 数据传递类
 **/
@Data
public class Transfer implements Serializable {

  /**
   * 数据发起端
   * ID or IP
   */
  private String source;

  /**
   * 数据目标端
   * ID or IP:PORT
   */
  private String target;

  /**
   * 命令
   */
  private Commands command;

  /**
   * 请求内容
   */
  private Object value;

  /**
   * 错误信息
   */
  private String message;

  /**
   * 错误异常
   */
  private Exception exception;

  @Override
  public String toString() {
    return new ToStringBuilder(this)
            .append("source", source)
            .append("target", target)
            .append("command", command)
            .append("value", value)
            .append("message", message)
            .append("exception", exception)
            .toString();
  }
}
