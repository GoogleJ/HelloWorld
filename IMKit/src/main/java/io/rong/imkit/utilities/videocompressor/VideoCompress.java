//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utilities.videocompressor;

import android.os.AsyncTask;
import io.rong.imkit.utilities.videocompressor.VideoController.CompressProgressListener;

public class VideoCompress {
  private static final String TAG = VideoCompress.class.getSimpleName();

  public VideoCompress() {
  }

  public static VideoCompress.VideoCompressTask compressVideo(String srcPath, String destPath, VideoCompress.CompressListener listener) {
    VideoCompress.VideoCompressTask task = new VideoCompress.VideoCompressTask(listener);
    task.execute(new String[]{srcPath, destPath});
    return task;
  }

  public interface CompressListener {
    void onStart();

    void onSuccess();

    void onFail();

    void onProgress(float var1);
  }

  private static class VideoCompressTask extends AsyncTask<String, Float, Boolean> {
    private VideoCompress.CompressListener mListener;

    public VideoCompressTask(VideoCompress.CompressListener listener) {
      this.mListener = listener;
    }

    protected void onPreExecute() {
      super.onPreExecute();
      if (this.mListener != null) {
        this.mListener.onStart();
      }

    }

    protected Boolean doInBackground(String... paths) {
      return VideoController.getInstance().convertVideo(paths[0], paths[1], new CompressProgressListener() {
        public void onProgress(float percent) {
          VideoCompressTask.this.publishProgress(new Float[]{percent});
        }
      });
    }

    protected void onProgressUpdate(Float... percent) {
      super.onProgressUpdate(percent);
      if (this.mListener != null) {
        this.mListener.onProgress(percent[0]);
      }

    }

    protected void onPostExecute(Boolean result) {
      super.onPostExecute(result);
      if (this.mListener != null) {
        if (result) {
          this.mListener.onSuccess();
        } else {
          this.mListener.onFail();
        }
      }

    }
  }
}