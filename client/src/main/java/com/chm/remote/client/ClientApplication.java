package com.chm.remote.client;

import com.chm.remote.client.form.ClientDesktop;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author caihongming
 * @version v1.0
 * @title ClientApplication
 * @package com.chm.remote.client
 * @since 2019-10-31
 * description 客户端启动入口
 **/

@SpringBootApplication
public class ClientApplication extends AbstractJavaFxApplicationSupport {

  public static void main(String[] args) {
    launch(ClientApplication.class, ClientDesktop.class, args);
  }

  @Override
  public void start(final Stage stage) throws Exception {
    super.start(stage);
    stage.setTitle("Remote");
  }

  @Override
  public Collection<Image> loadDefaultIcons() {
    return Arrays.asList(new Image(getClass().getResource("/icons/icon_16x16.jpg").toExternalForm()),
            new Image(getClass().getResource("/icons/icon_24x24.jpg").toExternalForm()),
            new Image(getClass().getResource("/icons/icon_36x36.jpg").toExternalForm()),
            new Image(getClass().getResource("/icons/icon_42x42.jpg").toExternalForm()),
            new Image(getClass().getResource("/icons/icon_64x64.jpg").toExternalForm()),
            new Image(getClass().getResource("/icons/icon.jpg").toExternalForm()));
  }

}
