package com.chm.remote.client.utils;

import com.chm.remote.common.utils.SpringUtil;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author caihongming
 * @version v1.0
 * @title FxUtils
 * @package com.chm.remote.client.utils
 * @since 2019-11-21
 * description
 **/
public final class FxUtils {

  public static Stage createFxmlView(final Class<? extends AbstractFxmlView> window, final Modality mode) {
    final AbstractFxmlView view = SpringUtil.getBean(window);
    Stage newStage = new Stage();

    Scene newScene;
    if (view.getView().getScene() != null) {
      // This view was already shown so
      // we have a scene for it and use this one.
      newScene = view.getView().getScene();
    } else {
      newScene = new Scene(view.getView());
    }

    newStage.setScene(newScene);
    if (null != mode) {
      newStage.initModality(mode);
    }
    newStage.initOwner(AbstractJavaFxApplicationSupport.getStage());
    newStage.setTitle(getDefaultTitle(view));
    newStage.initStyle(getDefaultStyle(view));

    return newStage;
  }

  private static String getDefaultTitle(AbstractFxmlView view) {
    FXMLView annotation = getFxmlAnnotation(view.getClass());
    return annotation.title();
  }

  private static StageStyle getDefaultStyle(AbstractFxmlView view) {
    FXMLView annotation = getFxmlAnnotation(view.getClass());
    final String style = annotation.stageStyle();
    return StageStyle.valueOf(style.toUpperCase());
  }

  private static FXMLView getFxmlAnnotation(Class<? extends AbstractFxmlView> clazz) {
    return clazz.getAnnotation(FXMLView.class);
  }
}
