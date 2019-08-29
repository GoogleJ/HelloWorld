package com.zxjk.duoduo.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zxjk.duoduo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BaseAddTitleDialog extends Dialog implements View.OnClickListener {
    private View view;
    Context context;
    @BindView(R.id.order_being_traded_label)
    TextView dialogTitle;

    public BaseAddTitleDialog(@NonNull Context context) {
        super(context, R.style.dialogstyle);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_remove_group_chat, null);
        ButterKnife.bind(this, view);
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

    public void show(String title) {
        show();
        dialogTitle.setText(title);
    }


    @OnClick({R.id.determine_btn, R.id.cancel_btn})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.determine_btn:
                dismiss();
                if (onClickListener != null) {
                    onClickListener.determine();
                }
                break;
            case R.id.cancel_btn:
                dismiss();
                break;
            default:
                dismiss();
                break;
        }

    }

    public OnClickListener onClickListener;

    public interface OnClickListener {
        void determine();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
