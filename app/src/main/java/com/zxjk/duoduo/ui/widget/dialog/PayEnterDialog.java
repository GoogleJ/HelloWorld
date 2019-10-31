package com.zxjk.duoduo.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.utils.MoneyValueFilter;

public class PayEnterDialog extends Dialog {
    private View view;

    private ImageView ivRight;
    private EditText et;
    private TextView tvCancel;
    private TextView tvCommit;
    private TextView tvTitle;

    private OnCommitClick onCommitClick;

    public void setOnCommitClick(OnCommitClick onCommitClick) {
        this.onCommitClick = onCommitClick;
    }

    public interface OnCommitClick {
        void commit(String str);
    }

    public PayEnterDialog(@NonNull Context context) {
        super(context, R.style.dialogstyle);

        this.view = View.inflate(context, R.layout.layout_dialog_payenter, null);

        ivRight = view.findViewById(R.id.ivRight);
        et = view.findViewById(R.id.et);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvCommit = view.findViewById(R.id.tvCommit);
        tvTitle = view.findViewById(R.id.tvTitle);

        et.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(4)});

        ivRight.setOnClickListener(v -> dismiss());
        tvCancel.setOnClickListener(v -> dismiss());

        tvCommit.setOnClickListener(v -> {
            String s = et.getText().toString();
            if (TextUtils.isEmpty(s)) {
                ToastUtils.showShort(R.string.input_empty);
                return;
            }
            if (onCommitClick != null)
                onCommitClick.commit(s);
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

    public void show(String title) {
        tvTitle.setText(title);
        super.show();
    }

    @Override
    public void dismiss() {
        if (getOwnerActivity() != null) {
            KeyboardUtils.hideSoftInput(getOwnerActivity());
        } else {
            KeyboardUtils.hideSoftInput(view);
        }
        super.dismiss();
    }
}
