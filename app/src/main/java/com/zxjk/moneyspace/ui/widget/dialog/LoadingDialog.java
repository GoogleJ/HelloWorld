package com.zxjk.moneyspace.ui.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zxjk.moneyspace.R;

import java.lang.ref.WeakReference;

public class LoadingDialog extends Dialog {
    private static final int MSG_SHOW = 1;
    private static final int MSG_HIDE = 2;
    private static final int MSG_REAL_HIDE = 3;

    private long delayTimeStamp = 400;
    private static int showTimeStamp = 700;

    private Handler mHandler;

    private WeakReference<Activity> parent;

    private TextView tips;

    public LoadingDialog(@NonNull Context context, String loadText) {
        super(context);
        parent = new WeakReference<>((Activity) context);
        mHandler = new Handler(this);
        setContentView(R.layout.dialog_loading);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setDimAmount(0f);
        tips = findViewById(R.id.tv_dialog_content);
        if (!TextUtils.isEmpty(loadText)) {
            tips.setVisibility(View.VISIBLE);
            tips.setText(loadText);
        } else {
            tips.setVisibility(View.GONE);
        }
    }

    public void setDelayTimeStamp(long delayTimeStamp) {
        this.delayTimeStamp = delayTimeStamp;
    }

    public void setText(String text) {
        tips.setText(text);
    }

    @Override
    public void show() {
        mHandler.sendEmptyMessageDelayed(MSG_SHOW, delayTimeStamp);
    }

    @Override
    public void dismiss() {
        mHandler.sendEmptyMessage(MSG_HIDE);
    }

    public void dismissReally() {
        mHandler.removeCallbacksAndMessages(null);
        if (isShowing()) {
            Activity activity = parent.get();
            if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                super.dismiss();
            }
        }
    }

    private void showReally() {
        Activity activity = parent.get();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            super.show();
        }
    }

    static class Handler extends android.os.Handler {
        private WeakReference<LoadingDialog> loadingDialog;
        private long lastShowTimeStamp;

        Handler(LoadingDialog dialog) {
            loadingDialog = new WeakReference<>(dialog);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            LoadingDialog dialog = loadingDialog.get();
            if (dialog == null) {
                return;
            }

            switch (msg.what) {
                case MSG_SHOW:
                    if (!dialog.isShowing()) {
                        dialog.showReally();
                        lastShowTimeStamp = System.currentTimeMillis();
                    }
                    break;
                case MSG_HIDE:
                    if (dialog.isShowing() && ((System.currentTimeMillis() - lastShowTimeStamp) < showTimeStamp)) {
                        sendEmptyMessageDelayed(MSG_REAL_HIDE, showTimeStamp + lastShowTimeStamp - System.currentTimeMillis());
                    } else {
                        dialog.dismissReally();
                    }
                    break;
                case MSG_REAL_HIDE:
                    if (dialog.isShowing()) {
                        dialog.dismissReally();
                    }
                    break;
            }
        }
    }

}
