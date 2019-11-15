/*
 * Copyright (C) 2011-2019 ShenZhen iBOXCHAIN Information Technology Co.,Ltd.
 *
 * All right reserved.
 *
 * This software is the confidential and proprietary
 * information of iBOXCHAIN Company of China.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with iBOXCHAIN inc.
 *
 */
package com.chm.remote.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * @author caihongming
 * @version v1.0
 * @title LogUtils
 * @package com.chm.remote.common.utils
 * @since 2019-10-31
 * description 日志工具类
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogUtils {

  public static final Logger APP_LOG = LoggerFactory.getLogger("app");
  public static final Logger ERROR_LOG = LoggerFactory.getLogger("error");
  public static final Logger OTHER_LOG = LoggerFactory.getLogger("other");
  public static final String INTERFACE_NAME = "InterfaceTag";
  public static final String METHOD_NAME = "ServerId";
  public static final String PROTOCOL = "Protocol";
  public static final String LOG_TYPE = "LogType";

  private static void removeParams() {
    MDC.remove("InterfaceTag");
    MDC.remove("ServerId");
  }

  private static void addParams() {
    if (MDC.get("InterfaceTag") == null) {
      MDC.put("InterfaceTag", getCallerClass());
    }

    if (MDC.get("ServerId") == null) {
      MDC.put("ServerId", "- " + getCallerMethod());
    }

  }

  private static String getCallerMethod() {
    return getCallName(2);
  }

  private static String getCallerClass() {
    return getCallName(1);
  }

  private static String getCallName(int type) {
    StackTraceElement[] stacks = (new Throwable()).getStackTrace();
    Integer index = null;

    for (int i = 0; i < stacks.length; ++i) {
      if (!LogUtils.class.getName().equals(stacks[i].getClassName())) {
        index = i;
        break;
      }
    }

    if (type == 1) {
      String clazz = index != null ? stacks[index].getClassName() : "";
      if (clazz.lastIndexOf(46) > -1) {
        clazz = clazz.substring(clazz.lastIndexOf(46) + 1);
      }

      return getBlock(clazz);
    } else {
      return index != null ? getBlock(stacks[index].getMethodName()) : "";
    }
  }

  public static void logApp(String message) {
    addParams();
    APP_LOG.info(message);
    removeParams();
  }

  public static void logApp(String message, Object... params) {
    addParams();
    APP_LOG.info(message, params);
    removeParams();
  }

  public static void logError(String message) {
    logError(message, null);
  }

  public static void logError(String message, Throwable e) {
    addParams();
    ERROR_LOG.error(message, e);
    removeParams();
  }

  public static void logOther(String text) {
    addParams();
    OTHER_LOG.info(text);
    removeParams();
  }

  public static void logOther(String text, Object... params) {
    addParams();
    OTHER_LOG.info(text, params);
    removeParams();
  }

  public static String getBlock(Object msg) {
    if (msg == null) {
      msg = "";
    }

    return "[" + msg.toString() + "]";
  }

  public static String formatMsg(Object msg) {
    if (msg == null) {
      msg = "null";
    }

    return msg.toString() + "|";
  }

}
