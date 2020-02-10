package com.chm.remote.client.utils;

import java.awt.*;

/**
 * @author caihongming
 * @version v1.0
 * @title MonitorInfo
 * @package com.chm.remote.client.utils
 * @since 2019-12-12
 * description 显示器信息
 **/
public class MonitorInfo {

  private static VirtualScreenBoundingBox boundingBox;
  private static Rectangle primaryScreenBounds;

  public static VirtualScreenBoundingBox getVirtualScreenBoundingBox() {
    if (boundingBox == null) {
      setBoundingBox();
    }
    return boundingBox;
  }

  public static Rectangle getPrimaryScreenBounds() {
    if (primaryScreenBounds == null) {
      primaryScreenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
              .getDefaultConfiguration().getBounds();
    }
    return primaryScreenBounds;
  }

  private static void setBoundingBox() {
    int topLeftX = Integer.MAX_VALUE;
    int topLeftY = Integer.MAX_VALUE;
    int lowerRightX = Integer.MIN_VALUE;
    int loverRightY = Integer.MIN_VALUE;

    for (GraphicsDevice graphicsDevice : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
      Rectangle currentMonitorBounds = graphicsDevice.getDefaultConfiguration().getBounds();
      int currentTopLeftX = currentMonitorBounds.x;
      int currentTopLeftY = currentMonitorBounds.y;
      if (currentTopLeftX < topLeftX) {
        topLeftX = currentTopLeftX;
      }
      if (currentTopLeftY < topLeftY) {
        topLeftY = currentTopLeftY;
      }
      int currentWidth = currentMonitorBounds.width;
      int currentHeight = currentMonitorBounds.height;
      int currentLowerRightX = currentTopLeftX + currentWidth;
      int currentLowerRightY = currentTopLeftY + currentHeight;
      if (currentLowerRightX > lowerRightX) {
        lowerRightX = currentLowerRightX;
      }
      if (currentLowerRightY > loverRightY) {
        loverRightY = currentLowerRightY;
      }
    }

    int width = lowerRightX - topLeftX;
    int height = loverRightY - topLeftY;

    boundingBox = new VirtualScreenBoundingBox(topLeftX, topLeftY, lowerRightX, loverRightY, width, height);
  }

  public static class VirtualScreenBoundingBox {

    public int topLeftX;
    public int topLeftY;
    public int lowerRightX;
    public int lowerRightY;
    public int width;
    public int height;

    VirtualScreenBoundingBox(int topLeftX, int topLeftY, int lowerRightX, int lowerRightY, int width, int height) {
      this.topLeftX = topLeftX;
      this.topLeftY = topLeftY;
      this.lowerRightX = lowerRightX;
      this.lowerRightY = lowerRightY;
      this.width = width;
      this.height = height;
    }
  }

}
