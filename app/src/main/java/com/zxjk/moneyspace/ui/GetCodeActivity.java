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
import com.zxjk.moneyspace.ui.base.BaseActivity;
import com.zxjk.moneyspace.ui.widget.PayPsdInputView;
import com.zxjk.moneyspace.utils.CommonUtils;
import com.zxjk.moneyspace.utils.MMKVUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class GetCodeActivity extends BaseActivity {

    private TextView tvTips1;
    private TextView tvTips2;
    private PayPsdInputView ppivVerify;

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

        //todo login
    }

    @SuppressLint("CheckResult")
    private void connect(String token, boolean equals) {
        Observable.timer(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(c -> CommonUtils.initDialog(this).show());

        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                CommonUtils.destoryDialog();
            }

            @Override
            public void onSuccess(String userid) {
                CommonUtils.destoryDialog();

                MMKVUtils.getInstance().enCode("login", Constant.currentUser);
                MMKVUtils.getInstance().enCode("token", Constant.currentUser.getToken());
                MMKVUtils.getInstance().enCode("userId", Constant.currentUser.getId());
                UserInfo userInfo = new UserInfo(userid, Constant.currentUser.getNick(), Uri.parse(Constant.currentUser.getHeadPortrait()));
                RongIM.getInstance().setCurrentUserInfo(userInfo);

                if (equals) {
                    Intent intent = new Intent(GetCodeActivity.this, SetUpPaymentPwdActivity.class);
                    intent.putExtra("firstLogin", true);
                    startActivity(intent);
                    finish();
                    return;
                }

                MMKVUtils.getInstance().enCode("isLogin", true);
                startActivity(new Intent(GetCodeActivity.this, HomeActivity.class));
                finish();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                ToastUtils.showShort(R.string.connect_failed);
                CommonUtils.destoryDialog();
            }
        });
    }

    public void back(View view) {
        finish();
    }
}
