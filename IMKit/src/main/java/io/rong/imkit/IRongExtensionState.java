package io.rong.imkit;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import io.rong.imkit.emoticon.EmoticonTabAdapter;

public abstract interface IRongExtensionState
{
  public abstract void changeView(RongExtension paramRongExtension);

  public abstract void onClick(RongExtension paramRongExtension, View paramView);

  public abstract boolean onEditTextTouch(RongExtension paramRongExtension, View paramView, MotionEvent paramMotionEvent);

  public abstract void hideEmoticonBoard(ImageView paramImageView, EmoticonTabAdapter paramEmoticonTabAdapter);
}