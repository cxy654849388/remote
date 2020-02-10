package com.chm.remote.client.utils;

import com.chm.remote.client.task.AsyncTask;
import com.chm.remote.common.utils.LogUtil;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.*;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author caihongming
 * @version v1.0
 * @title FFmpegUtil
 * @package com.chm.remote.client.utils
 * @since 2019-12-12
 * description FFmpeg工具类
 **/
public final class FfmpegUtil {

  /**
   * 线程池 screenTimer 录制视频
   */
  private static ScheduledThreadPoolExecutor screenTimer;

  /**
   * 线程池 exec 录制音频
   */
  private static ScheduledThreadPoolExecutor exec;

  private static FFmpegFrameGrabber grabber;

  /**
   * 视频类 FFmpegFrameRecorder
   */
  private static FFmpegFrameRecorder recorder;

  private static FFmpegFrameFilter cropFilter;

  private static TargetDataLine line;

  private static AudioFormat audioFormat;

  private static DataLine.Info dataLineInfo;

  private static ByteArrayOutputStream recorderOutput = new ByteArrayOutputStream();

  private static Rectangle rectangleFullScreen;

  private static int grabberPixelFormat;

  private static int lowerRightX;

  private static int lowerRightY;

  private static int positiveTopLeftX;

  private static int positiveTopLeftY;

  private static int fps;

  private static int crf = 28;

  private static int maxrateMbits;

  private static int lastWidth;

  private static int lastHeight;

  private static int lastX;

  private static int lastY;

  private static boolean isHaveDevice = true;
  private static boolean started = false;

  private static void init() {
    MonitorInfo.VirtualScreenBoundingBox boundingBox = MonitorInfo.getVirtualScreenBoundingBox();
    rectangleFullScreen = new Rectangle(
            boundingBox.topLeftX, boundingBox.topLeftY,
            boundingBox.width, boundingBox.height
    );
    positiveTopLeftX = -boundingBox.topLeftX;
    positiveTopLeftY = -boundingBox.topLeftY;
    lowerRightX = boundingBox.lowerRightX;
    lowerRightY = boundingBox.lowerRightY;
    validateAndFixRoi(rectangleFullScreen);
    String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("linux")) {
      // https://www.ffmpeg.org/ffmpeg-devices.html#x11grab
      String display = System.getenv("DISPLAY");
      if (display == null) {
        display = ":0";
      }
      grabber = new FFmpegFrameGrabber(display + "+" + rectangleFullScreen.getX() + "," + rectangleFullScreen.getY());
      grabber.setFormat("x11grab");
    } else if (os.contains("mac")) {
      // https://www.ffmpeg.org/ffmpeg-devices.html#avfoundation
      grabber = new FFmpegFrameGrabber("default");
      grabber.setFormat("avfoundation");
      grabber.setOption("-capture_cursor", "1");
    } else {
      // https://www.ffmpeg.org/ffmpeg-devices.html#gdigrab
      grabber = new FFmpegFrameGrabber("desktop");
      grabber.setFormat("gdigrab");
      grabber.setOption("offset_x", String.valueOf(rectangleFullScreen.getX()));
      grabber.setOption("offset_y", String.valueOf(rectangleFullScreen.getY()));
    }
    grabber.setImageWidth((int) rectangleFullScreen.getWidth());
    grabber.setImageHeight((int) rectangleFullScreen.getHeight());
    grabberPixelFormat = grabber.getPixelFormat();
    cropFilter = new FFmpegFrameFilter(null, rectangleFullScreen.width, rectangleFullScreen.height);
  }

  private static void setupNewRecorder(Rectangle roi) {
    recorder = new FFmpegFrameRecorder(recorderOutput, roi.width, roi.height);
    recorder.setInterleaved(true);

    recorder.setVideoOption("tune", "zerolatency");
    recorder.setVideoOption("preset", "ultrafast");
    recorder.setVideoOption("crf", String.valueOf(crf));
    // 2000 kb/s, reasonable "sane" area for 720
    recorder.setVideoBitrate(2000000);
    recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
    recorder.setFrameRate(fps);
    recorder.setFormat("h264");
    recorder.setVideoOption("profile", "baseline");
    recorder.setVideoOption("g", String.valueOf(fps * 10));
    recorder.setVideoOption("maxrate", String.valueOf(1024 * 1024 * maxrateMbits));
    recorder.setVideoOption("bufsize", String.valueOf(1024 * 1024 * maxrateMbits));

    /* recorder.setVideoOption("g", String.valueOf(fps * 10));*/
  /*  // yuv420p
    recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);*/
    // 16 Kbps
    recorder.setAudioBitrate(16000);
    // 44.1MHZ
    recorder.setSampleRate(44100);
    // 最好的音质
    recorder.setAudioQuality(0);
    recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
    // 音频比特率不可变
    recorder.setAudioOption("crf", "0");
    recorder.setAudioChannels(2);
  }

  private static void setupNewRecorder() {
    setupNewRecorder(rectangleFullScreen);
  }

  public static void setFps(int fps) {
    FfmpegUtil.fps = fps;
  }

  public static void setCrf(int crf) {
    FfmpegUtil.crf = crf;
  }

  public static void setMaxrateMbits(int maxrateMbits) {
    FfmpegUtil.maxrateMbits = maxrateMbits;
  }

  public static boolean isStarted() {
    return FfmpegUtil.started;
  }

  /**
   * 开始录制
   */
  public static void start() {
    try {
      if (!started) {
        grabber.setFrameRate(fps);
        grabber.start();
        setupNewRecorder();
        recorder.start();
        started = true;
      }
    } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
      LogUtil.logError(e.getMessage(), e);
      stop();
    }

    // 如果有录音设备则启动录音线程
    if (isHaveDevice) {
      // TODO Auto-generated method stub
      new Thread(FfmpegUtil::capture).start();
    }
    // 录屏
    screenTimer = new ScheduledThreadPoolExecutor(1);
    screenTimer.scheduleAtFixedRate(() -> AsyncTask.offerFrame(getFrameFullscreen()), 1000 / fps, 1000 / fps, TimeUnit.MILLISECONDS);

  }

  public static synchronized void stop() {
    if (null != screenTimer) {
      screenTimer.shutdownNow();
    }
    try {
      grabber.stop();
      recorder.stop();
      recorder.release();
      recorder.close();
      cropFilter.stop();
      screenTimer = null;
      // screenCapture = null;
      if (isHaveDevice) {
        if (null != exec) {
          exec.shutdownNow();
        }
        if (null != line) {
          line.stop();
          line.close();
        }
        dataLineInfo = null;
        audioFormat = null;
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (started) {
      recorder = null;
      started = false;
      lastWidth = 0;
      lastHeight = 0;
      lastX = 0;
      lastY = 0;
      recorderOutput.reset();
    }
  }

  private static boolean roiChanged(Rectangle roi) {
    return resolutionChanged(roi) || lastX != roi.x || lastY != roi.y;
  }

  private static boolean resolutionChanged(Rectangle roi) {
    return lastWidth != roi.width || lastHeight != roi.height;
  }

  private static void validateAndFixRoi(Rectangle roi) {
    // x264 needs even size
    if (roi.x + roi.width > lowerRightX) {
      roi.width = lowerRightX - roi.x;
    }
    if (roi.y + roi.height > lowerRightY) {
      roi.height = lowerRightY - roi.y;
    }
    if (roi.width % 2 == 1) {
      roi.width--;
    }
    if (roi.height % 2 == 1) {
      roi.height--;
    }
  }

  public static byte[] getFrameFullscreen() {
    return getFrame(rectangleFullScreen);
  }

  public static byte[] getFrameRoi(Rectangle roi) {
    validateAndFixRoi(roi);
    return getFrame(roi);
  }

  private static synchronized byte[] getFrame(Rectangle roi) {
    try {
      Frame frame;
      while ((frame = grabber.grabImage()) == null && started) {
        // apparently on windows lock screen grabImage() returns null, so we wait and try every second
        // or until !started anymore
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
      }
      if (!roi.equals(rectangleFullScreen)) {
        if (roiChanged(roi)) {
          cropFilter.setFilters("crop=w=" + roi.width + ":h=" + roi.height
                  + ":x=" + (roi.x + positiveTopLeftX) + ":y=" + (roi.y + positiveTopLeftY)
          );
          cropFilter.restart();
        }
        cropFilter.push(frame);
        frame = cropFilter.pull();
      }
      lastWidth = roi.width;
      lastHeight = roi.height;
      lastX = roi.x;
      lastY = roi.y;

      recorder.record(frame, grabberPixelFormat);
      return recorderOutput.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      recorderOutput.reset();
    }
  }

  /**
   * 抓取声音
   */
  public static void capture() {
    audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);
    dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
    try {
      line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
    } catch (LineUnavailableException e) {
      LogUtil.logError("录音线程启动失败", e);
    }
    try {
      line.open(audioFormat);
    } catch (LineUnavailableException e) {
      LogUtil.logError("录音线程启动失败", e);
    }
    line.start();

    final int sampleRate = (int) audioFormat.getSampleRate();
    final int numChannels = audioFormat.getChannels();

    int audioBufferSize = sampleRate * numChannels;
    final byte[] audioBytes = new byte[audioBufferSize];

    exec = new ScheduledThreadPoolExecutor(1);
    exec.scheduleAtFixedRate(() -> {
      try {
        int nBytesRead = line.read(audioBytes, 0, line.available());
        int nSamplesRead = nBytesRead / 2;
        short[] samples = new short[nSamplesRead];
        // Let's wrap our short[] into a ShortBuffer and
        // pass it to recordSamples
        ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
        ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);
        // recorder is instance of
        // org.bytedeco.javacv.FFmpegFrameRecorder
        recorder.recordSamples(sampleRate, numChannels, sBuff);
        AsyncTask.offerFrame(recorderOutput.toByteArray());
      } catch (FrameRecorder.Exception e) {
        LogUtil.logError("音频录制失败", e);
      } finally {
        recorderOutput.reset();
      }
    }, 1000 / fps, 1000 / fps, TimeUnit.MILLISECONDS);
  }

  static {
    init();
  }

}
