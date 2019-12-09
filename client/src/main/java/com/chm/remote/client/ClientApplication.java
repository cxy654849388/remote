package com.chm.remote.client;

import com.chm.remote.client.config.GlobalConfiguration;
import com.chm.remote.client.form.ClientDesktop;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.SplashScreen;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.CommandLineRunner;
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

@SpringBootApplication(scanBasePackages = {"com.chm.remote.common", "com.chm.remote.client"})
public class ClientApplication extends AbstractJavaFxApplicationSupport implements CommandLineRunner {

  public static void main(String[] args) {
    launch(ClientApplication.class, ClientDesktop.class, new CustomSplash(), args);
  }

  @Override
  public void start(final Stage stage) throws Exception {
    super.start(stage);
    stage.setOnCloseRequest(event -> Platform.exit());
  }

  @Override
  public void run(String... args) throws Exception {
    GlobalConfiguration.init();
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

  private static class CustomSplash extends SplashScreen {
    /**
     * Use your own splash image instead of the default one
     *
     * @return "/splash/javafx.png"
     */
    @Override
    public String getImagePath() {
      return "/icons/icon.jpg";
    }

    /**
     * Customize if the splash screen should be visible at all
     *
     * @return true by default
     */
    @Override
    public boolean visible() {
      return super.visible();
    }
  }
}
