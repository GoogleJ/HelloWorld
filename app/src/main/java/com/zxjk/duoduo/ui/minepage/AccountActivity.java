package com.zxjk.duoduo.ui.minepage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.zxjk.duoduo.Constant;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class AccountActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        initView();
    }

    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_DuoDuoNumber = findViewById(R.id.tv_DuoDuoNumber);
        TextView tv_phone = findViewById(R.id.tv_phone);
        tv_title.setText(R.string.account);
        tv_DuoDuoNumber.setText(TextUtils.isEmpty(Constant.currentUser.getDuoduoId())
                ? "" : Constant.currentUser.getDuoduoId());
        tv_phone.setText(Constant.currentUser.getMobile());
        //返回
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());
        //更换手机号1
        findViewById(R.id.rl_changePhoneNumber).setOnClickListener(v ->
                startActivity(new Intent(AccountActivity.this, ChangePhoneActivity.class)));
    }
}
