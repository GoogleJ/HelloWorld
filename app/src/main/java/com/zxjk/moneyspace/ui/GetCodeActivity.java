package com.zxjk.moneyspace.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.bean.response.LoginResponse;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.widget.PayPsdInputView;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.MMKVUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class GetCodeActivity extends BaseActivity {

    private TextView tvTips1;
    private TextView tvTips2;
    private PayPsdInputView ppivVerify;
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
        setContentView(R.layout.activity_get_code);

        tvTips1 = findViewById(R.id.tvTips1);
        tvTips2 = findViewById(R.id.tvTips2);
        ppivVerify = findViewById(R.id.ppivVerify);

        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phone");
        if (!TextUtils.isEmpty(email)) {
            tvTips2.setText(email);
            tvTips1.setText(R.string.vercod_email);
        } else if (!TextUtils.isEmpty(phone)) {
            tvTips2.setText(phone);
            tvTips1.setText(R.string.vercod_phone);
        }
    }

    public void next(View view) {
        if (TextUtils.isEmpty(ppivVerify.getPasswordString()) || ppivVerify.getPasswordString().length() != 6) {
            ToastUtils.showShort(R.string.please_enter_verification_code);
            return;
        }

        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phone");

        if (!TextUtils.isEmpty(email)) {
            doLoginByEmail(email);
        } else if (!TextUtils.isEmpty(phone)) {
            doLoginByPhone(phone);
        }
    }

    @SuppressLint("CheckResult")
    private void doLoginByPhone(String phone) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .appUserRegisterAndLogin(phone, ppivVerify.getPasswordString(), "")
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
                    if (!TextUtils.isEmpty(isChinaPhone)) {
                        Constant.currentUser.setIsChinaPhone(isChinaPhone);
                    }
                    MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                    MMKVUtils.getInstance().enCode("token", Constant.currentUser.getToken());
                    MMKVUtils.getInstance().enCode("userId", Constant.currentUser.getId());
                    UserInfo userInfo = new UserInfo(Constant.userId, Constant.currentUser.getNick(), Uri.parse(Constant.currentUser.getHeadPortrait()));
                    RongIM.getInstance().setCurrentUserInfo(userInfo);

                    if ("0".equals(loginResponse.getIsFirstLogin())) {
                        Intent intent = new Intent(GetCodeActivity.this, SetUpPaymentPwdActivity.class);
                        intent.putExtra("firstLogin", true);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    MMKVUtils.getInstance().enCode("isLogin", true);
                    startActivity(new Intent(GetCodeActivity.this, HomeActivity.class));
                    finish();
                }, this::handleApiError);
    }

    @SuppressLint("CheckResult")
    private void doLoginByEmail(String email) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .appUserRegisterAndLoginEmail(email, ppivVerify.getPasswordString(), "")
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

                    MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                    MMKVUtils.getInstance().enCode("token", Constant.currentUser.getToken());
                    MMKVUtils.getInstance().enCode("userId", Constant.currentUser.getId());
                    UserInfo userInfo = new UserInfo(Constant.userId, Constant.currentUser.getNick(), Uri.parse(Constant.currentUser.getHeadPortrait()));
                    RongIM.getInstance().setCurrentUserInfo(userInfo);

                    if ("0".equals(loginResponse.getIsFirstLogin())) {
                        Intent intent = new Intent(GetCodeActivity.this, SetUpPaymentPwdActivity.class);
                        intent.putExtra("firstLogin", true);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    MMKVUtils.getInstance().enCode("isLogin", true);
                    startActivity(new Intent(GetCodeActivity.this, HomeActivity.class));
                    finish();
                }, this::handleApiError);
    }

    public void back(View view) {
        finish();
    }
}
