package com.chm.remote.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author caihongming
 * @version v1.0
 * @title RobotUtils
 * @package com.chm.remote.common.utils
 * @since 2019-11-01
 * description Robot操作类
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RobotUtils {

  private static Toolkit toolkit = Toolkit.getDefaultToolkit();

  private static Robot robot;

  static {
    try {
      robot = new Robot();
    } catch (AWTException e) {
      e.printStackTrace();
    }
  }

  public static BufferedImage getScreenshot(){
    //获取屏幕分辨率
    Dimension d = toolkit.getScreenSize();
    //以屏幕的尺寸创建个矩形
    Rectangle screenRect = new Rectangle(d);
    //截图（截取整个屏幕图片）
    return robot.createScreenCapture(screenRect);
  }

}
