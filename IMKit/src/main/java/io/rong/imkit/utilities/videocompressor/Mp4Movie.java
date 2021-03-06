package io.rong.imkit.utilities.videocompressor;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaFormat;

import com.googlecode.mp4parser.util.Matrix;

import java.io.File;
import java.util.ArrayList;

@TargetApi(16)
public class Mp4Movie
{
  private Matrix matrix;
  private ArrayList<Track> tracks;
  private File cacheFile;
  private int width;
  private int height;

  public Mp4Movie()
  {
    this.matrix = Matrix.ROTATE_0;
    this.tracks = new ArrayList();
  }

  public Matrix getMatrix()
  {
    return this.matrix;
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  public void setRotation(int angle) {
    if (angle == 0)
      this.matrix = Matrix.ROTATE_0;
    else if (angle == 90)
      this.matrix = Matrix.ROTATE_90;
    else if (angle == 180)
      this.matrix = Matrix.ROTATE_180;
    else if (angle == 270)
      this.matrix = Matrix.ROTATE_270;
  }

  public void setSize(int w, int h)
  {
    this.width = w;
    this.height = h;
  }

  public ArrayList<Track> getTracks() {
    return this.tracks;
  }

  public File getCacheFile() {
    return this.cacheFile;
  }

  public void setCacheFile(File file) {
    this.cacheFile = file;
  }

  public void addSample(int trackIndex, long offset, MediaCodec.BufferInfo bufferInfo) throws Exception {
    if ((trackIndex < 0) || (trackIndex >= this.tracks.size()))
      return;

    Track track = (Track)this.tracks.get(trackIndex);
    track.addSample(offset, bufferInfo);
  }

  public int addTrack(MediaFormat mediaFormat, boolean isAudio) throws Exception {
    this.tracks.add(new Track(this.tracks.size(), mediaFormat, isAudio));
    return (this.tracks.size() - 1);
  }
}