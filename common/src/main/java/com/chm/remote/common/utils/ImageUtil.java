package com.chm.remote.common.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author caihongming
 * @version v1.0
 * @title ImageUtils
 * @package com.chm.remote.common.utils
 * @since 2019-11-01
 * description 图像处理工具类
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImageUtil {

  public static byte[] getScreenshotByteArray() {
    return ImageUtil.getScreenshotByteArray(1.0);
  }

  public static byte[] getScreenshotByteArray(double quality) {
    try {
      //截图（截取整个屏幕图片）
      BufferedImage bufferedImage = RobotUtil.getScreenshot();
      final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      Thumbnails.of(bufferedImage).scale(1f).outputFormat("jpg").outputQuality(quality).toOutputStream(byteArrayOutputStream);
      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 字节数组转化为图像
   *
   * @param data 字节数组
   * @return 图像
   */
  public static Image getImageFromByteArray(byte[] data) {
    try {
      final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
      return SwingFXUtils.toFXImage(Thumbnails.of(byteArrayInputStream).scale(1f).outputFormat("jpg").asBufferedImage(), null);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    InputStream inputStream = new ByteArrayInputStream(getScreenshotByteArray(1.0));
    Thumbnails.of(inputStream).size(1920, 1080).toFile("C:\\Users\\caihongming.IBOXPAY\\Desktop\\1.jpg");
  }

}
