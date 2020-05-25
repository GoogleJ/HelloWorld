package io.rong.imkit.widget;

import android.net.Uri;

public abstract interface IImageLoadingListener
{
  public abstract void onLoadingComplete(Uri paramUri);

  public abstract void onLoadingFail();
}