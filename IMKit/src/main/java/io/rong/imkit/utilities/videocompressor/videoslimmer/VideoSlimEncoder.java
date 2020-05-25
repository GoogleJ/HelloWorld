//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utilities.videocompressor.videoslimmer;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.os.Build.VERSION;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import io.rong.common.RLog;
import io.rong.imkit.utilities.videocompressor.videoslimmer.listner.SlimProgressListener;
import io.rong.imkit.utilities.videocompressor.videoslimmer.muxer.CodecInputSurface;

public class VideoSlimEncoder {
    private static final String TAG = "VideoSlimEncoder";
    private static final boolean VERBOSE = true;
    private String path;
    private String outputPath;
    private static final String MIME_TYPE = "video/avc";
    private BufferInfo mBufferInfo;
    private MediaMuxer mMuxer;
    private MediaCodec mEncoder;
    private MediaCodec mDecoder;
    private int mTrackIndex;
    private CodecInputSurface mInputSurface;
    private int mWidth = -1;
    private int mHeight = -1;
    private int mBitRate = -1;
    private static final int MEDIATYPE_NOT_AUDIO_VIDEO = -233;
    private static int FRAME_RATE = 25;
    private static int IFRAME_INTERVAL = 10;
    private final int TIMEOUT_USEC = 2500;

    public VideoSlimEncoder() {
    }

    @RequiresApi(
            api = 18
    )
    public boolean convertVideo(String sourcePath, String destinationPath, int nwidth, int nheight, int nbitrate, SlimProgressListener listener) {
        this.path = sourcePath;
        this.outputPath = destinationPath;
        if (this.checkParmsError(nwidth, nheight, nbitrate)) {
            return false;
        } else {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this.path);
            String width = retriever.extractMetadata(18);
            String height = retriever.extractMetadata(19);
            String rotation = retriever.extractMetadata(24);
            long duration = Long.valueOf(retriever.extractMetadata(9)) * 1000L;
            long startTime = -1L;
            long endTime = -1L;
            int originalWidth = Integer.valueOf(width);
            int originalHeight = Integer.valueOf(height);
            this.mBitRate = nbitrate;
            this.mWidth = nwidth;
            this.mHeight = nheight;
            boolean error = false;
            long videoStartTime = -1L;
            long time = System.currentTimeMillis();
            File cacheFile = new File(destinationPath);
            File inputFile = new File(this.path);
            if (!inputFile.canRead()) {
                return false;
            } else {
                MediaExtractor extractor = null;
                MediaExtractor mAudioExtractor = null;

                try {
                    extractor = new MediaExtractor();
                    extractor.setDataSource(inputFile.toString());
                    mAudioExtractor = new MediaExtractor();
                    mAudioExtractor.setDataSource(inputFile.toString());

                    try {
                        this.mMuxer = new MediaMuxer(this.outputPath, 0);
                    } catch (IOException var55) {
                        throw new RuntimeException("MediaMuxer creation failed", var55);
                    }

                    int muxerAudioTrackIndex = 0;
                    int audioIndex = this.selectTrack(mAudioExtractor, true);
                    if (audioIndex >= 0) {
                        mAudioExtractor.selectTrack(audioIndex);
                        mAudioExtractor.seekTo(0L, 0);
                        MediaFormat trackFormat = mAudioExtractor.getTrackFormat(audioIndex);
                        muxerAudioTrackIndex = this.mMuxer.addTrack(trackFormat);
                    }

                    if (nwidth == originalWidth && nheight == originalHeight) {
                        long videoTime = this.simpleReadAndWriteTrack(extractor, this.mMuxer, this.mBufferInfo, startTime, endTime, false);
                        if (videoTime != -1L) {
                            videoStartTime = videoTime;
                        }
                    } else {
                        int videoIndex = this.selectTrack(extractor, false);
                        if (videoIndex >= 0) {
                            long videoTime = -1L;
                            boolean outputDone = false;
                            boolean inputDone = false;
                            boolean decoderDone = false;
                            int videoTrackIndex = -233;
                            extractor.selectTrack(videoIndex);
                            if (startTime > 0L) {
                                extractor.seekTo(startTime, 0);
                            } else {
                                extractor.seekTo(0L, 0);
                            }

                            MediaFormat inputFormat = extractor.getTrackFormat(videoIndex);
                            this.prepareEncoder(inputFormat);
                            ByteBuffer[] decoderInputBuffers = null;
                            ByteBuffer[] encoderOutputBuffers = null;
                            decoderInputBuffers = this.mDecoder.getInputBuffers();
                            encoderOutputBuffers = this.mEncoder.getOutputBuffers();

                            while (!outputDone) {
                                boolean decoderOutputAvailable;
                                int encoderStatus;
                                ByteBuffer encodedData;
                                if (!inputDone) {
                                    decoderOutputAvailable = false;
                                    int index = extractor.getSampleTrackIndex();
                                    if (index == videoIndex) {
                                        encoderStatus = this.mDecoder.dequeueInputBuffer(2500L);
                                        if (encoderStatus >= 0) {
                                            if (VERSION.SDK_INT < 21) {
                                                encodedData = decoderInputBuffers[encoderStatus];
                                            } else {
                                                encodedData = this.mDecoder.getInputBuffer(encoderStatus);
                                            }

                                            int chunkSize = extractor.readSampleData(encodedData, 0);
                                            if (chunkSize < 0) {
                                                this.mDecoder.queueInputBuffer(encoderStatus, 0, 0, 0L, 4);
                                                inputDone = true;
                                            } else {
                                                this.mDecoder.queueInputBuffer(encoderStatus, 0, chunkSize, extractor.getSampleTime(), 0);
                                                extractor.advance();
                                            }
                                        }
                                    } else if (index == -1) {
                                        decoderOutputAvailable = true;
                                    }

                                    if (decoderOutputAvailable) {
                                        encoderStatus = this.mDecoder.dequeueInputBuffer(2500L);
                                        if (encoderStatus >= 0) {
                                            this.mDecoder.queueInputBuffer(encoderStatus, 0, 0, 0L, 4);
                                            inputDone = true;
                                        }
                                    }
                                }

                                decoderOutputAvailable = !decoderDone;
                                boolean encoderOutputAvailable = true;

                                while (decoderOutputAvailable || encoderOutputAvailable) {
                                    encoderStatus = this.mEncoder.dequeueOutputBuffer(this.mBufferInfo, 2500L);
                                    if (encoderStatus == -1) {
                                        encoderOutputAvailable = false;
                                    } else if (encoderStatus == -3) {
                                        if (VERSION.SDK_INT < 21) {
                                            encoderOutputBuffers = this.mEncoder.getOutputBuffers();
                                        }
                                    } else if (encoderStatus == -2) {
                                        MediaFormat newFormat = this.mEncoder.getOutputFormat();
                                        if (videoTrackIndex == -233) {
                                            videoTrackIndex = this.mMuxer.addTrack(newFormat);
                                            this.mTrackIndex = videoTrackIndex;
                                            this.mMuxer.start();
                                        }
                                    } else {
                                        if (encoderStatus < 0) {
                                            throw new RuntimeException("unexpected result from mEncoder.dequeueOutputBuffer: " + encoderStatus);
                                        }

                                        if (VERSION.SDK_INT < 21) {
                                            encodedData = encoderOutputBuffers[encoderStatus];
                                        } else {
                                            encodedData = this.mEncoder.getOutputBuffer(encoderStatus);
                                        }

                                        if (encodedData == null) {
                                            throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                                        }

                                        if (this.mBufferInfo.size > 1) {
                                            if ((this.mBufferInfo.flags & 2) == 0) {
                                                this.mMuxer.writeSampleData(videoTrackIndex, encodedData, this.mBufferInfo);
                                            } else if (videoTrackIndex == -233) {
                                                byte[] csd = new byte[this.mBufferInfo.size];
                                                encodedData.limit(this.mBufferInfo.offset + this.mBufferInfo.size);
                                                encodedData.position(this.mBufferInfo.offset);
                                                encodedData.get(csd);
                                                ByteBuffer sps = null;
                                                ByteBuffer pps = null;

                                                for (int a = this.mBufferInfo.size - 1; a >= 0 && a > 3; --a) {
                                                    if (csd[a] == 1 && csd[a - 1] == 0 && csd[a - 2] == 0 && csd[a - 3] == 0) {
                                                        sps = ByteBuffer.allocate(a - 3);
                                                        pps = ByteBuffer.allocate(this.mBufferInfo.size - (a - 3));
                                                        sps.put(csd, 0, a - 3).position(0);
                                                        pps.put(csd, a - 3, this.mBufferInfo.size - (a - 3)).position(0);
                                                        break;
                                                    }
                                                }

                                                MediaFormat newFormat = MediaFormat.createVideoFormat("video/avc", nwidth, nheight);
                                                if (sps != null && pps != null) {
                                                    newFormat.setByteBuffer("csd-0", sps);
                                                    newFormat.setByteBuffer("csd-1", pps);
                                                }

                                                videoTrackIndex = this.mMuxer.addTrack(newFormat);
                                                this.mMuxer.start();
                                            }
                                        }

                                        outputDone = (this.mBufferInfo.flags & 4) != 0;
                                        this.mEncoder.releaseOutputBuffer(encoderStatus, false);
                                    }

                                    if (encoderStatus == -1 && !decoderDone) {
                                        int decoderStatus = this.mDecoder.dequeueOutputBuffer(this.mBufferInfo, 2500L);
                                        if (decoderStatus == -1) {
                                            decoderOutputAvailable = false;
                                        } else if (decoderStatus != -3) {
                                            if (decoderStatus == -2) {
                                                MediaFormat var63 = this.mDecoder.getOutputFormat();
                                            } else {
                                                if (decoderStatus < 0) {
                                                    throw new RuntimeException("unexpected result from mDecoder.dequeueOutputBuffer: " + decoderStatus);
                                                }

                                                boolean doRender = false;
                                                doRender = this.mBufferInfo.size != 0;
                                                if (endTime > 0L && this.mBufferInfo.presentationTimeUs >= endTime) {
                                                    inputDone = true;
                                                    decoderDone = true;
                                                    doRender = false;
                                                    BufferInfo var10000 = this.mBufferInfo;
                                                    var10000.flags |= 4;
                                                }

                                                if (startTime > 0L && videoTime == -1L) {
                                                    if (this.mBufferInfo.presentationTimeUs < startTime) {
                                                        doRender = false;
                                                    } else {
                                                        videoTime = this.mBufferInfo.presentationTimeUs;
                                                    }
                                                }

                                                this.mDecoder.releaseOutputBuffer(decoderStatus, doRender);
                                                if (doRender) {
                                                    boolean errorWait = false;

                                                    try {
                                                        this.mInputSurface.awaitNewImage();
                                                    } catch (Exception var54) {
                                                        errorWait = true;
                                                        RLog.e("VideoSlimEncoder", var54.getMessage());
                                                    }

                                                    if (!errorWait) {
                                                        this.mInputSurface.drawImage();
                                                        this.mInputSurface.setPresentationTime(this.mBufferInfo.presentationTimeUs * 1000L);
                                                        if (listener != null) {
                                                            listener.onProgress((float) this.mBufferInfo.presentationTimeUs / (float) duration * 100.0F);
                                                        }

                                                        this.mInputSurface.swapBuffers();
                                                    }
                                                }

                                                if ((this.mBufferInfo.flags & 4) != 0) {
                                                    decoderOutputAvailable = false;
                                                    this.mEncoder.signalEndOfInputStream();
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (videoTime != -1L) {
                                videoStartTime = videoTime;
                            }
                        }

                        extractor.unselectTrack(videoIndex);
                    }

                    this.writeAudioTrack(mAudioExtractor, this.mMuxer, this.mBufferInfo, videoStartTime, endTime, cacheFile, muxerAudioTrackIndex);
                } catch (Exception var56) {
                    error = true;
                    RLog.e("VideoSlimEncoder", var56.getMessage());
                } finally {
                    if (extractor != null) {
                        extractor.release();
                        extractor = null;
                    }

                    if (mAudioExtractor != null) {
                        mAudioExtractor.release();
                        mAudioExtractor = null;
                    }

                }

                boolean b = this.releaseCoder();
                return !error || b;
            }
        }
    }

    private boolean checkParmsError(int nwidth, int nheight, int nbitrate) {
        return nwidth <= 0 || nheight <= 0 || nbitrate <= 0;
    }

    @RequiresApi(
            api = 18
    )
    private long simpleReadAndWriteTrack(MediaExtractor extractor, MediaMuxer mediaMuxer, BufferInfo info, long start, long end, boolean isAudio) throws Exception {
        int trackIndex = this.selectTrack(extractor, isAudio);
        if (trackIndex < 0) {
            return -1L;
        } else {
            extractor.selectTrack(trackIndex);
            MediaFormat trackFormat = extractor.getTrackFormat(trackIndex);
            int muxerTrackIndex = mediaMuxer.addTrack(trackFormat);
            if (!isAudio) {
                mediaMuxer.start();
            }

            int maxBufferSize = trackFormat.getInteger("max-input-size");
            boolean inputDone = false;
            if (start > 0L) {
                extractor.seekTo(start, 0);
            } else {
                extractor.seekTo(0L, 0);
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);
            long startTime = -1L;

            while (!inputDone) {
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
                            mediaMuxer.writeSampleData(muxerTrackIndex, buffer, info);
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

    @RequiresApi(
            api = 18
    )
    private long writeAudioTrack(MediaExtractor extractor, MediaMuxer mediaMuxer, BufferInfo info, long start, long end, File file, int muxerTrackIndex) throws Exception {
        int trackIndex = this.selectTrack(extractor, true);
        if (trackIndex < 0) {
            return -1L;
        } else {
            extractor.selectTrack(trackIndex);
            MediaFormat trackFormat = extractor.getTrackFormat(trackIndex);
            int maxBufferSize = trackFormat.getInteger("max-input-size");
            boolean inputDone = false;
            if (start > 0L) {
                extractor.seekTo(start, 0);
            } else {
                extractor.seekTo(0L, 0);
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);
            long startTime = -1L;

            while (!inputDone) {
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
                            mediaMuxer.writeSampleData(muxerTrackIndex, buffer, info);
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

    private int selectTrack(MediaExtractor extractor, boolean audio) {
        int numTracks = extractor.getTrackCount();

        for (int i = 0; i < numTracks; ++i) {
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

        return -233;
    }

    @RequiresApi(
            api = 18
    )
    private void prepareEncoder(MediaFormat inputFormat) {
        this.mBufferInfo = new BufferInfo();
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", this.mWidth, this.mHeight);
        format.setInteger("color-format", 2130708361);
        format.setInteger("bitrate", this.mBitRate);
        format.setInteger("frame-rate", FRAME_RATE);
        format.setInteger("i-frame-interval", IFRAME_INTERVAL);
        RLog.d("VideoSlimEncoder", "format: " + format);

        try {
            this.mEncoder = MediaCodec.createEncoderByType("video/avc");
        } catch (IOException var5) {
            RLog.d("VideoSlimEncoder", "prepareEncoder:" + var5);
        }

        this.mEncoder.configure(format, (Surface) null, (MediaCrypto) null, 1);
        this.mInputSurface = new CodecInputSurface(this.mEncoder.createInputSurface());
        this.mInputSurface.makeCurrent();
        this.mEncoder.start();

        try {
            this.mDecoder = MediaCodec.createDecoderByType(inputFormat.getString("mime"));
        } catch (IOException var4) {
            RLog.d("VideoSlimEncoder", "prepareEncoder:" + var4);
        }

        this.mInputSurface.createRender();
        this.mDecoder.configure(inputFormat, this.mInputSurface.getSurface(), (MediaCrypto) null, 0);
        this.mDecoder.start();
        this.mTrackIndex = -1;
    }

    @RequiresApi(
            api = 18
    )
    private boolean releaseCoder() {
        RLog.d("VideoSlimEncoder", "releasing encoder objects");
        if (this.mEncoder != null) {
            this.mEncoder.stop();
            this.mEncoder.release();
            this.mEncoder = null;
        }

        if (this.mDecoder != null) {
            this.mDecoder.stop();
            this.mDecoder.release();
            this.mDecoder = null;
        }

        if (this.mInputSurface != null) {
            this.mInputSurface.release();
            this.mInputSurface = null;
        }

        if (this.mMuxer != null) {
            boolean var2;
            try {
                this.mMuxer.stop();
                this.mMuxer.release();
                return true;
            } catch (Exception var6) {
                RLog.e("VideoSlimEncoder", "mMuxer fail");
                var2 = false;
            } finally {
                this.mMuxer = null;
            }

            return var2;
        } else {
            return true;
        }
    }
}