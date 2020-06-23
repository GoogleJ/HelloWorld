package com.zxjk.duoduo.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.utils.EmojiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PaymentTypeDialog extends Dialog implements View.OnClickListener {
    public OnClickListener onClickListener;
    public OnStartActivity onStartActivity;
    Context context;

    @BindView(R.id.dialog_title)
    TextView dialogTitle;
    @BindView(R.id.edit_information)
    EditText editInformation;
    @BindView(R.id.tvContrary)
    TextView tvContrary;
    @BindView(R.id.llContrary)
    LinearLayout llcontrary;


    String wechat = "WEIXIN";
    String alipay = "ALIPAY";
    String bank = "EBANK";
    String mobile = "MOBILE ";
    private View view;
    private int s;

    public PaymentTypeDialog(@NonNull Context context) {

        super(context, R.style.dialogstyle);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_payment_type, null);
        ButterKnife.bind(this, view);
    }

    public void show(String title, String hint, String type, int s) {
        show();
        this.s = s;
        dialogTitle.setText(title);
        if (wechat.equals(type)) {
            //微信的
            editInformation.setHint(hint);
        } else if (alipay.equals(type)) {
            //支付宝的
            editInformation.setHint(hint);
            editInformation.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        } else if (bank.equals(type)) {
            //银行卡号
            editInformation.setHint(hint);
            editInformation.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (mobile.equals(type)) {
            editInformation.setHint(hint);
            editInformation.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            //开户银行
            editInformation.setHint(hint);
            editInformation.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        }
        editInformation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EmojiUtils.containsEmoji(s.toString())) {
                    ToastUtils.showShort("禁止输入表情");
                    editInformation.setText("");
                }
            }
        });
    }

    public String getContrary() {
        return tvContrary.getText().toString();
    }

    public void setContrary(String s) {
        tvContrary.setText(s);
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

    @OnClick({R.id.cancel_btn, R.id.determine_btn, R.id.llContrary})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                editInformation.setText("");
                dismiss();
                break;
            case R.id.determine_btn:
                if (onClickListener != null) {
                    onClickListener.determine(editInformation.getText().toString(), s);
                    editInformation.setText("");
                }
                break;
            case R.id.llContrary:
                onStartActivity.start();
                break;
            default:
                dismiss();
                break;
        }
    }

    public void setVisibilitys() {
        llcontrary.setVisibility(View.VISIBLE);
    }

    public void setOnStartActivity(OnStartActivity onStartActivity) {
        this.onStartActivity = onStartActivity;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public interface OnStartActivity {
        void start();
    }

    public interface OnClickListener {
        void determine(String editContent, int s);
    }


}
