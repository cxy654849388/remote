package com.chm.remote.client.utils;

import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avcodec.AVCodecContext;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.bytedeco.ffmpeg.avutil.AVFrame;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.swscale.SwsContext;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.nio.ByteBuffer;

import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avformat.av_register_all;
import static org.bytedeco.ffmpeg.global.avutil.*;
import static org.bytedeco.ffmpeg.global.swscale.*;

/**
 * @author caihongming
 * @version v1.0
 * @title H264Util
 * @package com.chm.remote.client.utils
 * @since 2019-12-13
 * description
 **/
public final class H264Util {

  /**
   * H264解码器
   */
  private static AVCodec h264Decoder;

  /**
   * H264上下文
   */
  private static AVCodecContext h264Context;

  static {
    av_register_all();
    //avformat_network_init();
    avcodec_register_all();
  }

  /**
   * h264 i帧 转换为 rgb 字节
   *
   * @param iFrameBytes
   * @return
   */
  public static byte[] getRGBBytes(byte[] iFrameBytes) throws Exception {
    byte[] resultBytes = null;
    //h264解码器
    h264Decoder = avcodec_find_decoder(avcodec.AV_CODEC_ID_H264);
    // h264解码上下文
    h264Context = avcodec_alloc_context3(h264Decoder);
    //打开h264解码器
    AVDictionary optionsDict = null;
    if (avcodec_open2(h264Context, h264Decoder, optionsDict) != 0) {
      System.out.println("打开h264编码器失败");
      return resultBytes;
    }

    int[] frameFinished = new int[1];

    AVFrame pFrame = av_frame_alloc();

    AVPacket av_packet_alloc = av_packet_alloc();

    av_init_packet(av_packet_alloc);


    av_packet_from_data(av_packet_alloc, iFrameBytes, iFrameBytes.length);

    //h264 解码为yuv
    int stauts = avcodec_decode_video2(h264Context, pFrame, frameFinished, av_packet_alloc);
    if (stauts < 0) {
      System.out.println("解码yuv失败");
      return resultBytes;
    }

    DoublePointer param = null;
    SwsContext sws_ctx = sws_getContext(1920, 1080, h264Context.pix_fmt(), 1920, 1080, AV_PIX_FMT_BGR24, SWS_FAST_BILINEAR, null, null, param);

    AVFrame outFrameRGB = av_frame_alloc();
    outFrameRGB.width(1920);
    outFrameRGB.height(1080);
    outFrameRGB.format(AV_PIX_FMT_BGR24);

    BytePointer buffer = new BytePointer(av_malloc(av_image_get_buffer_size(AV_PIX_FMT_BGR24, 1920, 1080, 1)));

    av_image_fill_arrays(outFrameRGB.data(), outFrameRGB.linesize(), buffer, AV_PIX_FMT_BGR24, 1920, 1080, 1);

    //把需要解码的视频帧送进解码器
    if (avcodec_send_packet(h264Context, av_packet_alloc) == 0) {
      if (avcodec_receive_frame(h264Context, pFrame) == 0) {
        sws_scale(sws_ctx, pFrame.data(), pFrame.linesize(), 0, 1080, outFrameRGB.data(), outFrameRGB.linesize());

        resultBytes = saveFrame(outFrameRGB, 1920, 1080);
      }
      av_packet_from_data(av_packet_alloc, iFrameBytes, iFrameBytes.length);
      //av_packet_free(av_packet_alloc);
    }
    av_packet_alloc.close();
    pFrame.close();
    outFrameRGB.close();
    return resultBytes;
  }

  public static byte[] saveFrame(AVFrame frameRGB, int width, int height) {
    BytePointer data = frameRGB.data(0);
    int size = width * height * 3;
    //复制虚拟机外内存数据到java虚拟机中，因为这个方法之后会清理内存
    byte[] bytes = new byte[size];
    data.position(0).limit(size).get(bytes, 0, size);
    return bytes;
  }

  /**
   * 24位BGR数组转BufferedImage
   *
   * @param src       -源数据数组
   * @param width     -宽度
   * @param height-高度
   * @return
   */
  public static BufferedImage BGR2BufferedImage(byte[] src, int width, int height) {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    Raster ra = image.getRaster();
    DataBuffer out = ra.getDataBuffer();
    DataBufferByte db = (DataBufferByte) out;
    ByteBuffer.wrap(db.getData()).put(src, 0, src.length);
    return image;
  }

}
