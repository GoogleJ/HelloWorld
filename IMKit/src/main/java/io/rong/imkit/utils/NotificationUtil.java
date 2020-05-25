//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Build.VERSION;
import io.rong.common.RLog;
import java.lang.reflect.Method;

public class NotificationUtil {
  private static final String TAG = NotificationUtil.class.getSimpleName();
  private static final String CHANNEL_ID = "rc_notification_id";

  public NotificationUtil() {
  }

  public static void showNotification(Context context, String title, String content, PendingIntent intent, int notificationId, int defaults) {
    Notification notification = createNotification(context, title, content, intent, defaults);
    NotificationManager nm = (NotificationManager)context.getSystemService("notification");
    if (VERSION.SDK_INT >= 26) {
      int importance = 3;
      String channelName = context.getResources().getString(context.getResources().getIdentifier("rc_notification_channel_name", "string", context.getPackageName()));
      NotificationChannel notificationChannel = new NotificationChannel("rc_notification_id", channelName, importance);
      notificationChannel.enableLights(true);
      notificationChannel.setLightColor(-16711936);
      if (notification != null && notification.sound != null) {
        notificationChannel.setSound(notification.sound, (AudioAttributes)null);
      }

      nm.createNotificationChannel(notificationChannel);
    }

    if (notification != null) {
      nm.notify(notificationId, notification);
    }

  }

  public static void showNotification(Context context, String title, String content, PendingIntent intent, int notificationId) {
    showNotification(context, title, content, intent, notificationId, -1);
  }

  public static void clearNotification(Context context, int notificationId) {
    NotificationManager nm = (NotificationManager)context.getSystemService("notification");
    nm.cancel(notificationId);
  }

  private static Notification createNotification(Context context, String title, String content, PendingIntent pendingIntent, int defaults) {
    String tickerText = context.getResources().getString(context.getResources().getIdentifier("rc_notification_ticker_text", "string", context.getPackageName()));
    Notification notification;
    if (VERSION.SDK_INT < 11) {
      try {
        notification = new Notification(context.getApplicationInfo().icon, tickerText, System.currentTimeMillis());
        Class<?> classType = Notification.class;
        Method method = classType.getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
        method.invoke(notification, context, title, content, pendingIntent);
        notification.flags = 48;
        notification.defaults = -1;
      } catch (Exception var12) {
        RLog.e(TAG, "createNotification", var12);
        return null;
      }
    } else {
      boolean isLollipop = VERSION.SDK_INT >= 21;
      int smallIcon = context.getResources().getIdentifier("notification_small_icon", "drawable", context.getPackageName());
      if (smallIcon <= 0 || !isLollipop) {
        smallIcon = context.getApplicationInfo().icon;
      }

      Drawable loadIcon = context.getApplicationInfo().loadIcon(context.getPackageManager());
      Bitmap appIcon = null;

      try {
        if (VERSION.SDK_INT >= 26 && loadIcon instanceof AdaptiveIconDrawable) {
          appIcon = Bitmap.createBitmap(loadIcon.getIntrinsicWidth(), loadIcon.getIntrinsicHeight(), Config.ARGB_8888);
          Canvas canvas = new Canvas(appIcon);
          loadIcon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
          loadIcon.draw(canvas);
        } else {
          appIcon = ((BitmapDrawable)loadIcon).getBitmap();
        }
      } catch (Exception var13) {
        RLog.e(TAG, "createNotification", var13);
      }

      Builder builder = new Builder(context);
      builder.setLargeIcon(appIcon);
      builder.setSmallIcon(smallIcon);
      builder.setTicker(tickerText);
      builder.setContentTitle(title);
      builder.setContentText(content);
      builder.setContentIntent(pendingIntent);
      builder.setAutoCancel(true);
      builder.setOngoing(true);
      builder.setDefaults(defaults);
      if (VERSION.SDK_INT >= 26) {
        builder.setChannelId("rc_notification_id");
      }

      notification = builder.getNotification();
    }

    return notification;
  }

  public static int getRingerMode(Context context) {
    AudioManager audio = (AudioManager)context.getSystemService("audio");
    return audio.getRingerMode();
  }
}