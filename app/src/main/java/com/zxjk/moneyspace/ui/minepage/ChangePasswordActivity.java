package com.zxjk.moneyspace.ui.minepage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.Api;
import com.zxjk.moneyspace.network.ServiceFactory;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.MD5Utils;

public class ChangePasswordActivity extends BaseActivity {
    private TextView tv_phone;
    private EditText et_code;
    private TextView tv_getCode;
    private EditText et_pwd;
    private EditText et_pwd2;
    private Button btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initView();

        initData();
    }

    private void initView() {
        tv_phone = findViewById(R.id.tv_phone);
        et_code = findViewById(R.id.et_code);
        tv_getCode = findViewById(R.id.tv_getCode);
        et_pwd = findViewById(R.id.et_pwd);
        et_pwd2 = findViewById(R.id.et_pwd2);
        btn_confirm = findViewById(R.id.btn_confirm);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.login_pwd);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        tv_getCode.setOnClickListener(v -> getCode());
        if (TextUtils.isEmpty(Constant.currentUser.getMobile())) {
            tv_phone.setText(Constant.currentUser.getEmail());
        } else {
            tv_phone.setText(Constant.currentUser.getMobile());
        }

        btn_confirm.setOnClickListener(v -> {
            if (TextUtils.isEmpty(Constant.currentUser.getMobile())) {
                setPwd("", Constant.currentUser.getEmail());
            } else {
                setPwd(Constant.currentUser.getMobile(), "");
                tv_phone.setText(Constant.currentUser.getMobile());
            }
        });
    }

    @SuppressLint("CheckResult")
    private void getCode() {
        Api api = ServiceFactory.getInstance().getBaseService(Api.class);
        if (!TextUtils.isEmpty(tv_phone.getText().toString())) {
            if (TextUtils.isEmpty(Constant.currentUser.getMobile())) {
                api.getEmailCode(tv_phone.getText().toString().trim())
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            ToastUtils.showShort(R.string.send_successfully);
                        }, this::handleApiError);
            } else {
                api.getCode(tv_phone.getText().toString(), Constant.currentUser.getIsChinaPhone())
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(o -> {
                            ToastUtils.showShort(R.string.send_successfully);
                        }, this::handleApiError);
            }
        }
    }

    @SuppressLint("CheckResult")
    private void setPwd(String phone, String email) {
        if (TextUtils.isEmpty(et_code.getText().toString())) {
            ToastUtils.showShort(R.string.please_enter_verification_code);
            return;
        } else if (TextUtils.isEmpty(et_pwd.getText().toString()) || TextUtils.isEmpty(et_pwd2.getText().toString())) {
            ToastUtils.showShort(R.string.enter_your_pwd);
            return;
        } else if (et_pwd.getText().toString().length() < 6 || et_pwd2.getText().toString().length() < 6) {
            ToastUtils.showShort(getString(R.string.inconformity1));
        } else if (!et_pwd.getText().toString().equals(et_pwd2.getText().toString())) {
            ToastUtils.showShort(getString(R.string.inconformity));
        }

        ServiceFactory.getInstance().getBaseService(Api.class)
                .updatePassword(phone, email, et_code.getText().toString(), MD5Utils.getMD5(et_pwd.getText().toString()),
                        MD5Utils.getMD5(et_pwd2.getText().toString()))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.normalTrans())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(o -> {
                    ToastUtils.showShort(R.string.password_changed_successfully);
                    finish();
                }, this::handleApiError);
    }
}
