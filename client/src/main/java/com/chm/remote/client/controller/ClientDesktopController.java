package com.chm.remote.client.controller;

import com.chm.remote.client.config.GlobalConfiguration;
import com.chm.remote.client.form.PersonalSettings;
import com.chm.remote.client.netty.DirectClient;
import com.chm.remote.client.textformatter.RemoteIdConverter;
import com.chm.remote.client.utils.DialogBuilder;
import com.chm.remote.client.utils.FxUtils;
import com.chm.remote.client.utils.TemVariables;
import com.chm.remote.common.utils.LogUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author caihongming
 * @version v1.0
 * @title ClientDesktopController
 * @package com.chm.remote.client.form
 * @since 2019-11-05
 * description 客户端桌面程序控制器
 **/
@FXMLController
public class ClientDesktopController implements Initializable {

  private static final String REMOTE_ID = "远端id";

  private static final String REMOTE_IP_PORT = "远端ip:端口";

  @FXML
  private StackPane rootPane;

  /**
   * 连接远端按钮
   */
  @FXML
  private Button connectionBtn;

  /**
   * 重置随机密码按钮
   */
  @FXML
  private Button resetRandomPassword;

  /**
   * 设置个人密码按钮
   */
  @FXML
  private Button setPersonalPassword;

  /**
   * 本地id
   */
  @FXML
  private TextField localIdText;

  /**
   * 本地随机密码
   */
  @FXML
  private TextField passwordText;

  /**
   * 连接方式选择框
   */
  @FXML
  private ComboBox<String> connectionSelect;

  /**
   * 远端id or 远端ip 输入框
   */
  @FXML
  private TextField remoteIdText;

  @FXML
  private Label symbol;

  /**
   * 远端端口输入框
   */
  @FXML
  private TextField remotePortText;

  @Autowired
  private PersonalSettingsController personalSettingsController;

  @Autowired
  private DirectClient directClient;

  private Stage settingStage;

  private GlobalConfiguration globalConfiguration = GlobalConfiguration.getInstance();

  private ResourceBundle resources;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.resources = resources;
    // 设置id显示格式
    localIdText.setTextFormatter(new TextFormatter<>(new RemoteIdConverter()));
    // 设置id不可变
    localIdText.textProperty().addListener((observable, oldValue, newValue) -> {
      newValue = StringUtils.replace(newValue, " ", "");
      if (!StringUtils.equals("", newValue)) {
        localIdText.setText("");
      }
    });
    // 设置密码不可变
    passwordText.textProperty().addListener((observable, oldValue, newValue) -> {
      newValue = StringUtils.replace(newValue, " ", "");
      if (!StringUtils.equals(TemVariables.getRandomPassword(), newValue)) {
        passwordText.setText(TemVariables.getRandomPassword());
      }
    });
    // 设置下拉框选择事件
    connectionSelect.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (StringUtils.equals(REMOTE_ID, newValue)) {
        symbol.setVisible(false);
        symbol.setManaged(false);
        remotePortText.setVisible(false);
        remotePortText.setManaged(false);
      } else if (StringUtils.equals(REMOTE_IP_PORT, newValue)) {
        symbol.setVisible(true);
        symbol.setManaged(true);
        remotePortText.setVisible(true);
        remotePortText.setManaged(true);
      }
    });
    // 设置默认值
    connectionSelect.getSelectionModel().select(0);

    // 设置随机密码
    this.resetRandomPassword.fire();
  }

  @FXML
  public void setPersonalPassword(ActionEvent actionEvent) throws IOException {
    if (null == settingStage) {
      settingStage = FxUtils.createFxmlView(PersonalSettings.class, Modality.WINDOW_MODAL);
      personalSettingsController.setStage(settingStage);
    }
    if (!settingStage.isShowing()) {
      personalSettingsController.setPasswordText(globalConfiguration.getPersonalPassword());
      personalSettingsController.initFocus();
      settingStage.show();
    }
  }

  @FXML
  public void resetRandomPassword(ActionEvent actionEvent) {
    TemVariables.setRandomPassword(RandomStringUtils.randomAlphanumeric(6));
    passwordText.setText(TemVariables.getRandomPassword());
  }

  @FXML
  public void connection(ActionEvent actionEvent) {
    if (StringUtils.equals(connectionSelect.getSelectionModel().getSelectedItem(), "远端id")) {
      String remoteId = remoteIdText.getText();
      if (StringUtils.isBlank(remoteId)) {
        new DialogBuilder().setTitle("输入错误").setMessage("请输入远端id").setPositiveBtn("确定").setNegativeBtn("取消").create();
        return;
      }
    } else if (StringUtils.equals(connectionSelect.getSelectionModel().getSelectedItem(), "远端ip:端口")) {
      String host = remoteIdText.getText();
      Integer port = StringUtils.isBlank(remotePortText.getText()) ? null : NumberUtils.createInteger(remotePortText.getText());
      if (StringUtils.isBlank(host)) {
        new DialogBuilder().setTitle("输入错误").setMessage("请输入ip地址").setPositiveBtn("确定").setNegativeBtn("取消").create();
        return;
      }
      if (null == port) {
        new DialogBuilder().setTitle("输入错误").setMessage("请输入端口号").setPositiveBtn("确定").setNegativeBtn("取消").create();
        return;
      }
      directClient.connect(host, port);
    }
  }

  public static void main(String[] args) {
    System.out.println(RegExUtils.replacePattern("231231231312", "(?<=\\d)(?=(\\d{3})+$)", "\\ "));
    System.out.println(RandomStringUtils.randomAlphanumeric(6));
  }
}
