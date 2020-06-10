package io.rong.imkit;

import android.net.Uri;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.LinkedHashMap;

import io.rong.imkit.plugin.IPluginModule;

public abstract interface IExtensionClickListener extends TextWatcher
{
  public abstract void onSendToggleClick(View paramView, String paramString);

  public abstract void onImageResult(LinkedHashMap<String, Integer> paramLinkedHashMap, boolean paramBoolean);

  public abstract void onLocationResult(double paramDouble1, double paramDouble2, String paramString, Uri paramUri);

  public abstract void onSwitchToggleClick(View paramView, ViewGroup paramViewGroup);

  public abstract void onVoiceInputToggleTouch(View paramView, MotionEvent paramMotionEvent);

  public abstract void onEmoticonToggleClick(View paramView, ViewGroup paramViewGroup);

  public abstract void onPluginToggleClick(View paramView, ViewGroup paramViewGroup);

  public abstract void onMenuClick(int paramInt1, int paramInt2);

  public abstract void onEditTextClick(EditText paramEditText);

  public abstract boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent);

  public abstract void onExtensionCollapsed();

  public abstract void onExtensionExpanded(int paramInt);

  public abstract void onPluginClicked(IPluginModule paramIPluginModule, int paramInt);

  public abstract void onPhrasesClicked(String paramString, int paramInt);
}