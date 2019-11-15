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
public class ImageUtils {

  public static byte[] getScreenshotByteArray(double quality) {
    try {
      //截图（截取整个屏幕图片）
      BufferedImage bufferedImage = RobotUtils.getScreenshot();
      final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      Thumbnails.of(bufferedImage).scale(1f).outputFormat("jpg").outputQuality(quality).toOutputStream(byteArrayOutputStream);
      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    InputStream inputStream = new ByteArrayInputStream(getScreenshotByteArray(1.0));
    Thumbnails.of(inputStream).size(1920,1080).toFile("C:\\Users\\caihongming.IBOXPAY\\Desktop\\1.jpg");
  }

}
