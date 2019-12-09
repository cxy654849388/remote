package com.chm.remote.client.process.client;

import com.chm.remote.client.control.CustomTab;
import com.chm.remote.client.controller.ConsoleController;
import com.chm.remote.client.form.Console;
import com.chm.remote.client.form.PersonalSettings;
import com.chm.remote.client.utils.DialogBuilder;
import com.chm.remote.client.utils.FxUtils;
import com.chm.remote.common.process.CommandProcess;
import com.chm.remote.common.transfer.Transfer;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author caihongming
 * @version v1.0
 * @title TokenDirectClientProcess
 * @package com.chm.remote.client.process.client
 * @since 2019-12-05
 * description 访问密码命令处理客户端实现
 **/
@Service("tokenDirectClientProcess")
public class TokenDirectClientProcess implements CommandProcess {

  private Stage consoleStage;

  @Autowired
  private ConsoleController consoleController;

  @Override
  public Transfer process(ChannelHandlerContext ctx, Transfer transfer) {
    boolean result = (boolean) transfer.getValue();
    if (result) {
      Platform.runLater(() -> {
        if (null == consoleStage) {
          consoleStage = FxUtils.createFxmlView(Console.class, null);
          consoleStage.setOnCloseRequest(event -> consoleController.closeAllTab());
          consoleController.setStage(consoleStage);
        }
        if (!consoleStage.isShowing()) {
          consoleStage.show();
        }
        consoleController.addTab(ctx, transfer);

      });
    } else {
      Platform.runLater(() -> new DialogBuilder().setTitle("连接失败").setMessage("密码错误").setPositiveBtn("确定").setNegativeBtn("取消").create());
      ctx.close();
    }
    return null;
  }
}
