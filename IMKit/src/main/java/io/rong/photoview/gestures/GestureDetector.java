package io.rong.photoview.gestures;

import android.view.MotionEvent;

public abstract interface GestureDetector
{
  public abstract boolean onTouchEvent(MotionEvent paramMotionEvent);

  public abstract boolean isScaling();

  public abstract boolean isDragging();

  public abstract void setOnGestureListener(OnGestureListener paramOnGestureListener);
}