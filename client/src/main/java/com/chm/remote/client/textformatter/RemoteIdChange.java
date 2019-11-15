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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import lombok.NoArgsConstructor;

/**
 * @author caihongming
 * @version v1.0
 * @title RemoteIdChange
 * @package com.chm.remote.client.textformatter
 * @since 2019-11-07
 * description 内容监听
 **/
@NoArgsConstructor
public class RemoteIdChange implements ChangeListener<String> {

  @Override
  public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
    System.out.println(111);
  }
}
