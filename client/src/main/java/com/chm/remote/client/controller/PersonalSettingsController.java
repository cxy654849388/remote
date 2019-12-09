package com.chm.remote.client.controller;

import com.chm.remote.client.config.GlobalConfiguration;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author caihongming
 * @version v1.0
 * @title PersonalSettingsController
 * @package com.chm.remote.client.controller
 * @since 2019-11-26
 * description 个人设置页控制器
 **/
@FXMLController
public class PersonalSettingsController implements Initializable {

  @FXML
  private BorderPane rootPane;

  @FXML
  private PasswordField passwordText;

  private Stage stage;

  private GlobalConfiguration globalConfiguration = GlobalConfiguration.getInstance();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  @FXML
  public void okBtn(ActionEvent actionEvent) {
    if (!StringUtils.equals(passwordText.getText(), globalConfiguration.getPersonalPassword())) {
      globalConfiguration.setPersonalPasswordAndSave(passwordText.getText());
    }
    this.stage.close();
  }

  @FXML
  public void cancelBtn(ActionEvent actionEvent) {
    this.stage.close();
  }

  public void initFocus() {
    rootPane.requestFocus();
  }

  public void setPasswordText(String passwordText) {
    this.passwordText.setText(passwordText);
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

}
