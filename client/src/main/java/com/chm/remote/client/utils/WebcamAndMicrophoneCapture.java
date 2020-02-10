package com.chm.remote.client.utils;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.swing.*;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FrameRecorder.Exception;

/**
 * @author caihongming
 * @version v1.0
 * @title WebcamAndMicrophoneCapture
 * @package com.chm.remote.client.utils
 * @since 2019-12-16
 * description
 **/
public class WebcamAndMicrophoneCapture {
  final private static int WEBCAM_DEVICE_INDEX = 1;
  final private static int AUDIO_DEVICE_INDEX = 4;

  final private static int FRAME_RATE = 30;
  final private static int GOP_LENGTH_IN_FRAMES = 60;

  private static ScheduledThreadPoolExecutor exec;

  private static long startTime = 0;
  private static long videoTS = 0;

  public static void main(String[] args) throws Exception, org.bytedeco.javacv.FrameGrabber.Exception {
    final int captureWidth = 1280;
    final int captureHeight = 720;

    // The available FrameGrabber classes include OpenCVFrameGrabber (opencv_videoio),
    // DC1394FrameGrabber, FlyCapture2FrameGrabber, OpenKinectFrameGrabber,
    // PS3EyeFrameGrabber, VideoInputFrameGrabber, and FFmpegFrameGrabber.
    final FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("desktop");
    grabber.setFormat("gdigrab");
    grabber.start();

    // org.bytedeco.javacv.FFmpegFrameRecorder.FFmpegFrameRecorder(String
    // filename, int imageWidth, int imageHeight, int audioChannels)
    // For each param, we're passing in...
    // filename = either a path to a local file we wish to create, or an
    // RTMP url to an FMS / Wowza server
    // imageWidth = width we specified for the grabber
    // imageHeight = height we specified for the grabber
    // audioChannels = 2, because we like stereo
    final FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
            "E:/demo.mp4",
            captureWidth, captureHeight);
    // 13
    recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
    recorder.setFormat("mp4");
    // recorder.setFormat("mov,mp4,m4a,3gp,3g2,mj2,h264,ogg,MPEG4");
    recorder.setSampleRate(44100);
    recorder.setFrameRate(FRAME_RATE);

    recorder.setVideoQuality(0);
    recorder.setVideoOption("crf", "23");
    // 2000 kb/s, 720P视频的合理比特率范围
    recorder.setVideoBitrate(1000000);
    /**
     * 权衡quality(视频质量)和encode speed(编码速度) values(值)： ultrafast(终极快),superfast(超级快),
     * veryfast(非常快), faster(很快), fast(快), medium(中等), slow(慢), slower(很慢),
     * veryslow(非常慢)
     * ultrafast(终极快)提供最少的压缩（低编码器CPU）和最大的视频流大小；而veryslow(非常慢)提供最佳的压缩（高编码器CPU）的同时降低视频流的大小
     * 参考：https://trac.ffmpeg.org/wiki/Encode/H.264 官方原文参考：-preset ultrafast as the
     * name implies provides for the fastest possible encoding. If some tradeoff
     * between quality and encode speed, go for the speed. This might be needed if
     * you are going to be transcoding multiple streams on one machine.
     */
    recorder.setVideoOption("preset", "slow");
    // yuv420p
    recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
    recorder.setAudioChannels(2);
    recorder.setAudioOption("crf", "0");
    // Highest quality
    recorder.setAudioQuality(0);
    recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);

    // Jack 'n coke... do it...
    recorder.start();
    // Thread for audio capture, this could be in a nested private class if you prefer...
    new Thread(() -> caputre(recorder)).start();

    // A really nice hardware accelerated component for our preview...
    final CanvasFrame cFrame = new CanvasFrame("Capture Preview", CanvasFrame.getDefaultGamma() / grabber.getGamma());

    //cFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Frame capturedFrame = null;

    // While we are capturing...
    while (cFrame.isVisible() && (capturedFrame = grabber.grabImage()) != null) {
      // Show our frame in the preview
      cFrame.showImage(capturedFrame);

      // Let's define our start time...
      // This needs to be initialized as close to when we'll use it as
      // possible,
      // as the delta from assignment to computed time could be too high
      if (startTime == 0) {
        startTime = System.currentTimeMillis();
      }

      // Create timestamp for this frame
      videoTS = 1000 * (System.currentTimeMillis() - startTime);

      // Check for AV drift
      if (videoTS > recorder.getTimestamp()) {
        System.out.println(
                "Lip-flap correction: "
                        + videoTS + " : "
                        + recorder.getTimestamp() + " -> "
                        + (videoTS - recorder.getTimestamp()));

        // We tell the recorder to write this frame at this timestamp
        recorder.setTimestamp(videoTS);
      }

      // Send the frame to the org.bytedeco.javacv.FFmpegFrameRecorder
      recorder.record(capturedFrame);
    }
    if (null != exec) {
      exec.shutdownNow();
      try {
        exec.awaitTermination(500, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      exec = null;
    }
    cFrame.dispose();
    recorder.stop();
    recorder.release();
    recorder.close();
    grabber.stop();
  }

  /**
   * 抓取声音
   */
  public static void caputre(final FFmpegFrameRecorder recorder) {
    AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);
    System.out.println("准备开启音频！");
    // 通过AudioSystem获取本地音频混合器信息
    Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();
    // 通过AudioSystem获取本地音频混合器
    Mixer mixer = AudioSystem.getMixer(minfoSet[5]);
    // 通过设置好的音频编解码器获取数据线信息
    DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
    TargetDataLine line = null;
    try {
      line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
      line.open(audioFormat);
      line.start();
    } catch (LineUnavailableException e1) {
      // TODO Auto-generated catch block
      System.out.println("#################");
    }


    int sampleRate = (int) audioFormat.getSampleRate();
    int numChannels = audioFormat.getChannels();

    int audioBufferSize = sampleRate * numChannels;
    byte[] audioBytes = new byte[audioBufferSize];
    TargetDataLine finalLine = line;
    exec = new ScheduledThreadPoolExecutor(3);

    exec.scheduleAtFixedRate(new Runnable() {
      @Override
      public synchronized void run() {
        try {
          int nBytesRead = 0;
          while (nBytesRead == 0) {
            nBytesRead = finalLine.read(audioBytes, 0, finalLine.available());
          }
          int nSamplesRead = nBytesRead / 2;
          short[] samples = new short[nSamplesRead];

          // Let's wrap our short[] into a ShortBuffer and
          // pass it to recordSamples
          ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
          ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);

          // recorder is instance of
          // org.bytedeco.javacv.FFmpegFrameRecorder
          recorder.recordSamples(sampleRate, numChannels, sBuff);
          // System.gc();
        } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
          e.printStackTrace();
        }
      }
    }, 1000 / FRAME_RATE, 1000 / FRAME_RATE, TimeUnit.MILLISECONDS);
  }
}