//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utilities.videocompressor;

import android.annotation.TargetApi;
import android.media.MediaFormat;
import android.media.MediaCodec.BufferInfo;
import com.coremedia.iso.boxes.AbstractMediaHeaderBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SoundMediaHeaderBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.AudioSpecificConfig;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.SLConfigDescriptor;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@TargetApi(16)
public class Track {
  private static Map<Integer, Integer> samplingFrequencyIndexMap = new HashMap();
  private long trackId = 0L;
  private ArrayList<Sample> samples = new ArrayList();
  private long duration = 0L;
  private String handler;
  private AbstractMediaHeaderBox headerBox = null;
  private SampleDescriptionBox sampleDescriptionBox = null;
  private LinkedList<Integer> syncSamples = null;
  private int timeScale;
  private Date creationTime = new Date();
  private int height;
  private int width;
  private float volume = 0.0F;
  private ArrayList<Long> sampleDurations = new ArrayList();
  private boolean isAudio = false;
  private long lastPresentationTimeUs = 0L;
  private boolean first = true;

  public Track(int id, MediaFormat format, boolean isAudio) throws Exception {
    this.trackId = (long)id;
    if (!isAudio) {
      this.sampleDurations.add(3015L);
      this.duration = 3015L;
      this.width = format.getInteger("width");
      this.height = format.getInteger("height");
      this.timeScale = 90000;
      this.syncSamples = new LinkedList();
      this.handler = "vide";
      this.headerBox = new VideoMediaHeaderBox();
      this.sampleDescriptionBox = new SampleDescriptionBox();
      String mime = format.getString("mime");
      VisualSampleEntry visualSampleEntry;
      if (mime.equals("video/avc")) {
        visualSampleEntry = new VisualSampleEntry("avc1");
        visualSampleEntry.setDataReferenceIndex(1);
        visualSampleEntry.setDepth(24);
        visualSampleEntry.setFrameCount(1);
        visualSampleEntry.setHorizresolution(72.0D);
        visualSampleEntry.setVertresolution(72.0D);
        visualSampleEntry.setWidth(this.width);
        visualSampleEntry.setHeight(this.height);
        AvcConfigurationBox avcConfigurationBox = new AvcConfigurationBox();
        if (format.getByteBuffer("csd-0") != null) {
          ArrayList<byte[]> spsArray = new ArrayList();
          ByteBuffer spsBuff = format.getByteBuffer("csd-0");
          spsBuff.position(4);
          byte[] spsBytes = new byte[spsBuff.remaining()];
          spsBuff.get(spsBytes);
          spsArray.add(spsBytes);
          ArrayList<byte[]> ppsArray = new ArrayList();
          ByteBuffer ppsBuff = format.getByteBuffer("csd-1");
          ppsBuff.position(4);
          byte[] ppsBytes = new byte[ppsBuff.remaining()];
          ppsBuff.get(ppsBytes);
          ppsArray.add(ppsBytes);
          avcConfigurationBox.setSequenceParameterSets(spsArray);
          avcConfigurationBox.setPictureParameterSets(ppsArray);
        }

        avcConfigurationBox.setAvcLevelIndication(13);
        avcConfigurationBox.setAvcProfileIndication(100);
        avcConfigurationBox.setBitDepthLumaMinus8(-1);
        avcConfigurationBox.setBitDepthChromaMinus8(-1);
        avcConfigurationBox.setChromaFormat(-1);
        avcConfigurationBox.setConfigurationVersion(1);
        avcConfigurationBox.setLengthSizeMinusOne(3);
        avcConfigurationBox.setProfileCompatibility(0);
        visualSampleEntry.addBox(avcConfigurationBox);
        this.sampleDescriptionBox.addBox(visualSampleEntry);
      } else if (mime.equals("video/mp4v")) {
        visualSampleEntry = new VisualSampleEntry("mp4v");
        visualSampleEntry.setDataReferenceIndex(1);
        visualSampleEntry.setDepth(24);
        visualSampleEntry.setFrameCount(1);
        visualSampleEntry.setHorizresolution(72.0D);
        visualSampleEntry.setVertresolution(72.0D);
        visualSampleEntry.setWidth(this.width);
        visualSampleEntry.setHeight(this.height);
        this.sampleDescriptionBox.addBox(visualSampleEntry);
      }
    } else {
      this.sampleDurations.add(1024L);
      this.duration = 1024L;
      isAudio = true;
      this.volume = 1.0F;
      this.timeScale = format.getInteger("sample-rate");
      this.handler = "soun";
      this.headerBox = new SoundMediaHeaderBox();
      this.sampleDescriptionBox = new SampleDescriptionBox();
      AudioSampleEntry audioSampleEntry = new AudioSampleEntry("mp4a");
      audioSampleEntry.setChannelCount(format.getInteger("channel-count"));
      audioSampleEntry.setSampleRate((long)format.getInteger("sample-rate"));
      audioSampleEntry.setDataReferenceIndex(1);
      audioSampleEntry.setSampleSize(16);
      ESDescriptorBox esds = new ESDescriptorBox();
      ESDescriptor descriptor = new ESDescriptor();
      descriptor.setEsId(0);
      SLConfigDescriptor slConfigDescriptor = new SLConfigDescriptor();
      slConfigDescriptor.setPredefined(2);
      descriptor.setSlConfigDescriptor(slConfigDescriptor);
      DecoderConfigDescriptor decoderConfigDescriptor = new DecoderConfigDescriptor();
      decoderConfigDescriptor.setObjectTypeIndication(64);
      decoderConfigDescriptor.setStreamType(5);
      decoderConfigDescriptor.setBufferSizeDB(1536);
      decoderConfigDescriptor.setMaxBitRate(96000L);
      decoderConfigDescriptor.setAvgBitRate(96000L);
      AudioSpecificConfig audioSpecificConfig = new AudioSpecificConfig();
      audioSpecificConfig.setAudioObjectType(2);
      audioSpecificConfig.setSamplingFrequencyIndex((Integer)samplingFrequencyIndexMap.get((int)audioSampleEntry.getSampleRate()));
      audioSpecificConfig.setChannelConfiguration(audioSampleEntry.getChannelCount());
      decoderConfigDescriptor.setAudioSpecificInfo(audioSpecificConfig);
      descriptor.setDecoderConfigDescriptor(decoderConfigDescriptor);
      ByteBuffer data = descriptor.serialize();
      esds.setEsDescriptor(descriptor);
      esds.setData(data);
      audioSampleEntry.addBox(esds);
      this.sampleDescriptionBox.addBox(audioSampleEntry);
    }

  }

  public long getTrackId() {
    return this.trackId;
  }

  public void addSample(long offset, BufferInfo bufferInfo) {
    boolean isSyncFrame = !this.isAudio && (bufferInfo.flags & 1) != 0;
    this.samples.add(new Sample(offset, (long)bufferInfo.size));
    if (this.syncSamples != null && isSyncFrame) {
      this.syncSamples.add(this.samples.size());
    }

    long delta = bufferInfo.presentationTimeUs - this.lastPresentationTimeUs;
    this.lastPresentationTimeUs = bufferInfo.presentationTimeUs;
    delta = (delta * (long)this.timeScale + 500000L) / 1000000L;
    if (!this.first) {
      this.sampleDurations.add(this.sampleDurations.size() - 1, delta);
      this.duration += delta;
    }

    this.first = false;
  }

  public ArrayList<Sample> getSamples() {
    return this.samples;
  }

  public long getDuration() {
    return this.duration;
  }

  public String getHandler() {
    return this.handler;
  }

  public AbstractMediaHeaderBox getMediaHeaderBox() {
    return this.headerBox;
  }

  public SampleDescriptionBox getSampleDescriptionBox() {
    return this.sampleDescriptionBox;
  }

  public long[] getSyncSamples() {
    if (this.syncSamples != null && !this.syncSamples.isEmpty()) {
      long[] returns = new long[this.syncSamples.size()];

      for(int i = 0; i < this.syncSamples.size(); ++i) {
        returns[i] = (long)(Integer)this.syncSamples.get(i);
      }

      return returns;
    } else {
      return null;
    }
  }

  public int getTimeScale() {
    return this.timeScale;
  }

  public Date getCreationTime() {
    return this.creationTime;
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  public float getVolume() {
    return this.volume;
  }

  public ArrayList<Long> getSampleDurations() {
    return this.sampleDurations;
  }

  public boolean isAudio() {
    return this.isAudio;
  }

  static {
    samplingFrequencyIndexMap.put(96000, 0);
    samplingFrequencyIndexMap.put(88200, 1);
    samplingFrequencyIndexMap.put(64000, 2);
    samplingFrequencyIndexMap.put(48000, 3);
    samplingFrequencyIndexMap.put(44100, 4);
    samplingFrequencyIndexMap.put(32000, 5);
    samplingFrequencyIndexMap.put(24000, 6);
    samplingFrequencyIndexMap.put(22050, 7);
    samplingFrequencyIndexMap.put(16000, 8);
    samplingFrequencyIndexMap.put(12000, 9);
    samplingFrequencyIndexMap.put(11025, 10);
    samplingFrequencyIndexMap.put(8000, 11);
  }
}