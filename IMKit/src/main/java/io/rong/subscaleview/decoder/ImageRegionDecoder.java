package io.rong.subscaleview.decoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;

public abstract interface ImageRegionDecoder
{
  public abstract Point init(Context paramContext, Uri paramUri)
    throws Exception;

  public abstract Bitmap decodeRegion(Rect paramRect, int paramInt);

  public abstract boolean isReady();

  public abstract void recycle();
}