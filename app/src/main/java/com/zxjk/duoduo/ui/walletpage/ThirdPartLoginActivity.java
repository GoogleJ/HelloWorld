package com.zxjk.duoduo.ui.walletpage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class ThirdPartLoginActivity extends BaseActivity {

    public static final String ACTION_THIRDPARTLOGINACCESS = "ThirdPartLoginAccess";

    private String action = "";

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirdpart_login);

        action = getIntent().getStringExtra("action");

    }

    public void login(View view) {

        switch (action) {
            case ACTION_THIRDPARTLOGINACCESS:
                String appId = getIntent().getStringExtra("appId");
                String randomStr = getIntent().getStringExtra("randomStr");
                String sign = getIntent().getStringExtra("sign");
                if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(randomStr) | TextUtils.isEmpty(sign)) {
                    return;
                }
                break;
        }

    }
}
