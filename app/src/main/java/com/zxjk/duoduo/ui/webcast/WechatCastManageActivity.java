package com.zxjk.duoduo.ui.webcast;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;

public class WechatCastManageActivity extends BaseActivity {

    private Switch sw1;
    private Switch sw2;
    private Switch sw3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat_cast_manage);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.cast_manage);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        sw1 = findViewById(R.id.sw1);
        sw2 = findViewById(R.id.sw2);
        sw3 = findViewById(R.id.sw3);

    }

    public void closeCast(View view) {

    }
}
