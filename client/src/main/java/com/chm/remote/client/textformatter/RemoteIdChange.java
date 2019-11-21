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
