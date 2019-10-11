package com.zxjk.duoduo.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zxjk.duoduo.R;

public class MuteRemoveDialog extends Dialog {
    private View view;

    private TextView tvTitle;
    private TextView tvCancel;
    private TextView tvCommit;
    private TextView tvContent;
    private ImageView ivRight;

    public interface OnCommitListener {
        void commit();
    }

    public interface OnCancelListener {
        void cancel();
    }

    public interface OnCloseListener {
        void close();
    }

    private OnCommitListener onCommitListener;

    private OnCancelListener onCancelListener;

    private OnCloseListener onCloseListener;

    public void setOnCloseListener(OnCloseListener onCloseListener) {
        this.onCloseListener = onCloseListener;
    }

    public void setOnCommitListener(OnCommitListener onCommitListener) {
        this.onCommitListener = onCommitListener;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public MuteRemoveDialog(@NonNull Context context) {
        this(context, context.getString(R.string.delete_directly),
                context.getString(R.string.move_to_kickout),
                context.getString(R.string.choose_your_func),
                "");
    }

    public MuteRemoveDialog(@NonNull Context context, String cancel, String commit, String title, String content) {
        super(context, R.style.dialogstyle);
        this.view = View.inflate(context, R.layout.dialog_mute_remove, null);

        tvTitle = view.findViewById(R.id.tvTitle);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvCommit = view.findViewById(R.id.tvCommit);
        ivRight = view.findViewById(R.id.ivRight);
        tvContent = view.findViewById(R.id.tvContent);

        if (!TextUtils.isEmpty(content)) {
            tvContent.setText(content);
            tvContent.setVisibility(View.VISIBLE);
        }

//        ivRight.setVisibility(View.VISIBLE);

        tvCancel.setTextColor(Color.parseColor("#FC6660"));

        tvCancel.setText(cancel);
        tvCommit.setText(commit);
        tvTitle.setText(title);

//        ivRight.setOnClickListener(v -> {
//            dismiss();
//            if (onCloseListener != null) onCloseListener.close();
//        });

        tvCancel.setOnClickListener(v -> {
            dismiss();
            if (onCancelListener != null)
                onCancelListener.cancel();
        });

        tvCommit.setOnClickListener(v -> {
            dismiss();
            if (onCommitListener != null)
                onCommitListener.commit();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
    }
}
