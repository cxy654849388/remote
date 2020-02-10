package com.chm.remote.client.utils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author caihongming
 * @version v1.0
 * @title TemVariablesMaps
 * @package com.chm.remote.client.utils
 * @since 2019-11-22
 * description 临时变量
 **/
public final class TemVariables {

  /**
   * 随机密码
   */
  private static String randomPassword;

  public static String getRandomPassword() {
    return randomPassword;
  }

  public static void setRandomPassword(String randomPassword) {
    TemVariables.randomPassword = randomPassword;
  }
}
