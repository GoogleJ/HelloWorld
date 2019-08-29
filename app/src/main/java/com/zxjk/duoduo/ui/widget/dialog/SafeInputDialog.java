package com.zxjk.duoduo.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.widget.PayPsdInputView;
import androidx.annotation.NonNull;

public class SafeInputDialog extends Dialog {

    private ImageView ivDelete;
    private TextView tvSafeInputMoney;
    private PayPsdInputView etSafePsy;

    private OnFinishListener onFinishListener;

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    public interface OnFinishListener {
        void onFinish(String psd);
    }

    public SafeInputDialog(@NonNull Context context) {
        super(context);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_safeinput, null);

        ivDelete = inflate.findViewById(R.id.ivDelete);
        tvSafeInputMoney = inflate.findViewById(R.id.tvSafeInputMoney);
        etSafePsy = inflate.findViewById(R.id.etSafePsy);

        ivDelete.setOnClickListener(v -> dismiss());

        etSafePsy.setComparePassword(new PayPsdInputView.onPasswordListener() {
            @Override
            public void onDifference(String oldPsd, String newPsd) {

            }

            @Override
            public void onEqual(String psd) {

            }

            @Override
            public void inputFinished(String inputPsd) {
                dismiss();
                if (onFinishListener != null) {
                    onFinishListener.onFinish(inputPsd);
                }
            }
        });

        setContentView(inflate);

    }

    public void show(String money) {
        tvSafeInputMoney.setText(money);
        this.show();
    }
}
