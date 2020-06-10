//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.voiceMessageDownload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AutoDownloadNetWorkChangeReceiver extends BroadcastReceiver {
  public AutoDownloadNetWorkChangeReceiver() {
  }

  public void onReceive(Context context, Intent intent) {
    ConnectivityManager cm = (ConnectivityManager)context.getSystemService("connectivity");
    NetworkInfo networkInfo = null;

    try {
      networkInfo = cm.getActiveNetworkInfo();
    } catch (Exception var6) {
      var6.printStackTrace();
    }

    boolean networkAvailable = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction()) && networkAvailable) {
      HQVoiceMsgDownloadManager.getInstance().resumeDownloadService();
    } else {
      HQVoiceMsgDownloadManager.getInstance().pauseDownloadService();
    }

  }
}