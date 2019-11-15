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
package com.chm.remote.client.textformatter;

import javafx.util.StringConverter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RegExUtils;

/**
 * @author caihongming
 * @version v1.0
 * @title RemoteIdConverter
 * @package com.chm.remote.client.textformatter
 * @since 2019-11-07
 * description remoteId字符转换器
 **/
@NoArgsConstructor
public class RemoteIdConverter extends StringConverter<String> {

  @Override
  public String toString(String object) {
    return null == object ? "" : RegExUtils.replacePattern(object, "(?<=\\d)(?=(\\d{3})+$)", "\\ ");
  }

  @Override
  public String fromString(String value) {
    // If the specified value is null or zero-length, return null
    if (value == null) {
      return null;
    }
    value = value.trim();
    if (value.length() < 1) {
      return null;
    }
    return value;
  }
}
