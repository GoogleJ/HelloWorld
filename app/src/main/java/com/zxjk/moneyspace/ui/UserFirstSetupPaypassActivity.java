package com.zxjk.moneyspace.ui;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.widget.PayPsdInputView;

public class UserFirstSetupPaypassActivity extends BaseActivity {

    private TextView tvTips1;
    private TextView tvTips2;
    private PayPsdInputView payPsdInputView;

    private String newPwd;
    private String newPwdTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        BarUtils.setStatusBarVisibility(this, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        setContentView(R.layout.activity_user_first_setup_paypass);

        tvTips1 = findViewById(R.id.tvTips1);
        tvTips2 = findViewById(R.id.tvTips2);
        payPsdInputView = findViewById(R.id.m_set_payment_pwd_edit);

    }

    public void next(View view) {
        String pwd = payPsdInputView.getPasswordString();

        if (TextUtils.isEmpty(pwd) || pwd.length() != 6) {
            ToastUtils.showShort(R.string.input_pay_pwd);
            return;
        }

        if (TextUtils.isEmpty(newPwd)) {
            newPwd = pwd;
            payPsdInputView.cleanPsd();
            tvTips1.setText(R.string.verify_paypass);
            tvTips2.setText(R.string.verify_paypass1);
            return;
        }

        if (TextUtils.isEmpty(newPwdTwo)) {
            newPwdTwo = pwd;
            if (!newPwdTwo.equals(newPwd)) {
                tvTips1.setText(R.string.setup_paypass);
                tvTips2.setText(R.string.setup_paypass1);

                newPwdTwo = "";
                newPwd = "";

                payPsdInputView.cleanPsd();

                ToastUtils.showShort(R.string.passnotsame);
                return;
            }

            //todo call api
        }
    }
}
