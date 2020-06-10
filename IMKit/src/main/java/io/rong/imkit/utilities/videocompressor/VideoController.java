//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utilities.videocompressor;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.Surface;
import io.rong.common.RLog;
import io.rong.imkit.utilities.videocompressor.videoslimmer.VideoSlimEncoder;
import io.rong.imkit.utilities.videocompressor.videoslimmer.listner.SlimProgressListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@SuppressLint({"NewApi"})
public class VideoController {
  private static final String TAG = VideoController.class.getSimpleName();
  private static final int PROCESSOR_TYPE_OTHER = 0;
  private static final int PROCESSOR_TYPE_QCOM = 1;
  private static final int PROCESSOR_TYPE_INTEL = 2;
  private static final int PROCESSOR_TYPE_MTK = 3;
  private static final int PROCESSOR_TYPE_SEC = 4;
  private static final int PROCESSOR_TYPE_TI = 5;
  private static final String MIME_TYPE = "video/avc";
  private static volatile VideoController Instance = null;
  private boolean videoConvertFirstWrite = true;
  public String path;

  public VideoController() {
  }

  public static VideoController getInstance() {
    VideoController localInstance = Instance;
    if (localInstance == null) {
      Class var1 = VideoController.class;
      synchronized(VideoController.class) {
        localInstance = Instance;
        if (localInstance == null) {
          Instance = localInstance = new VideoController();
        }
      }
    }

    return localInstance;
  }

  @SuppressLint({"NewApi"})
  private static int selectColorFormat(MediaCodecInfo codecInfo, String mimeType) {
    CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
    int lastColorFormat = 0;

    for(int i = 0; i < capabilities.colorFormats.length; ++i) {
      int colorFormat = capabilities.colorFormats[i];
      if (isRecognizedFormat(colorFormat)) {
        lastColorFormat = colorFormat;
        if (!codecInfo.getName().equals("OMX.SEC.AVC.Encoder") || colorFormat != 19) {
          return colorFormat;
        }
      }
    }

    return lastColorFormat;
  }

  private static boolean isRecognizedFormat(int colorFormat) {
    switch(colorFormat) {
      case 19:
      case 20:
      case 21:
      case 39:
      case 2130706688:
        return true;
      default:
        return false;
    }
  }

  public static native int convertVideoFrame(ByteBuffer var0, ByteBuffer var1, int var2, int var3, int var4, int var5, int var6);

  private static MediaCodecInfo selectCodec(String mimeType) {
    int numCodecs = MediaCodecList.getCodecCount();
    MediaCodecInfo lastCodecInfo = null;

    for(int i = 0; i < numCodecs; ++i) {
      MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
      if (codecInfo.isEncoder()) {
        String[] types = codecInfo.getSupportedTypes();
        String[] var6 = types;
        int var7 = types.length;

        for(int var8 = 0; var8 < var7; ++var8) {
          String type = var6[var8];
          if (type.equalsIgnoreCase(mimeType)) {
            lastCodecInfo = codecInfo;
            if (!codecInfo.getName().equals("OMX.SEC.avc.enc")) {
              return codecInfo;
            }

            if (codecInfo.getName().equals("OMX.SEC.AVC.Encoder")) {
              return codecInfo;
            }
          }
        }
      }
    }

    return lastCodecInfo;
  }

  public static void copyFile(File src, File dst) {
    FileInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;
    FileChannel inChannel = null;
    FileChannel outChannel = null;

    try {
      fileInputStream = new FileInputStream(src);
      fileOutputStream = new FileOutputStream(dst);
      inChannel = fileInputStream.getChannel();
      outChannel = fileOutputStream.getChannel();
      inChannel.transferTo(1L, inChannel.size(), outChannel);
    } catch (Exception var27) {
      RLog.e(TAG, "copyFile", var27);
    } finally {
      try {
        if (inChannel != null) {
          inChannel.close();
        }
      } catch (IOException var26) {
        RLog.e(TAG, "copyFile inChannel close", var26);
      }

      try {
        if (outChannel != null) {
          outChannel.close();
        }
      } catch (IOException var25) {
        RLog.e(TAG, "copyFile outChannel close", var25);
      }

      try {
        if (fileInputStream != null) {
          fileInputStream.close();
        }
      } catch (IOException var24) {
        RLog.e(TAG, "copyFile fileInputStream close", var24);
      }

      try {
        if (fileOutputStream != null) {
          fileOutputStream.close();
        }
      } catch (IOException var23) {
        RLog.e(TAG, "copyFile fileOutputStream close", var23);
      }

    }

  }

  private void didWriteData(boolean last, boolean error) {
    boolean firstWrite = this.videoConvertFirstWrite;
    if (firstWrite) {
      this.videoConvertFirstWrite = false;
    }

  }

  public void scheduleVideoConvert(String path, String dest) {
    this.startVideoConvertFromQueue(path, dest);
  }

  private void startVideoConvertFromQueue(String path, String dest) {
    VideoController.VideoConvertRunnable.runConversion(path, dest);
  }

  @TargetApi(16)
  private long readAndWriteTrack(MediaExtractor extractor, MP4Builder mediaMuxer, BufferInfo info, long start, long end, File file, boolean isAudio) throws Exception {
    int trackIndex = this.selectTrack(extractor, isAudio);
    if (trackIndex < 0) {
      return -1L;
    } else {
      extractor.selectTrack(trackIndex);
      MediaFormat trackFormat = extractor.getTrackFormat(trackIndex);
      int muxerTrackIndex = mediaMuxer.addTrack(trackFormat, isAudio);
      int maxBufferSize = trackFormat.getInteger("max-input-size");
      boolean inputDone = false;
      if (start > 0L) {
        extractor.seekTo(start, 0);
      } else {
        extractor.seekTo(0L, 0);
      }

      ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);
      long startTime = -1L;

      while(!inputDone) {
        boolean eof = false;
        int index = extractor.getSampleTrackIndex();
        if (index == trackIndex) {
          info.size = extractor.readSampleData(buffer, 0);
          if (info.size < 0) {
            info.size = 0;
            eof = true;
          } else {
            info.presentationTimeUs = extractor.getSampleTime();
            if (start > 0L && startTime == -1L) {
              startTime = info.presentationTimeUs;
            }

            if (end >= 0L && info.presentationTimeUs >= end) {
              eof = true;
            } else {
              info.offset = 0;
              info.flags = extractor.getSampleFlags();
              mediaMuxer.writeSampleData(muxerTrackIndex, buffer, info, isAudio);
              extractor.advance();
            }
          }
        } else if (index == -1) {
          eof = true;
        }

        if (eof) {
          inputDone = true;
        }
      }

      extractor.unselectTrack(trackIndex);
      return startTime;
    }
  }

  @TargetApi(16)
  private int selectTrack(MediaExtractor extractor, boolean audio) {
    int numTracks = extractor.getTrackCount();

    for(int i = 0; i < numTracks; ++i) {
      MediaFormat format = extractor.getTrackFormat(i);
      String mime = format.getString("mime");
      if (audio) {
        if (mime.startsWith("audio/")) {
          return i;
        }
      } else if (mime.startsWith("video/")) {
        return i;
      }
    }

    return -5;
  }

  @TargetApi(16)
  public boolean convertVideo(String sourcePath, String destinationPath, VideoController.CompressProgressListener listener) {
    if (!(new File(sourcePath)).exists()) {
      return false;
    } else {
      this.path = sourcePath;
      MediaMetadataRetriever retriever = new MediaMetadataRetriever();

      try {
        retriever.setDataSource(this.path);
      } catch (Exception var81) {
        RLog.e(TAG, var81.toString());
        return false;
      }

      String width = retriever.extractMetadata(18);
      String height = retriever.extractMetadata(19);
      String rotation = retriever.extractMetadata(24);
      if (!TextUtils.isEmpty(width) && !TextUtils.isEmpty(height) && !TextUtils.isEmpty(rotation)) {
        long duration = Long.valueOf(retriever.extractMetadata(9)) * 1000L;
        long startTime = -1L;
        long endTime = -1L;
        int rotationValue = Integer.valueOf(rotation);
        int originalWidth = Integer.valueOf(width);
        int originalHeight = Integer.valueOf(height);
        RLog.d(TAG, "Resolution of origin width is " + originalWidth);
        RLog.d(TAG, "Resolution of origin height is " + originalHeight);
        RLog.d(TAG, "Origin rotation value is " + rotationValue);
        int resultWidth;
        if (originalWidth % 16 != 0) {
          resultWidth = originalWidth / 16;
          originalWidth = 16 * resultWidth;
        }

        if (originalHeight % 16 != 0) {
          resultWidth = originalHeight / 16;
          originalHeight = 16 * resultWidth;
        }

        int resultHeight;
        double ratio;
        int quotient;
        if (originalHeight >= originalWidth) {
          if (originalHeight <= 960 && originalWidth <= 544) {
            if (originalHeight == 960 && originalWidth == 544) {
              if (VERSION.SDK_INT < 18) {
                resultWidth = originalWidth - 16;
                resultHeight = originalHeight - 16;
              } else {
                resultWidth = originalWidth;
                resultHeight = originalHeight;
              }
            } else {
              resultWidth = originalWidth;
              resultHeight = originalHeight;
            }
          } else if (originalHeight <= 960 && originalWidth > 544) {
            resultWidth = 544;
            ratio = (double)originalHeight / (double)originalWidth;
            quotient = (int)(ratio * (double)resultWidth) / 16;
            resultHeight = 16 * quotient;
          } else {
            resultHeight = 960;
            ratio = (double)originalWidth / (double)originalHeight;
            quotient = (int)(ratio * (double)resultHeight) / 16;
            resultWidth = 16 * quotient;
          }
        } else if (originalWidth <= 960 && originalHeight <= 544) {
          resultWidth = originalWidth;
          resultHeight = originalHeight;
        } else if (originalWidth <= 960 && originalHeight > 544) {
          resultHeight = 544;
          ratio = (double)originalWidth / (double)originalHeight;
          quotient = (int)(ratio * (double)resultHeight) / 16;
          resultWidth = 16 * quotient;
        } else {
          resultWidth = 960;
          ratio = (double)originalHeight / (double)originalWidth;
          quotient = (int)(ratio * (double)resultWidth) / 16;
          resultHeight = 16 * quotient;
        }

        int bitrate = resultWidth / 2 * (resultHeight / 2) * 10;
        int rotateRender = 0;
        File cacheFile = new File(destinationPath);
        int temp;
        if (VERSION.SDK_INT < 18 && resultHeight > resultWidth && resultWidth != originalWidth && resultHeight != originalHeight) {
          temp = resultHeight;
          resultHeight = resultWidth;
          resultWidth = temp;
          rotationValue = 90;
          rotateRender = 270;
        } else if (VERSION.SDK_INT > 20) {
          if (rotationValue == 90) {
            temp = resultHeight;
            resultHeight = resultWidth;
            resultWidth = temp;
            rotationValue = 0;
            rotateRender = 270;
          } else if (rotationValue == 180) {
            rotateRender = 180;
            rotationValue = 0;
          } else if (rotationValue == 270) {
            temp = resultHeight;
            resultHeight = resultWidth;
            resultWidth = temp;
            rotationValue = 0;
            rotateRender = 90;
          }
        }

        RLog.d(TAG, "Resolution of result width is " + resultWidth);
        RLog.d(TAG, "Resolution of result height is " + resultHeight);
        RLog.d(TAG, "Result rotation value is " + rotationValue);
        RLog.d(TAG, "Result render value is " + rotateRender);
        File inputFile = new File(this.path);
        if (!inputFile.canRead()) {
          this.didWriteData(true, true);
          return false;
        } else {
          this.videoConvertFirstWrite = true;
          boolean error = false;
          long videoStartTime = startTime;
          long time = System.currentTimeMillis();
          if (resultWidth != 0 && resultHeight != 0 && VERSION.SDK_INT >= 18) {
            try {
              boolean result = (new VideoSlimEncoder()).convertVideo(sourcePath, destinationPath, resultWidth, resultHeight, bitrate, (SlimProgressListener)null);
              if (!result) {
                File file = new File(destinationPath);
                if (file != null && file.exists()) {
                  boolean delete = file.delete();
                  RLog.d(TAG, "delete:" + delete);
                }

                resultWidth += 16;
                resultHeight += 16;
                result = (new VideoSlimEncoder()).convertVideo(sourcePath, destinationPath, resultWidth, resultHeight, resultWidth / 2 * (resultHeight / 2) * 10, (SlimProgressListener)null);
              }

              return result;
            } catch (Exception var79) {
              RLog.e(TAG, "compress fail", var79);
              return false;
            }
          } else if (resultWidth != 0 && resultHeight != 0) {
            MP4Builder mediaMuxer = null;
            MediaExtractor extractor = null;

            try {
              BufferInfo info = new BufferInfo();
              Mp4Movie movie = new Mp4Movie();
              movie.setCacheFile(cacheFile);
              movie.setRotation(rotationValue);
              movie.setSize(resultWidth, resultHeight);
              mediaMuxer = (new MP4Builder()).createMovie(movie);
              extractor = new MediaExtractor();
              extractor.setDataSource(inputFile.toString());
              if (resultWidth == originalWidth && resultHeight == originalHeight) {
                long videoTime = this.readAndWriteTrack(extractor, mediaMuxer, info, startTime, endTime, cacheFile, false);
                if (videoTime != -1L) {
                  videoStartTime = videoTime;
                }
              } else {
                int videoIndex = this.selectTrack(extractor, false);
                if (videoIndex >= 0) {
                  MediaCodec decoder = null;
                  MediaCodec encoder = null;
                  InputSurface inputSurface = null;
                  OutputSurface outputSurface = null;

                  try {
                    long videoTime = -1L;
                    boolean outputDone = false;
                    boolean inputDone = false;
                    boolean decoderDone = false;
                    int swapUV = 0;
                    int videoTrackIndex = -5;
                    int processorType = 0;
                    String manufacturer = Build.MANUFACTURER.toLowerCase();
                    MediaCodecInfo codecInfo = selectCodec("video/avc");
                    if (codecInfo == null) {
                      RLog.e(TAG, "codecInfo is null ");
                      boolean var93 = false;
                      return var93;
                    }

                    int colorFormat;
                    if (VERSION.SDK_INT < 18) {
                      colorFormat = selectColorFormat(codecInfo, "video/avc");
                      if (colorFormat == 0) {
                        throw new RuntimeException("no supported color format");
                      }

                      String codecName = codecInfo.getName();
                      if (codecName.contains("OMX.qcom.")) {
                        processorType = 1;
                        if (VERSION.SDK_INT == 16 && (manufacturer.equals("lge") || manufacturer.equals("nokia"))) {
                          swapUV = 1;
                        }
                      } else if (codecName.contains("OMX.Intel.")) {
                        processorType = 2;
                      } else if (codecName.equals("OMX.MTK.VIDEO.ENCODER.AVC")) {
                        processorType = 3;
                      } else if (codecName.equals("OMX.SEC.AVC.Encoder")) {
                        processorType = 4;
                        swapUV = 1;
                      } else if (codecName.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                        processorType = 5;
                      }
                    } else {
                      colorFormat = 2130708361;
                    }

                    RLog.e(TAG, "codec = " + codecInfo.getName() + " manufacturer = " + manufacturer + "device = " + Build.MODEL);
                    RLog.d(TAG, "colorFormat = " + colorFormat);
                    int padding = 0;
                    int bufferSize = resultWidth * resultHeight * 3 / 2;
                    int resultHeightAligned;
                    if (processorType == 0) {
                      if (resultHeight % 16 != 0) {
                        resultHeightAligned = resultHeight + (16 - resultHeight % 16);
                        padding = resultWidth * (resultHeightAligned - resultHeight);
                        bufferSize += padding * 5 / 4;
                      }
                    } else if (processorType == 1) {
                      if (!manufacturer.toLowerCase().equals("lge")) {
                        int uvoffset = resultWidth * resultHeight + 2047 & -2048;
                        padding = uvoffset - resultWidth * resultHeight;
                        bufferSize += padding;
                      }
                    } else if (processorType != 5 && processorType == 3 && manufacturer.equals("baidu")) {
                      resultHeightAligned = resultHeight + (16 - resultHeight % 16);
                      padding = resultWidth * (resultHeightAligned - resultHeight);
                      bufferSize += padding * 5 / 4;
                    }

                    extractor.selectTrack(videoIndex);
                    if (startTime > 0L) {
                      extractor.seekTo(startTime, 0);
                    } else {
                      extractor.seekTo(0L, 0);
                    }

                    MediaFormat inputFormat = extractor.getTrackFormat(videoIndex);
                    MediaFormat outputFormat = MediaFormat.createVideoFormat("video/avc", resultWidth, resultHeight);
                    outputFormat.setInteger("color-format", colorFormat);
                    outputFormat.setInteger("bitrate", bitrate);
                    outputFormat.setInteger("frame-rate", 25);
                    outputFormat.setInteger("i-frame-interval", 10);
                    if (VERSION.SDK_INT < 18) {
                      outputFormat.setInteger("stride", resultWidth + 32);
                      outputFormat.setInteger("slice-height", resultHeight);
                    }

                    encoder = MediaCodec.createByCodecName(codecInfo.getName());
                    encoder.configure(outputFormat, (Surface)null, (MediaCrypto)null, 1);
                    if (VERSION.SDK_INT >= 18) {
                      inputSurface = new InputSurface(encoder.createInputSurface());
                      inputSurface.makeCurrent();
                    }

                    encoder.start();
                    decoder = MediaCodec.createDecoderByType(inputFormat.getString("mime"));
                    if (VERSION.SDK_INT >= 18) {
                      outputSurface = new OutputSurface();
                    } else {
                      outputSurface = new OutputSurface(resultWidth, resultHeight, rotateRender);
                    }

                    decoder.configure(inputFormat, outputSurface.getSurface(), (MediaCrypto)null, 0);
                    decoder.start();
                    ByteBuffer[] decoderInputBuffers = null;
                    ByteBuffer[] encoderOutputBuffers = null;
                    ByteBuffer[] encoderInputBuffers = null;
                    if (VERSION.SDK_INT < 21) {
                      decoderInputBuffers = decoder.getInputBuffers();
                      encoderOutputBuffers = encoder.getOutputBuffers();
                      if (VERSION.SDK_INT < 18) {
                        encoderInputBuffers = encoder.getInputBuffers();
                      }
                    }

                    label1002:
                    while(true) {
                      if (outputDone) {
                        if (videoTime != -1L) {
                          videoStartTime = videoTime;
                        }
                        break;
                      }

                      boolean decoderOutputAvailable;
                      int encoderStatus;
                      ByteBuffer encodedData;
                      if (!inputDone) {
                        decoderOutputAvailable = false;
                        int index = extractor.getSampleTrackIndex();
                        if (index == videoIndex) {
                          encoderStatus = decoder.dequeueInputBuffer(2500L);
                          if (encoderStatus >= 0) {
                            if (VERSION.SDK_INT < 21) {
                              encodedData = decoderInputBuffers[encoderStatus];
                            } else {
                              encodedData = decoder.getInputBuffer(encoderStatus);
                            }

                            int chunkSize = extractor.readSampleData(encodedData, 0);
                            if (chunkSize < 0) {
                              decoder.queueInputBuffer(encoderStatus, 0, 0, 0L, 4);
                              inputDone = true;
                            } else {
                              decoder.queueInputBuffer(encoderStatus, 0, chunkSize, extractor.getSampleTime(), 0);
                              extractor.advance();
                            }
                          }
                        } else if (index == -1) {
                          decoderOutputAvailable = true;
                        }

                        if (decoderOutputAvailable) {
                          encoderStatus = decoder.dequeueInputBuffer(2500L);
                          if (encoderStatus >= 0) {
                            decoder.queueInputBuffer(encoderStatus, 0, 0, 0L, 4);
                            inputDone = true;
                          }
                        }
                      }

                      decoderOutputAvailable = !decoderDone;
                      boolean encoderOutputAvailable = true;

                      while(true) {
                        while(true) {
                          do {
                            do {
                              if (!decoderOutputAvailable && !encoderOutputAvailable) {
                                continue label1002;
                              }

                              encoderStatus = encoder.dequeueOutputBuffer(info, 2500L);
                              if (encoderStatus == -1) {
                                encoderOutputAvailable = false;
                              } else if (encoderStatus == -3) {
                                if (VERSION.SDK_INT < 21) {
                                  encoderOutputBuffers = encoder.getOutputBuffers();
                                }
                              } else if (encoderStatus == -2) {
                                MediaFormat newFormat = encoder.getOutputFormat();
                                if (videoTrackIndex == -5) {
                                  videoTrackIndex = mediaMuxer.addTrack(newFormat, false);
                                }
                              } else {
                                if (encoderStatus < 0) {
                                  throw new RuntimeException("unexpected result from encoder.dequeueOutputBuffer: " + encoderStatus);
                                }

                                if (VERSION.SDK_INT < 21) {
                                  encodedData = encoderOutputBuffers[encoderStatus];
                                } else {
                                  encodedData = encoder.getOutputBuffer(encoderStatus);
                                }

                                if (encodedData == null) {
                                  throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                                }

                                if (info.size > 1) {
                                  if ((info.flags & 2) == 0) {
                                    if (mediaMuxer.writeSampleData(videoTrackIndex, encodedData, info, false)) {
                                      this.didWriteData(false, false);
                                    }
                                  } else if (videoTrackIndex == -5) {
                                    byte[] csd = new byte[info.size];
                                    encodedData.limit(info.offset + info.size);
                                    encodedData.position(info.offset);
                                    encodedData.get(csd);
                                    ByteBuffer sps = null;
                                    ByteBuffer pps = null;

                                    for(int a = info.size - 1; a >= 0 && a > 3; --a) {
                                      if (csd[a] == 1 && csd[a - 1] == 0 && csd[a - 2] == 0 && csd[a - 3] == 0) {
                                        sps = ByteBuffer.allocate(a - 3);
                                        pps = ByteBuffer.allocate(info.size - (a - 3));
                                        sps.put(csd, 0, a - 3).position(0);
                                        pps.put(csd, a - 3, info.size - (a - 3)).position(0);
                                        break;
                                      }
                                    }

                                    MediaFormat newFormat = MediaFormat.createVideoFormat("video/avc", resultWidth, resultHeight);
                                    if (sps != null && pps != null) {
                                      newFormat.setByteBuffer("csd-0", sps);
                                      newFormat.setByteBuffer("csd-1", pps);
                                    }

                                    videoTrackIndex = mediaMuxer.addTrack(newFormat, false);
                                  }
                                }

                                outputDone = (info.flags & 4) != 0;
                                encoder.releaseOutputBuffer(encoderStatus, false);
                              }
                            } while(encoderStatus != -1);
                          } while(decoderDone);

                          int decoderStatus = decoder.dequeueOutputBuffer(info, 2500L);
                          if (decoderStatus == -1) {
                            decoderOutputAvailable = false;
                          } else if (decoderStatus != -3) {
                            if (decoderStatus == -2) {
                              MediaFormat newFormat = decoder.getOutputFormat();
                              RLog.d(TAG, "newFormat = " + newFormat);
                            } else {
                              if (decoderStatus < 0) {
                                throw new RuntimeException("unexpected result from decoder.dequeueOutputBuffer: " + decoderStatus);
                              }

                              boolean doRender;
                              if (VERSION.SDK_INT >= 18) {
                                doRender = info.size != 0;
                              } else {
                                doRender = info.size != 0 || info.presentationTimeUs != 0L;
                              }

                              if (endTime > 0L && info.presentationTimeUs >= endTime) {
                                inputDone = true;
                                decoderDone = true;
                                doRender = false;
                                info.flags |= 4;
                              }

                              if (startTime > 0L && videoTime == -1L) {
                                if (info.presentationTimeUs < startTime) {
                                  doRender = false;
                                  RLog.e(TAG, "drop frame startTime = " + startTime + " present time = " + info.presentationTimeUs);
                                } else {
                                  videoTime = info.presentationTimeUs;
                                }
                              }

                              decoder.releaseOutputBuffer(decoderStatus, doRender);
                              if (doRender) {
                                boolean errorWait = false;

                                try {
                                  outputSurface.awaitNewImage();
                                } catch (Exception var80) {
                                  errorWait = true;
                                  RLog.e(TAG, var80.getMessage());
                                }

                                if (!errorWait) {
                                  if (VERSION.SDK_INT >= 18) {
                                    outputSurface.drawImage(false);
                                    inputSurface.setPresentationTime(info.presentationTimeUs * 1000L);
                                    if (listener != null) {
                                      listener.onProgress((float)info.presentationTimeUs / (float)duration * 100.0F);
                                    }

                                    inputSurface.swapBuffers();
                                  } else {
                                    int inputBufIndex = encoder.dequeueInputBuffer(2500L);
                                    if (inputBufIndex >= 0) {
                                      outputSurface.drawImage(true);
                                      ByteBuffer rgbBuf = outputSurface.getFrame();
                                      ByteBuffer yuvBuf = encoderInputBuffers[inputBufIndex];
                                      yuvBuf.clear();
                                      convertVideoFrame(rgbBuf, yuvBuf, colorFormat, resultWidth, resultHeight, padding, swapUV);
                                      encoder.queueInputBuffer(inputBufIndex, 0, bufferSize, info.presentationTimeUs, 0);
                                    } else {
                                      RLog.e(TAG, "input buffer not available");
                                    }
                                  }
                                }
                              }

                              if ((info.flags & 4) != 0) {
                                decoderOutputAvailable = false;
                                RLog.d(TAG, "decoder stream end");
                                if (VERSION.SDK_INT >= 18) {
                                  encoder.signalEndOfInputStream();
                                } else {
                                  int inputBufIndex = encoder.dequeueInputBuffer(2500L);
                                  if (inputBufIndex >= 0) {
                                    encoder.queueInputBuffer(inputBufIndex, 0, 1, info.presentationTimeUs, 4);
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  } catch (Exception var82) {
                    RLog.e(TAG, var82.getMessage());
                    error = true;
                  }

                  extractor.unselectTrack(videoIndex);
                  if (outputSurface != null) {
                    outputSurface.release();
                  }

                  if (inputSurface != null) {
                    inputSurface.release();
                  }

                  if (decoder != null) {
                    decoder.stop();
                    decoder.release();
                  }

                  if (encoder != null) {
                    encoder.stop();
                    encoder.release();
                  }
                }
              }

              if (!error) {
                this.readAndWriteTrack(extractor, mediaMuxer, info, videoStartTime, endTime, cacheFile, true);
              }
            } catch (Exception var83) {
              error = true;
              RLog.e(TAG, var83.getMessage());
            } finally {
              if (extractor != null) {
                extractor.release();
              }

              if (mediaMuxer != null) {
                try {
                  mediaMuxer.finishMovie(false);
                } catch (Exception var78) {
                  RLog.e(TAG, var78.getMessage());
                }
              }

              RLog.d(TAG, "time = " + (System.currentTimeMillis() - time));
            }

            this.didWriteData(true, error);
            RLog.d(TAG, "source path: " + this.path);
            RLog.d(TAG, "cacheFile:" + cacheFile.getPath());
            RLog.d(TAG, "inputFile:" + inputFile.getPath());
            return true;
          } else {
            this.didWriteData(true, true);
            return false;
          }
        }
      } else {
        return false;
      }
    }
  }

  public static class VideoConvertRunnable implements Runnable {
    private String videoPath;
    private String destPath;

    private VideoConvertRunnable(String videoPath, String destPath) {
      this.videoPath = videoPath;
      this.destPath = destPath;
    }

    private static void runConversion(final String videoPath, final String destPath) {
      (new Thread(new Runnable() {
        public void run() {
          try {
            VideoController.VideoConvertRunnable wrapper = new VideoController.VideoConvertRunnable(videoPath, destPath);
            Thread th = new Thread(wrapper, "VideoConvertRunnable");
            th.start();
            th.join();
          } catch (Exception var3) {
            RLog.e(VideoController.TAG, var3.getMessage());
          }

        }
      })).start();
    }

    public void run() {
      VideoController.getInstance().convertVideo(this.videoPath, this.destPath, (VideoController.CompressProgressListener)null);
    }
  }

  interface CompressProgressListener {
    void onProgress(float var1);
  }
}