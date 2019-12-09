package com.chm.remote.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author caihongming
 * @version v1.0
 * @title SpringUtils
 * @package com.chm.remote.common.utils
 * @since 2019-11-22
 * description
 **/
@Component
public final class SpringUtil implements ApplicationContextAware {

  private static ApplicationContext applicationContext;

  public static <T> T getBean(Class<T> cls) {
    return applicationContext.getBean(cls);
  }

  public static Object getBean(String beanName) {
    return applicationContext.getBean(beanName);
  }

  public static <T> T getBean(String name, Class<T> cls) {
    return applicationContext.getBean(name, cls);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    if (SpringUtil.applicationContext == null) {
      SpringUtil.applicationContext = applicationContext;
    }
  }
}
