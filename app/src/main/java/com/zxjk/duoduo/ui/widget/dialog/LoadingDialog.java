package com.zxjk.duoduo.ui.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zxjk.duoduo.R;

import java.lang.ref.WeakReference;

public class LoadingDialog extends Dialog {
    public static final int MSG_SHOW = 1;
    public static final int MSG_HIDE = 2;
    public static final int MSG_REAL_HIDE = 3;

    private static int delayTimeStamp = 500;
    private static int showTimeStamp = 1000;

    private Handler mHandler;

    private Activity parent;

    public LoadingDialog(@NonNull Context context, String loadText) {
        super(context);
        mHandler = new Handler(this);
        parent = (Activity) context;
        setContentView(R.layout.dialog_loading);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!TextUtils.isEmpty(loadText)) {
            TextView tips = findViewById(R.id.tv_dialog_content);
            tips.setText(loadText);
        }
    }

    @Override
    public void show() {
        mHandler.sendEmptyMessageDelayed(MSG_SHOW, delayTimeStamp);
    }

    @Override
    public void dismiss() {
        mHandler.sendEmptyMessage(MSG_HIDE);
    }

    private void dismissReally() {
        if (parent != null && !parent.isFinishing()) {
            mHandler.removeCallbacksAndMessages(null);
            if (isShowing()) {
                super.dismiss();
            }
        }
    }

    private void showReally() {
        if (parent != null && !parent.isFinishing()) {
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
