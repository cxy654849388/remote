package com.chm.remote.client.controller;

import com.chm.remote.client.control.CustomTab;
import com.chm.remote.common.transfer.Transfer;
import com.chm.remote.common.utils.ImageUtil;
import com.chm.remote.common.utils.RobotUtil;
import com.google.common.collect.Maps;
import de.felixroske.jfxsupport.FXMLController;
import io.netty.channel.ChannelHandlerContext;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;

import java.awt.*;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author caihongming
 * @version v1.0
 * @title ConsoleController
 * @package com.chm.remote.client.controller
 * @since 2019-12-06
 * description
 **/
@FXMLController
public class ConsoleController implements Initializable {

  private static final Map<ChannelHandlerContext, ImageView> IMAGE_VIEW_MAP = Maps.newConcurrentMap();

  private Stage stage;

  @FXML
  private BorderPane rootPane;

  @FXML
  private TabPane tabPane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Dimension screenSize = RobotUtil.getScreenSize();
    rootPane.setPrefSize(screenSize.getWidth() * 0.6, screenSize.getHeight() * 0.8);
    tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
    tabPane.getTabs().addListener((ListChangeListener<Tab>) c -> {
      while (c.next()) {
        if (CollectionUtils.isEmpty(tabPane.getTabs())) {
          stage.close();
        }
      }
    });
  }

  @FXML
  public void mouseMoved(MouseEvent mouseEvent) {
    double width = mouseEvent.getSceneX() / tabPane.getWidth();
    double height = mouseEvent.getSceneY() / tabPane.getHeight();
  }

  public void addTab(ChannelHandlerContext ctx, Transfer transfer) {
    // 设置Tab
    CustomTab tab = new CustomTab();
    tab.setText(transfer.getSource());
    ScrollPane scrollPane = new ScrollPane();
    ImageView imageView = new ImageView();
    scrollPane.setContent(imageView);
    tab.setContent(scrollPane);
    tabPane.getTabs().add(tab);

    // 监听鼠标移动
    imageView.setOnMouseMoved(event -> {
      double width = event.getSceneX() / tabPane.getWidth();
      double height = event.getSceneY() / tabPane.getHeight();
    });

    // 监听鼠标点击
    imageView.setOnMouseClicked(event -> {
      //event.getButton();
    });

    // 设置Tab关闭事件
    tab.setOnCloseRequest(event -> {
      ctx.close();
    });

    IMAGE_VIEW_MAP.put(ctx, imageView);
  }

  public void setImage(ChannelHandlerContext ctx,Image image) {
    ImageView imageView = IMAGE_VIEW_MAP.get(ctx);
    imageView.setImage(image);
  }

  public void closeAllTab() {
    tabPane.getTabs().forEach(tab -> tab.getOnCloseRequest().handle(null));
    tabPane.getTabs().clear();
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }
}
