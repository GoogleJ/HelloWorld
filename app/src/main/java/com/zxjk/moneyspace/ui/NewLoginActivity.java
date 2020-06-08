package com.zxjk.moneyspace.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.CountryEntity;
import com.zxjk.moneyspace.bean.response.LoginResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.MMKVUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class NewLoginActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvChangeLanguage;
    private LinearLayout llContrary;
    private TextView tvContrary;
    private EditText etPhone;
    private LinearLayout llpwd;
    private EditText etPwd;
    private TextView tvLogin;
    private TextView tv_loginType;
    private TextView tv_pwdLogin;
    private String phone;
    private boolean isPwd = true;
    private TextView tv_forget_the_password;
    private LinearLayout ll_code;
    private EditText etCode;
    private TextView tvCode;
    private ImageView ivIcon;
    private TextView tv1;
    private TextView tv2;

    /**
     * 0：国外
     * 1：国内
     */
    private String isChinaPhone;

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
        setContentView(R.layout.activity_new_login);

        initView();

        initData();
    }

    @SuppressLint("CheckResult")
    private void initData() {
        tvChangeLanguage.setOnClickListener(v -> {
//            if ("english".equals(LanguageUtil.getInstance(this).getCurrentLanguage())) {
//                LanguageUtil.getInstance(this).changeLanguage(LanguageUtil.CHINESE);
//            } else {
//                LanguageUtil.getInstance(this).changeLanguage(LanguageUtil.ENGLISH);
//            }
//            finish();
//            startActivity(new Intent(this, NewLoginActivity.class));
            ToastUtils.showShort(R.string.developing);
        });

        llContrary.setOnClickListener(v -> startActivityForResult(new Intent(this, CountrySelectActivity.class), 200));

        ivBack.setOnClickListener(v -> {
            if (tvLogin.getText().equals(R.string.m_edit_information_btn)) {
                ll_code.setVisibility(View.GONE);
                tvLogin.setText(R.string.login);
                llpwd.setVisibility(View.VISIBLE);
                tv_loginType.setVisibility(View.VISIBLE);
                tv_pwdLogin.setVisibility(View.GONE);
                tv_forget_the_password.setVisibility(View.VISIBLE);
                ivIcon.setVisibility(View.VISIBLE);
                tv1.setText(R.string.hello);
                tv2.setVisibility(View.VISIBLE);
            } else if (tvLogin.getText().equals(R.string.login)) {
                tv_loginType.setText(R.string.login_byemail);
                tv_pwdLogin.setVisibility(View.VISIBLE);
                llpwd.setVisibility(View.GONE);
                tvLogin.setText(R.string.getVerGode);
                tv_forget_the_password.setVisibility(View.GONE);
            } else {
                finish();
            }
        });
    }

    @SuppressLint("CheckResult")
    public void code(View view) {
        String phoneText = etPhone.getText().toString().trim();

        if ("86".equals(tvContrary.getText().toString().substring(1))) {
            isChinaPhone = "1";
            phone = phoneText;
        } else {
            isChinaPhone = "0";
            phone = tvContrary.getText().toString().substring(1) + phoneText;
        }

        if (TextUtils.isEmpty(phone) || ("1".equals(isChinaPhone) && !RegexUtils.isMobileExact(phone))) {
            ToastUtils.showShort(R.string.edit_mobile_tip);
            return;
        }
        if (tvLogin.getText().equals(getString(R.string.getVerGode))) {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .getCode(phone, isChinaPhone)
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(o -> {
                        Intent intent = new Intent(this, GetCodeActivity.class);
                        intent.putExtra("phone", phone);
                        intent.putExtra("isChinaPhone", isChinaPhone);
                        startActivity(intent);
                    }, this::handleApiError);
        } else if (ll_code.getVisibility() == View.VISIBLE) {
            if (TextUtils.isEmpty(etCode.getText().toString().trim())) {
                ToastUtils.showShort(R.string.please_enter_verification_code);
                return;
            }
            if (TextUtils.isEmpty(etPwd.getText().toString().trim())) {
                ToastUtils.showShort(R.string.enter_your_pwd);
                return;
            }
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .updatePassword(phone, "", etCode.getText().toString(), etPwd.getText().toString(), etPwd.getText().toString())
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                    .subscribe(o -> {
                        ToastUtils.showShort(R.string.password_changed_successfully);
                        finish();
                    }, this::handleApiError);
        } else {
            ServiceFactory.getInstance().getBaseService(Api.class)
                    .appUserRegisterAndLogin(phone, "", etPwd.getText().toString())
                    .compose(bindToLifecycle())
                    .compose(RxSchedulers.normalTrans())
                    .flatMap((Function<LoginResponse, ObservableSource<Object>>) loginResponse ->
                            Observable.create(emitter ->
                                    RongIM.connect(loginResponse.getRongToken(), new RongIMClient.ConnectCallback() {
                                        @Override
                                        public void onTokenIncorrect() {
                                            emitter.tryOnError(new Exception(getString(R.string.connect_failed)));
                                        }

                                        @Override
                                        public void onSuccess(String userid) {
                                            emitter.onNext(loginResponse);
                                        }

                                        @Override
                                        public void onError(RongIMClient.ErrorCode errorCode) {
                                            emitter.tryOnError(new Exception(getString(R.string.connect_failed)));
                                        }
                                    }))
                                    .compose(bindToLifecycle())
                    )
                    .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this, 0)))
                    .subscribe(o -> {
                        LoginResponse loginResponse = (LoginResponse) o;
                        Constant.currentUser = loginResponse;
                        Constant.userId = loginResponse.getId();
                        Constant.token = loginResponse.getToken();
                        Constant.authentication = loginResponse.getIsAuthentication();
                        Constant.currentUser.setIsChinaPhone(isChinaPhone);
                        MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                        MMKVUtils.getInstance().enCode("token", Constant.currentUser.getToken());
                        MMKVUtils.getInstance().enCode("userId", Constant.currentUser.getId());
                        UserInfo userInfo = new UserInfo(Constant.userId, Constant.currentUser.getNick(), Uri.parse(Constant.currentUser.getHeadPortrait()));
                        RongIM.getInstance().setCurrentUserInfo(userInfo);

//                        if ("0".equals(loginResponse.getIsFirstLogin())) {
//                            Intent intent = new Intent(this, SetUpPaymentPwdActivity.class);
//                            intent.putExtra("firstLogin", true);
//                            startActivity(intent);
//                            finish();
//                            return;
//                        }

                        MMKVUtils.getInstance().enCode("isLogin", true);
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    }, this::handleApiError);
        }
    }

    public void email(View view) {
        if (tvLogin.getText().equals(R.string.login)) {
            tv_loginType.setText(R.string.login_byemail);
            tv_pwdLogin.setVisibility(View.VISIBLE);
            llpwd.setVisibility(View.GONE);
            tvLogin.setText(R.string.text_get_code);
            tv_forget_the_password.setVisibility(View.GONE);
        } else {
            startActivity(new Intent(this, NewLoginActivity1.class));
            finish();
        }

    }

    public void pwd(View view) {
        llpwd.setVisibility(View.VISIBLE);
        tvLogin.setText(R.string.login);
        tv_loginType.setText(R.string.code_login);
        tv_pwdLogin.setVisibility(View.GONE);
        tv_forget_the_password.setVisibility(View.VISIBLE);
    }

    private void initView() {
        ivBack = findViewById(R.id.ivBack);
        tvChangeLanguage = findViewById(R.id.tvChangeLanguage);
        llContrary = findViewById(R.id.llContrary);
        tvContrary = findViewById(R.id.tvContrary);
        etPhone = findViewById(R.id.etPhone);
        llpwd = findViewById(R.id.llpwd);
        etPwd = findViewById(R.id.etPwd);
        tvLogin = findViewById(R.id.bt_login);
        tv_loginType = findViewById(R.id.tv_loginType);
        tv_pwdLogin = findViewById(R.id.tv_pwdLogin);
        tv_forget_the_password = findViewById(R.id.tv_forget_the_password);
        ll_code = findViewById(R.id.ll_code);
        etCode = findViewById(R.id.et_code);
        tvCode = findViewById(R.id.tv_code);
        ivIcon = findViewById(R.id.ivIcon);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {
            CountryEntity countryEntity = (CountryEntity) data.getSerializableExtra("country");
            tvContrary.setText(getString(R.string.country_phone, (countryEntity != null ? countryEntity.countryCode : "86")));
            if (countryEntity != null) {
                Constant.HEAD_LOCATION = countryEntity.countryCode;
            }
        }
    }

    public void forgetThePassword(View view) {
        ll_code.setVisibility(View.VISIBLE);
        tvLogin.setText(R.string.m_edit_information_btn);
        llpwd.setVisibility(View.VISIBLE);
        tv_loginType.setVisibility(View.GONE);
        tv_pwdLogin.setVisibility(View.GONE);
        tv_forget_the_password.setVisibility(View.GONE);
        ivIcon.setVisibility(View.INVISIBLE);
        tv1.setText(R.string.find_back_the_password);
        tv2.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("CheckResult")
    public void getCode(View view) {
        String phoneText = etPhone.getText().toString().trim();

        if ("86".equals(tvContrary.getText().toString().substring(1))) {
            isChinaPhone = "1";
            phone = phoneText;
        } else {
            isChinaPhone = "0";
            phone = tvContrary.getText().toString().substring(1) + phoneText;
        }

        if (TextUtils.isEmpty(phone) || ("1".equals(isChinaPhone) && !RegexUtils.isMobileExact(phone))) {
            ToastUtils.showShort(R.string.edit_mobile_tip);
            return;
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .getCode(phone, isChinaPhone)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(o -> {
                    ToastUtils.showShort(R.string.send_successfully);
                }, this::handleApiError);
    }
}
