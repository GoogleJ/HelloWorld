package com.zxjk.moneyspace.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.widget.KeyboardPopupWindow;
import com.zxjk.moneyspace.ui.widget.PayPsdInputView;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.MD5Utils;

import butterknife.ButterKnife;

public class SettingPayPwdActivity extends BaseActivity {
    PayPsdInputView payPsdInputView;
    TextView commmitBtn;
    LinearLayout rootView;
    TextView m_set_payment_pwd_label;
    String newPwd;
    String newPwdTwo;
    KeyboardPopupWindow popupWindow;
    private boolean isUiCreated = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pay_pwd);
        ButterKnife.bind(this);
        initUI();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    private void initUI() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.set_pay_password));
        m_set_payment_pwd_label = findViewById(R.id.m_set_payment_pwd_label);
        rootView = findViewById(R.id.root_view);
        payPsdInputView = findViewById(R.id.m_set_payment_pwd_edit);
        commmitBtn = findViewById(R.id.m_edit_information_btn);
        m_set_payment_pwd_label.setText(R.string.input_newpaypwd);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        payPsdInputView.setComparePassword(new PayPsdInputView.onPasswordListener() {

            @Override
            public void onDifference(String oldPsd, String newPsd) {
            }


            @Override
            public void onEqual(String psd) {
            }

            @Override
            public void inputFinished(String inputPsd) {
                if (TextUtils.isEmpty(newPwd)) {
                    payPsdInputView.cleanPsd();
                    newPwd = inputPsd;
                    m_set_payment_pwd_label.setText(R.string.please_set_paypass_twtice);
                    return;
                }

                if (TextUtils.isEmpty(newPwdTwo)) {
                    newPwdTwo = inputPsd;
                }

                if (!newPwd.equals(newPwdTwo)) {
                    payPsdInputView.cleanPsd();
                    ToastUtils.showShort(R.string.passnotsame);
                    newPwdTwo = "";
                    newPwd = "";
                    m_set_payment_pwd_label.setText(R.string.please_set_paypass1);
                    return;
                }

                m_set_payment_pwd_label.setText(R.string.please_set_paypass2);
                commmitBtn.setVisibility(View.VISIBLE);
            }
        });
        commmitBtn.setOnClickListener(v -> {
            Intent intent = getIntent();
            String number = intent.getStringExtra("idCardEdit");
            String verifiedCodeEdit = intent.getStringExtra("verifiedCodeEdit");
            settingPayPwd(number, verifiedCodeEdit, MD5Utils.getMD5(newPwd), MD5Utils.getMD5(newPwdTwo));
        });
        popupWindow = new KeyboardPopupWindow(this, getWindow().getDecorView(), payPsdInputView, false);
        payPsdInputView.setOnClickListener(v -> {
            if (popupWindow != null) {
                popupWindow.show();
            }
        });
        payPsdInputView.setOnFocusChangeListener((v, hasFocus) -> {
            if (popupWindow != null && isUiCreated) {
                popupWindow.refreshKeyboardOutSideTouchable(!hasFocus);
            }
            //隐藏系统软键盘
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(payPsdInputView.getWindowToken(), 0);
            }
        });
    }


    @SuppressLint("CheckResult")
    public void settingPayPwd(String number, String securityCode, String newPwd, String newPwdTwo) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .fandPayPwd(number, securityCode, newPwd, newPwdTwo)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    ToastUtils.showShort(SettingPayPwdActivity.this.getString(R.string.successfully_modified));
                    SettingPayPwdActivity.this.finish();
                }, this::handleApiError);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        isUiCreated = true;
    }

    @Override
    protected void onDestroy() {
        if (popupWindow != null) {
            popupWindow.releaseResources();
        }
        super.onDestroy();
    }


}
