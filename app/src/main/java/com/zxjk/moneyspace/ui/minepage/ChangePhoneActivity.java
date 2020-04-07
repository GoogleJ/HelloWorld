package com.zxjk.moneyspace.ui.minepage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.CountryEntity;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.CountrySelectActivity;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.MMKVUtils;

@SuppressLint({"CheckResult", "SetTextI18n"})
public class ChangePhoneActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_phone;
    private EditText et_verificationCode;
    private TextView tv_verificationCode;
    private String verify;
    private LinearLayout llContrary;
    private TextView tv_countryCode;
    private String isChinaPhone;


    CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            long time = millisUntilFinished / 1000;
            tv_verificationCode.setText(time + getString(R.string.regain_after_seconds));
            tv_verificationCode.setClickable(false);
        }

        @Override
        public void onFinish() {
            tv_verificationCode.setText(getString(R.string.regain_verification_code));
            tv_verificationCode.setClickable(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.change_phone_number));
        et_phone = findViewById(R.id.et_phone);
        et_verificationCode = findViewById(R.id.et_verificationCode);
        tv_verificationCode = findViewById(R.id.tv_verificationCode);
        tv_countryCode = findViewById(R.id.tv_countryCode);
        tv_verificationCode.setOnClickListener(this);
        llContrary = findViewById(R.id.ll_contrary);
        llContrary.setOnClickListener(v -> startActivityForResult(new Intent(this, CountrySelectActivity.class), 200));
//        tv_countryCode.setText("+" + Constant.HEAD_LOCATION);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {
            CountryEntity countryEntity = (CountryEntity) data.getSerializableExtra("country");
            tv_countryCode.setText("+" + (countryEntity != null ? countryEntity.countryCode : "86"));
            if (countryEntity != null) {
                Constant.HEAD_LOCATION = countryEntity.countryCode;
            }
        }
    }
    private void doChangePhone(String verifyCode) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .updateMobile(verify, verifyCode)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ChangePhoneActivity.this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> {
                    Constant.currentUser.setMobile(verify);
                    MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                    ToastUtils.showShort(getString(R.string.successfully_modified));
                    finish();
                }, this::handleApiError);
    }

    @Override
    public void onClick(View v) {
        String phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShort(getString(R.string.please_enter_phone_number));
            return;
        }
        if ("86".equals(tv_countryCode.getText().toString().substring(1))){
            isChinaPhone = "1";
            verify = phone;
        } else {
            isChinaPhone = "0";
            verify = tv_countryCode.getText().toString().substring(1) + phone;
        }
        if ("0".equals(isChinaPhone) || ("1".equals(isChinaPhone) && RegexUtils.isMobileExact(verify))) {
            getVerifyCode();
            tv_verificationCode.setClickable(false);
            return;
        }
        ToastUtils.showShort(getString(R.string.please_enter_a_valid_phone_number));
    }

    private void getVerifyCode() {
        timer.start();
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getCode(verify, isChinaPhone)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ChangePhoneActivity.this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(s -> ToastUtils.showShort(getString(R.string.the_verification_code_has_been_sent_please_check)), t -> {
                    handleApiError(t);
                    timer.cancel();
                    timer.onFinish();
                });
    }

    //完成点击事件
    public void accomplish(View view) {
        String phone = et_phone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShort(getString(R.string.please_enter_phone_number));
            return;
        }

        String verifyCode = et_verificationCode.getText().toString();
        if (TextUtils.isEmpty(verifyCode)) {
            ToastUtils.showShort(getString(R.string.please_enter_verification_code));
            return;
        }

        if ("1".equals(isChinaPhone) && !RegexUtils.isMobileExact(phone)) {
            ToastUtils.showShort(getString(R.string.please_enter_a_valid_phone_number));
            return;
        }
        if (!"86".equals(tv_countryCode.getText().toString().substring(1))){
            phone = tv_countryCode.getText().toString().substring(1) + phone;
        }
        if (!phone.equals(verify)) {
            ToastUtils.showShort(getString(R.string.verification_code_error));
            return;
        }
        doChangePhone(verifyCode);
    }
}
