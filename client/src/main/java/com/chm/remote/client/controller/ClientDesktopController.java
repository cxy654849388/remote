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
package com.chm.remote.client.controller;

import com.chm.remote.client.textformatter.RemoteIdConverter;
import com.jfoenix.controls.JFXNodesList;
import de.felixroske.jfxsupport.FXMLController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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

  @FXML
  private Button btn;

  @FXML
  private Label remoteIdLabel;

  @FXML
  private TextField remoteIdText;

  @FXML
  private Label passwordLabel;

  @FXML
  private TextField passwordText;

  private ResourceBundle resources;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.resources = resources;
    remoteIdText.setTextFormatter(new TextFormatter<>(new RemoteIdConverter()));
    btn.getStyleClass().add("button-raised");
    remoteIdLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.USER, "25px"));
    remoteIdText.setStyle("-jfx-focus-color: transparent;-jfx-unfocus-color: transparent;");
    remoteIdText.textProperty().addListener((observable, oldValue, newValue) -> {
      newValue = StringUtils.replace(newValue, " ", "");
      if (!StringUtils.equals("123456789", newValue)) {
        remoteIdText.setText("123456789");
      }
    });
    passwordLabel.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.LOCK, "30px"));
    passwordText.setStyle("-jfx-focus-color: transparent;-jfx-unfocus-color: transparent;");
    passwordText.textProperty().addListener((observable, oldValue, newValue) -> {
      newValue = StringUtils.replace(newValue, " ", "");
      if (!StringUtils.equals("123456789", newValue)) {
        passwordText.setText("123456789");
      }
    });
  }

  @FXML
  public void btnClick(ActionEvent actionEvent) {
    remoteIdText.setText("123456789");
  }

  public static void main(String[] args) {
    System.out.println(RegExUtils.replacePattern("231231231312", "(?<=\\d)(?=(\\d{3})+$)", "\\ "));
  }
}
