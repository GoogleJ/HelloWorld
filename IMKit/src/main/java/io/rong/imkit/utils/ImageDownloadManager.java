//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utils;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;

public class ImageDownloadManager {
  private static final String TAG = ImageDownloadManager.class.getSimpleName();
  private static ImageDownloadManager instance;
  private static Context mContext;
  private DownloadManager downloadManager;
  private String savePath = "download";
  private long taskId;
  private String imageString;
  private ImageDownloadManager.DownloadStatusListener downloadStatusListener;
  private BroadcastReceiver receiver = new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
      ImageDownloadManager.this.checkDownloadStatus();
    }
  };

  private ImageDownloadManager() {
    if (mContext == null) {
      throw new NullPointerException("mContext is empty ,need invoke init!");
    } else {
      this.downloadManager = (DownloadManager)mContext.getSystemService("download");
    }
  }

  public static void init(Context context) {
    mContext = context;
  }

  public static ImageDownloadManager getInstance() {
    if (instance == null) {
      Class var0 = ImageDownloadManager.class;
      synchronized(ImageDownloadManager.class) {
        if (instance == null) {
          instance = new ImageDownloadManager();
        }
      }
    }

    return instance;
  }

  public void downloadImage(String remotePath, ImageDownloadManager.DownloadStatusListener downloadStatusListener) {
    if (TextUtils.isEmpty(remotePath) && downloadStatusListener == null) {
      throw new NullPointerException("parameter is error");
    } else {
      this.downloadStatusListener = downloadStatusListener;
      if (!this.canDownloadManagerState()) {
        Log.e(TAG, "DownloadManager is disable or Device ROM remove it");
        downloadStatusListener.downloadFailed(ImageDownloadManager.DownloadStatusError.DEVICE_DISABLE);
      } else {
        this.imageString = remotePath.substring(remotePath.length() - 7, remotePath.length());
        String path = "/storage/emulated/0/" + this.savePath + "/" + this.imageString + ".jpg";
        if ((new File(path)).exists()) {
          downloadStatusListener.downloadSuccess(path, (Bitmap)null);
        } else {
          try {
            Request request = new Request(Uri.parse(remotePath));
            request.setDestinationInExternalPublicDir(this.savePath, this.imageString + ".jpg");
            this.setTaskId(this.downloadManager.enqueue(request));
            mContext.registerReceiver(this.receiver, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));
          } catch (IllegalArgumentException var5) {
            Log.e(TAG, "Can only download HTTP/HTTPS URIs");
            downloadStatusListener.downloadFailed(ImageDownloadManager.DownloadStatusError.DOWNLOAD_FAILED);
          }

        }
      }
    }
  }

  private void checkDownloadStatus() {
    Query query = new Query();
    query.setFilterById(new long[]{this.getTaskId()});
    Cursor c = this.downloadManager.query(query);
    if (c.moveToFirst()) {
      int status = c.getInt(c.getColumnIndex("status"));
      switch(status) {
        case 1:
          Log.e(TAG, "STATUS_PENDING");
          break;
        case 2:
          Log.e(TAG, "STATUS_RUNNING");
          break;
        case 4:
          Log.e(TAG, "STATUS_PAUSED");
          break;
        case 8:
          String path = "/storage/emulated/0/" + this.savePath + "/" + this.imageString + ".jpg";
          this.downloadStatusListener.downloadSuccess("file://" + path, (Bitmap)null);
          mContext.unregisterReceiver(this.receiver);
          Log.e(TAG, "STATUS_SUCCESSFUL PATH: " + path);
          break;
        case 16:
          this.downloadStatusListener.downloadFailed(ImageDownloadManager.DownloadStatusError.DOWNLOAD_FAILED);
          Log.e(TAG, "STATUS_FAILED");
      }
    }

  }

  private boolean canDownloadManagerState() {
    try {
      int state = mContext.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
      return state != 2 && state != 3 && state != 4;
    } catch (Exception var2) {
      var2.printStackTrace();
      return false;
    }
  }

  private long getTaskId() {
    return this.taskId;
  }

  private void setTaskId(long taskId) {
    this.taskId = taskId;
  }

  public static enum DownloadStatusError {
    DEVICE_DISABLE,
    DOWNLOAD_FAILED;

    private DownloadStatusError() {
    }
  }

  public interface DownloadStatusListener {
    void downloadSuccess(String var1, Bitmap var2);

    void downloadFailed(ImageDownloadManager.DownloadStatusError var1);
  }
}