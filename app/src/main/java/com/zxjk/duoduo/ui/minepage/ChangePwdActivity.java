package com.zxjk.duoduo.ui.minepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.LoginActivity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.MD5Utils;
import com.zxjk.duoduo.utils.MMKVUtils;
import io.rong.imkit.RongIM;

@SuppressLint("CheckResult")
public class ChangePwdActivity extends BaseActivity {

    private EditText etChangePwdOrigin;
    private EditText etChangePwdNew;
    private EditText etChangePwdConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);

        etChangePwdOrigin = findViewById(R.id.etChangePwdOrigin);
        etChangePwdNew = findViewById(R.id.etChangePwdNew);
        etChangePwdConfirm = findViewById(R.id.etChangePwdConfirm);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.change_password));
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
    }


    private void doChangePwd(String oldPwd, String newPwd) {
        ServiceFactory.getInstance().getBaseService(Api.class)
                .updatePwd(oldPwd, newPwd, newPwd)
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(ChangePwdActivity.this)))
                .compose(RxSchedulers.normalTrans())
                .subscribe(response -> {
                    ToastUtils.showShort(R.string.successfully_modified);
                    RongIM.getInstance().logout();
                    MMKVUtils.getInstance().enCode("isLogin", false);
                    Constant.clear();
                    ToastUtils.showShort(R.string.login_out);
                    Intent intent = new Intent(ChangePwdActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }, this::handleApiError);
    }


    private boolean verifyPwd(String pwd) {
        return !TextUtils.isEmpty(pwd) && (pwd.length() >= 6);
    }

    public void accomplish(View view) {
        String s = etChangePwdOrigin.getText().toString().trim();
        String s1 = etChangePwdNew.getText().toString().trim();
        String s2 = etChangePwdConfirm.getText().toString().trim();

        if (!verifyPwd(s)) {
            ToastUtils.showShort(R.string.verify_pwd1);
            return;
        }

        if (!verifyPwd(s1)) {
            ToastUtils.showShort(R.string.verify_pwd2);
            return;
        }

        if (!verifyPwd(s2)) {
            ToastUtils.showShort(R.string.verify_pwd2);
            return;
        }

        if (!s1.equals(s2)) {
            ToastUtils.showShort(R.string.verify_pwd3);
            return;
        }
        doChangePwd(MD5Utils.getMD5(s), MD5Utils.getMD5(s1));
    }
}
