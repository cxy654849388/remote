package com.chm.remote.common.command;

/**
 * @author caihongming
 * @version v1.0
 * @title Commands
 * @package com.chm.remote.common.command
 * @since 2019-11-22
 * description 命令集
 **/
public enum Commands {

  /*  *//**
   * 准备就绪命令，用于检测被控端是否准备好被控制
   *//*
  READINESS("readinessProcess"),*/

  /**
   * 客户端向服务端发起连接命令
   */
  CONNECT("connectDirectClientProcess", "connectDirectServerProcess", "connectServerProcess"),

  /**
   * 控制命令
   * 1.客户端向服务端发送控制请他客户端请求
   * 2.客户端向请他客户端发送控制请求
   */
  CONTROL("controlDirectClientProcess", "controlDirectServerProcess", "controlServerProcess"),

  /**
   * 连接口令
   * 被控端随机密码 or 被控端个人密码
   */
  TOKEN("tokenDirectClientProcess", "tokenDirectServerProcess", "tokenServerProcess"),

  /**
   * 客户端发送心跳给服务器
   */
  HEARTBEAT("heartbeatDirectClientProcess", "heartbeatDirectServerProcess", "heartbeatServerProcess"),

  /**
   * 被控端发送屏幕截图命令
   */
  SCREEN("screenDirectClientProcess", "screenDirectServerProcess", "screenServerProcess"),

  /**
   * 控制端发送键盘事件
   */
  KEYBOARD("keyboardDirectClientProcess", "keyboardDirectServerProcess", "keyboardServerProcess"),

  /**
   * 控制端发送鼠标事件
   */
  MOUSE("mouseDirectClientProcess", "mouseDirectServerProcess", "mouseServerProcess"),

  /**
   * 断开控制
   */
  TERMINATE("terminateDirectClientProcess", "terminateDirectServerProcess", "terminateServerProcess"),

  /**
   * 调整清晰度
   */
  QUALITY("qualityDirectClientProcess", "qualityDirectServerProcess", "qualityServerProcess");

  /**
   * 直连客户端处理接口bean名称
   */
  private final String directClientProcess;

  /**
   * 直连服务端处理接口bean名称
   */
  private final String directServerProcess;

  /**
   * 服务端处理接口bean名称
   */
  private final String serverProcess;

  Commands(String directClientProcess, String directServerProcess, String serverProcess) {
    this.directClientProcess = directClientProcess;
    this.directServerProcess = directServerProcess;
    this.serverProcess = serverProcess;
  }

  public String getDirectClientProcess() {
    return directClientProcess;
  }

  public String getDirectServerProcess() {
    return directServerProcess;
  }

  public String getServerProcess() {
    return serverProcess;
  }
}
