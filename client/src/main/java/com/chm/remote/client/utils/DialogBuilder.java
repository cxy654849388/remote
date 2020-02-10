package com.chm.remote.client.utils;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.lang.Nullable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * @author caihongming
 * @version v1.0
 * @title DialogBuilder
 * @package com.chm.remote.client.utils
 * @since 2019-12-02
 * description 对话框构造器
 **/
public final class DialogBuilder {

  private String title;

  private String message;

  private JFXButton negativeBtn = null;

  private JFXButton positiveBtn = null;

  private Window window;

  /**
   * 否定按钮文字颜色，默认灰色
   */
  private Paint negativeBtnPaint = Paint.valueOf("#747474");

  private Paint positiveBtnPaint = Paint.valueOf("#0099ff");

  private Hyperlink hyperlink = null;

  private TextField textField = null;

  private JFXAlert<String> alert;

  private OnInputListener onInputListener = null;

  /**
   * 构造方法
   */
  public DialogBuilder() {
    window = AbstractJavaFxApplicationSupport.getStage();
  }

  /**
   * 构造方法
   */
  public DialogBuilder(Stage stage) {
    window = stage;
  }

  public DialogBuilder setTitle(String title) {
    this.title = title;
    return this;
  }

  public DialogBuilder setMessage(String message) {
    this.message = message;
    return this;
  }

  public DialogBuilder setNegativeBtn(String negativeBtnText) {
    return setNegativeBtn(negativeBtnText, null, null);
  }

  /**
   * 设置否定按钮文字和文字颜色
   *
   * @param negativeBtnText 文字
   * @param color           文字颜色 十六进制 #fafafa
   * @return
   */
  public DialogBuilder setNegativeBtn(String negativeBtnText, String color) {
    return setNegativeBtn(negativeBtnText, null, color);
  }

  /**
   * 设置按钮文字和按钮文字颜色，按钮监听器和
   *
   * @param negativeBtnText
   * @param negativeBtnOnclickListener
   * @param color                      文字颜色 十六进制 #fafafa
   * @return
   */
  public DialogBuilder setNegativeBtn(String negativeBtnText, @Nullable OnClickListener negativeBtnOnclickListener, String color) {
    if (color != null) {
      this.negativeBtnPaint = Paint.valueOf(color);
    }
    return setNegativeBtn(negativeBtnText, negativeBtnOnclickListener);
  }


  /**
   * 设置按钮文字和点击监听器
   *
   * @param negativeBtnText            按钮文字
   * @param negativeBtnOnclickListener 点击监听器
   * @return
   */
  public DialogBuilder setNegativeBtn(String negativeBtnText, @Nullable OnClickListener negativeBtnOnclickListener) {

    negativeBtn = new JFXButton(negativeBtnText);
    negativeBtn.setCancelButton(true);
    negativeBtn.setTextFill(negativeBtnPaint);
    negativeBtn.setButtonType(JFXButton.ButtonType.FLAT);
    negativeBtn.setOnAction(addEvent -> {
      alert.hideWithAnimation();
      if (negativeBtnOnclickListener != null) {
        negativeBtnOnclickListener.onClick();
      }
    });
    return this;
  }

  /**
   * 设置按钮文字和颜色
   *
   * @param positiveBtnText 文字
   * @return
   */
  public DialogBuilder setPositiveBtn(String positiveBtnText) {
    return setPositiveBtn(positiveBtnText, null, null);
  }

  /**
   * 设置按钮文字和颜色
   *
   * @param positiveBtnText 文字
   * @param color           颜色 十六进制 #fafafa
   * @return
   */
  public DialogBuilder setPositiveBtn(String positiveBtnText, String color) {
    return setPositiveBtn(positiveBtnText, null, color);
  }

  /**
   * 设置按钮文字，颜色和点击监听器
   *
   * @param positiveBtnText            文字
   * @param positiveBtnOnclickListener 点击监听器
   * @param color                      颜色 十六进制 #fafafa
   * @return
   */
  public DialogBuilder setPositiveBtn(String positiveBtnText, @Nullable OnClickListener positiveBtnOnclickListener, String color) {
    if (color != null) {
      this.negativeBtnPaint = Paint.valueOf(color);
    }
    return setPositiveBtn(positiveBtnText, positiveBtnOnclickListener);
  }

  /**
   * 设置按钮文字和监听器
   *
   * @param positiveBtnText            文字
   * @param positiveBtnOnclickListener 点击监听器
   * @return
   */
  public DialogBuilder setPositiveBtn(String positiveBtnText, @Nullable OnClickListener positiveBtnOnclickListener) {
    positiveBtn = new JFXButton(positiveBtnText);
    positiveBtn.setDefaultButton(true);
    positiveBtn.setTextFill(positiveBtnPaint);
    positiveBtn.setOnAction(closeEvent -> {
      alert.hideWithAnimation();
      if (positiveBtnOnclickListener != null) {
        //回调onClick方法
        positiveBtnOnclickListener.onClick();
      }
    });
    return this;
  }

  public DialogBuilder setHyperLink(String text) {
    hyperlink = new Hyperlink(text);
    hyperlink.setBorder(Border.EMPTY);
    hyperlink.setOnMouseClicked(event -> {
      if (text.contains("www") || text.contains("com") || text.contains(".")) {
        try {
          Desktop.getDesktop().browse(new URI(text));
        } catch (IOException | URISyntaxException e) {
          e.printStackTrace();
        }
      } else if (text.contains(File.separator)) {
        try {
          Desktop.getDesktop().open(new File(text));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    return this;
  }

  public DialogBuilder setTextFieldText(OnInputListener onInputListener) {
    this.textField = new TextField();
    this.onInputListener = onInputListener;
    return this;
  }

  /**
   * 创建对话框并显示
   *
   * @return JFXAlert<String>
   */
  public JFXAlert<String> create() {
    alert = new JFXAlert<>((Stage) (window));
    alert.initModality(Modality.APPLICATION_MODAL);
    alert.setOverlayClose(false);

    JFXDialogLayout layout = new JFXDialogLayout();
    layout.setHeading(new Label(title));
    //添加hyperlink超链接文本
    if (hyperlink != null) {
      layout.setBody(new HBox(new Label(this.message), hyperlink));
    } else if (textField != null) {
      layout.setBody(new VBox(new Label(this.message), textField));
      positiveBtn.setOnAction(event -> {
        alert.setResult(textField.getText());
        alert.hideWithAnimation();
      });
    } else {
      layout.setBody(new VBox(new Label(this.message)));
    }
    //添加确定和取消按钮
    if (negativeBtn != null && positiveBtn != null) {
      layout.setActions(negativeBtn, positiveBtn);
    } else {
      if (negativeBtn != null) {
        layout.setActions(negativeBtn);
      } else if (positiveBtn != null) {
        layout.setActions(positiveBtn);
      }
    }

    alert.setContent(layout);
    alert.setResultConverter(button -> null);
    Optional<String> input = alert.showAndWait();
    //不为空，则回调接口
    input.ifPresent(s -> onInputListener.onGetText(s));

    return alert;
  }

  public interface OnClickListener {

    /**
     * 点击处理方法
     */
    void onClick();
  }

  public interface OnInputListener {

    /**
     * 获取输入内容
     *
     * @param result
     */
    void onGetText(String result);
  }
}
