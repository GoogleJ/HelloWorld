package com.zxjk.moneyspace.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
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

public class NewLoginActivity1 extends BaseActivity {
    private EditText et;
    private LinearLayout llpwd;
    private EditText etPwd;
    private TextView tvLogin;
    private TextView tv_loginType;
    private TextView tv_pwdLogin;
    private String phone;
    private boolean isPwd = true;
    private TextView tv_forget_the_password;

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
        setContentView(R.layout.activity_new_login1);

        et = findViewById(R.id.et);
        llpwd = findViewById(R.id.llpwd);
        etPwd = findViewById(R.id.etPwd);
        tvLogin = findViewById(R.id.bt_login);
        tv_loginType = findViewById(R.id.tv_loginType);
        tv_pwdLogin = findViewById(R.id.tv_pwdLogin);
        tv_forget_the_password = findViewById(R.id.tv_forget_the_password);
    }

    @SuppressLint("CheckResult")
    public void code(View view) {
        if (!"".equals(et.getText().toString().trim())
                && RegexUtils.isEmail(et.getText().toString().trim())) {
            if (tvLogin.getText().equals("获取验证码")) {
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .getEmailCode(et.getText().toString().trim())
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this, 0)))
                        .subscribe(s -> {
                            Intent intent = new Intent(this, GetCodeActivity.class);
                            intent.putExtra("email", et.getText().toString().trim());
                            startActivity(intent);
                        }, this::handleApiError);
            } else {
                ServiceFactory.getInstance().getBaseService(Api.class)
                        .appUserRegisterAndLoginEmail(et.getText().toString().trim(), "", etPwd.getText().toString())
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

        } else {
            ToastUtils.showShort(R.string.input_wrong);
        }
    }

//    public void pwd(View view) {

//    }

    public void pwd(View view){
        llpwd.setVisibility(View.VISIBLE);
        tvLogin.setText("登录");
        tv_loginType.setText("验证码登录");
        tv_pwdLogin.setVisibility(View.GONE);
        tv_forget_the_password.setVisibility(View.VISIBLE);
    }

    public void phone(View view) {
        if (tvLogin.getText().equals("登录")) {
            tv_loginType.setText("邮箱登录");
            tv_pwdLogin.setVisibility(View.VISIBLE);
            llpwd.setVisibility(View.GONE);
            tvLogin.setText("获取验证码");
            tv_forget_the_password.setVisibility(View.GONE);
        } else {
            startActivity(new Intent(this, NewLoginActivity.class));
            finish();
        }
    }

    public void back(View view) {
        finish();
    }

}
