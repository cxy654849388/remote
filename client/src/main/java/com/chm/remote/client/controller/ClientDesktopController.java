package com.chm.remote.client.controller;

import com.chm.remote.client.textformatter.RemoteIdConverter;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author caihongming
 * @version v1.0
 * @title ClientDesktopController
 * @package com.chm.remote.client.form
 * @since 2019-11-05
 * description
 **/

@FXMLController
public class ClientDesktopController implements Initializable {

  private static final String REMOTE_ID = "远端id";

  private static final String REMOTE_IP_PORT = "远端ip:端口";

  @FXML
  private Button connectionBtn;

  @FXML
  private Button resetRandomPassword;

  @FXML
  private Button setPersonalPassword;

  @FXML
  private TextField localIdText;

  @FXML
  private TextField remoteIdText;

  @FXML
  private TextField passwordText;

  @FXML
  private ComboBox<String> connectionSelect;

  @FXML
  private Label symbol;

  @FXML
  private TextField remotePortText;


  private ResourceBundle resources;

  private String randomPassword;

  private String personalPassword;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.resources = resources;
    // 设置id显示格式
    localIdText.setTextFormatter(new TextFormatter<>(new RemoteIdConverter()));
    // 设置id不可变
    localIdText.textProperty().addListener((observable, oldValue, newValue) -> {
      newValue = StringUtils.replace(newValue, " ", "");
      if (!StringUtils.equals(this.personalPassword, newValue)) {
        localIdText.setText(this.personalPassword);
      }
    });
    // 设置密码不可变
    passwordText.textProperty().addListener((observable, oldValue, newValue) -> {
      newValue = StringUtils.replace(newValue, " ", "");
      if (!StringUtils.equals(this.randomPassword, newValue)) {
        passwordText.setText(this.randomPassword);
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
  public void setPersonalPassword(ActionEvent actionEvent) {
    // TODO 设置个人密码
  }

  @FXML
  public void resetRandomPassword(ActionEvent actionEvent) {
    this.randomPassword = RandomStringUtils.randomAlphanumeric(6);
    passwordText.setText(this.randomPassword);
  }

  @FXML
  public void connection(ActionEvent actionEvent) {
    // TODO 连接方式
  }

  public static void main(String[] args) {
    System.out.println(RegExUtils.replacePattern("231231231312", "(?<=\\d)(?=(\\d{3})+$)", "\\ "));
    System.out.println(RandomStringUtils.randomAlphanumeric(6));
  }
}
