package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MMKVUtils;

@SuppressLint({"CheckResult", "SetTextI18n"})
public class ChangePhoneActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_phone;
    private EditText et_verificationCode;
    private TextView tv_verificationCode;
    private String verify;

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
        TextView tv_countryCode = findViewById(R.id.tv_countryCode);
        tv_verificationCode.setOnClickListener(this);
        tv_countryCode.setText("+" + Constant.HEAD_LOCATION);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
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
        if (RegexUtils.isMobileExact(phone)) {
            verify = phone;
            getVerifyCode();
            tv_verificationCode.setClickable(false);
            return;
        }
        ToastUtils.showShort(getString(R.string.please_enter_a_valid_phone_number));
    }

    private void getVerifyCode() {
        timer.start();
        ServiceFactory.getInstance().getBaseService(Api.class)
                .getCode(Constant.HEAD_LOCATION + "-" + verify, "0")
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

        if (!RegexUtils.isMobileExact(phone)) {
            ToastUtils.showShort(getString(R.string.please_enter_a_valid_phone_number));
            return;
        }

        if (!phone.equals(verify)) {
            ToastUtils.showShort(getString(R.string.verification_code_error));
            return;
        }
        doChangePhone(verifyCode);
    }
}
