package com.zxjk.duoduo.ui.minepage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

/**
 * 支付设置
 */
public class PaySettingActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_setting);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.pay_setting));
        //返回
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        //修改支付密码
        findViewById(R.id.rl_changePayPassword).setOnClickListener(v -> {
            if (TextUtils.isEmpty(Constant.currentUser.getIsAuthentication())) {
                ToastUtils.showShort(R.string.verifyfirstpls);
                return;
            }
            if (Constant.currentUser.getIsAuthentication().equals("1")) {
                ToastUtils.showShort(R.string.verifyfirstpls);
                return;
            }
            if (Constant.currentUser.getIsAuthentication().equals("2")) {
                ToastUtils.showShort(R.string.waitAuthentication);
                return;
            }
            startActivity(new Intent(PaySettingActivity.this, RetrievePayPwdActivity.class));
        });
        //找回支付密码
        findViewById(R.id.rl_findPayPassword).setOnClickListener(v ->
                startActivity(new Intent(PaySettingActivity.this, UpdatePayPwdActivity.class)));


    }


}
