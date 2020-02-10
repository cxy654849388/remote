package com.chm.remote.client.process.client;

import com.chm.remote.client.controller.ConsoleController;
import com.chm.remote.common.process.CommandProcess;
import com.chm.remote.common.transfer.Transfer;
import com.chm.remote.common.utils.LogUtil;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.bytedeco.javacv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * @author caihongming
 * @version v1.0
 * @title ScreenDirectClientProcess
 * @package com.chm.remote.client.process.client
 * @since 2019-12-06
 * description 控制端接收受控端屏幕截图
 **/
@Service("frameDirectClientProcess")
public class FrameDirectClientProcess implements CommandProcess {

  @Autowired
  private ConsoleController consoleController;

  private final Java2DFrameConverter converter = new Java2DFrameConverter();

  @Override
  public Transfer process(ChannelHandlerContext ctx, Transfer transfer) {
    byte[] data = (byte[]) transfer.getValue();
    try {
      processFrame(ctx, data);
    } catch (FrameGrabber.Exception e) {
      LogUtil.logError("处理视频帧异常", e);
    }
    return null;
  }

  /**
   * 处理视频帧
   */
  private void processFrame(ChannelHandlerContext ctx, byte[] data) throws FrameGrabber.Exception {
    InputStream in = new ByteArrayInputStream(data);

    final FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(in);
    grabber.start();
    Frame frame;
    while (null != (frame = grabber.grab())) {
      if (frame.image != null) {
        final Image image = SwingFXUtils.toFXImage(converter.convert(frame), null);
        Platform.runLater(() -> consoleController.setImage(ctx, image));
      }
      if (frame.samples != null) {
        final ShortBuffer channelSamplesShortBuffer = (ShortBuffer) frame.samples[0];
        channelSamplesShortBuffer.rewind();

        final ByteBuffer outBuffer = ByteBuffer.allocate(channelSamplesShortBuffer.capacity() * 2);

        for (int i = 0; i < channelSamplesShortBuffer.capacity(); i++) {
          short val = channelSamplesShortBuffer.get(i);
          outBuffer.putShort(val);
        }

        /**
         * We need this because soundLine.write ignores
         * interruptions during writing.
         */
       /* try {
          executor.submit((Runnable) () -> {
            soundLine.write(outBuffer.array(), 0, outBuffer.capacity());
            outBuffer.clear();
          }).get();
        } catch (InterruptedException interruptedException) {
          Thread.currentThread().interrupt();
        }*/
      }
    }

  }
}
