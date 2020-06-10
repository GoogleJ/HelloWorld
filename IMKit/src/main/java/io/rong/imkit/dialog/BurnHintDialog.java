//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import io.rong.common.RLog;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.utilities.RongUtils;

public class BurnHintDialog extends DialogFragment implements OnClickListener {
  private static final String TAG = "BurnHintDialog";
  protected Dialog mDialog;
  protected View mRootView;
  private static boolean isFirstClick = false;
  private TextView mConfirm;

  public BurnHintDialog() {
  }

  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setStyle(1, 16973940);
  }

  @Nullable
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    this.mRootView = inflater.inflate(layout.rc_dialog_fire_hint, container, false);
    this.initView();
    return this.mRootView;
  }

  protected void initView() {
    this.mConfirm = (TextView)this.mRootView.findViewById(id.tv_confirm);
    this.mConfirm.setOnClickListener(this);
  }

  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    this.mDialog = this.getDialog();
    if (this.mDialog != null) {
      Window dialogWindow = this.mDialog.getWindow();
      dialogWindow.setBackgroundDrawableResource(17170445);
      DisplayMetrics dm = new DisplayMetrics();
      this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
      dialogWindow.setLayout((int)((float)dm.widthPixels * this.getScreenWidthProportion()), -2);
      LayoutParams attributes = dialogWindow.getAttributes();
      attributes.gravity = this.getGravity();
      attributes.x = -RongUtils.dip2px(this.getHorizontalMovement());
      attributes.y = RongUtils.dip2px(this.getVerticalMovement());
      dialogWindow.setAttributes(attributes);
    }

  }

  protected int getGravity() {
    return 17;
  }

  protected float getScreenWidthProportion() {
    return 0.75F;
  }

  protected float getVerticalMovement() {
    return 0.0F;
  }

  protected float getHorizontalMovement() {
    return 0.0F;
  }

  public void show(FragmentManager manager) {
    try {
      this.show(manager, "");
      this.setCancelable(false);
      if (this.mDialog != null) {
        this.mDialog.setCanceledOnTouchOutside(false);
      }
    } catch (IllegalStateException var3) {
      RLog.e("BurnHintDialog", "show", var3);
    }

  }

  public void onClick(View v) {
    int i = v.getId();
    if (i == id.tv_confirm) {
      SharedPreferences sp = v.getContext().getSharedPreferences("RongKitConfig", 0);
      sp.edit().putBoolean("isFirstUseBurn", true).apply();
      this.hideDialog();
    }

  }

  public void hideDialog() {
    this.dismissAllowingStateLoss();
  }

  public static boolean isFirstClick(Context pContext) {
    if (!isFirstClick) {
      SharedPreferences sp = pContext.getSharedPreferences("RongKitConfig", 0);
      isFirstClick = sp.getBoolean("isFirstUseBurn", false);
    }

    return isFirstClick;
  }
}
