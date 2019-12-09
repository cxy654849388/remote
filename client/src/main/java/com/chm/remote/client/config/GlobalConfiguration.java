package com.chm.remote.client.config;

import com.chm.remote.common.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author caihongming
 * @version v1.0
 * @title GlobalConfiguration
 * @package com.chm.remote.client.config
 * @since 2019-11-21
 * description
 **/
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalConfiguration implements Serializable {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private static GlobalConfiguration single;

  /**
   * 个人密码
   */
  private String personalPassword;

  /**
   * 直连服务端端口
   */
  private Integer directServerPort = 9019;

  /**
   * 屏幕刷新频率(ms)
   */
  private Integer screenRefreshFrequency = 500;

  public static GlobalConfiguration getInstance() {
    return null != single ? single : (single = new GlobalConfiguration());
  }

  public void setPersonalPasswordAndSave(String personalPassword) {
    this.setPersonalPassword(personalPassword);
    this.save();
  }

  public void save() {
    try {
      FileUtils.writeStringToFile(new File("conf/setting.json"), GSON.toJson(this), "utf-8");
    } catch (IOException e) {
      LogUtil.logError("写入文件失败", e);
    }
  }

  public static void init() {
    try {
      single = getInstance();
      File configFile = new File("conf/setting.json");
      if (!configFile.exists()) {
        FileUtils.writeStringToFile(configFile, GSON.toJson(single), "utf-8");
      }
      String jsonStr = FileUtils.readFileToString(configFile, "utf-8");
      GlobalConfiguration globalConfiguration = GSON.fromJson(jsonStr, GlobalConfiguration.class);
      if (null != globalConfiguration) {
        BeanUtils.copyProperties(globalConfiguration, single);
      }
    } catch (IOException e) {
      LogUtil.logError("文件初始化失败", e);
    }
  }

}
