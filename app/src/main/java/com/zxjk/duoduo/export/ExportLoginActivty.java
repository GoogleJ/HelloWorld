package com.zxjk.duoduo.export;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.ClearableEditTextWithIcon;
import com.zxjk.duoduo.utils.CommonUtils;

import static com.zxjk.duoduo.utils.MD5Utils.getMD5;

public class ExportLoginActivty extends BaseActivity implements View.OnClickListener {

    private ClearableEditTextWithIcon edit_mobile;
    private ClearableEditTextWithIcon edit_password;

    private TextView text_forget_password;
    private TextView text_go_register;

    private TextView tv_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (TextUtils.isEmpty(Constant.phoneUuid)) {
            Constant.phoneUuid =
                    TextUtils.isEmpty(DeviceUtils.getMacAddress()) ? DeviceUtils.getAndroidID() : DeviceUtils.getMacAddress();
        }

        tv_login = findViewById(R.id.tv_login);

        edit_mobile = findViewById(R.id.edit_mobile);
        edit_password = findViewById(R.id.edit_password);

        text_forget_password = findViewById(R.id.text_forget_password);
        text_go_register = findViewById(R.id.text_go_register);
        text_forget_password.setVisibility(View.INVISIBLE);
        text_go_register.setVisibility(View.INVISIBLE);

        tv_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String mobile = edit_mobile.getText().toString().trim();
        String password = edit_password.getText().toString().trim();
        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(password)) {
            ToastUtils.showShort(getString(R.string.edit_mobile_or_password_tip));
            return;
        }
        if (TextUtils.isEmpty(mobile) && "".equals(mobile)) {
            ToastUtils.showShort(getString(R.string.edit_mobile_tip));
            return;
        }
        if (TextUtils.isEmpty(password) || 5 >= password.length() || password.length() >= 14) {
            ToastUtils.showShort(getString(R.string.edit_password_reg));
            return;
        }
        login(mobile, password);
        SPUtils.getInstance().put("mobile", edit_mobile.getText().toString().trim());
    }

    @SuppressLint("CheckResult")
    public void login(String phone, String pwd) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .login(phone, getMD5(pwd))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(loginResponse -> {
                    Constant.token = loginResponse.getToken();
                    Constant.userId = loginResponse.getId();
                    Constant.currentUser = loginResponse;
                    Constant.authentication = loginResponse.getIsAuthentication();

                     if (loginResponse.getIsFirstLogin().equals(Constant.FLAG_FIRSTLOGIN)) {
                        ToastUtils.showShort(R.string.export_firstlogin);
                    } else {
                        getIntent().setClass(this, ExportConfirmOrderActivty.class);
                        startActivity(getIntent());
                        finish();
                    }
                }, this::handleApiError);
    }
}
